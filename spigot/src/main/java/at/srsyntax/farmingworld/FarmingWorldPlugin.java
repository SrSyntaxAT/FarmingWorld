package at.srsyntax.farmingworld;

import at.srsyntax.farmingworld.api.API;
import at.srsyntax.farmingworld.api.farmworld.FarmWorld;
import at.srsyntax.farmingworld.command.SpawnCommand;
import at.srsyntax.farmingworld.command.admin.AdminCommand;
import at.srsyntax.farmingworld.command.farming.FarmingCommand;
import at.srsyntax.farmingworld.config.Config;
import at.srsyntax.farmingworld.config.MessageConfig;
import at.srsyntax.farmingworld.config.PluginConfig;
import at.srsyntax.farmingworld.database.Database;
import at.srsyntax.farmingworld.database.DatabaseException;
import at.srsyntax.farmingworld.database.sqlite.SQLiteDatabase;
import at.srsyntax.farmingworld.farmworld.*;
import at.srsyntax.farmingworld.farmworld.display.DisplayRegistry;
import at.srsyntax.farmingworld.farmworld.scheduler.FarmWorldScheduler;
import at.srsyntax.farmingworld.farmworld.sign.SignListeners;
import at.srsyntax.farmingworld.farmworld.sign.SignRegistryImpl;
import at.srsyntax.farmingworld.handler.countdown.CountdownListener;
import at.srsyntax.farmingworld.handler.countdown.FarmWorldCountdownRegistry;
import at.srsyntax.farmingworld.safeteleport.SafeTeleportRegistryImpl;
import at.srsyntax.farmingworld.ticket.TicketListener;
import at.srsyntax.farmingworld.util.*;
import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/*
 * MIT License
 *
 * Copyright (c) 2022-2023 Marcel Haberl
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
public class FarmingWorldPlugin extends JavaPlugin {

    private static final int BSTATS_ID = 14550, RESOURCE_ID = 100640;
    @Getter private static API api;

    @Getter private Database database;
    @Getter private PluginConfig pluginConfig;
    @Getter private MessageConfig messageConfig;

    @Getter private FarmWorldCountdownRegistry countdownRegistry;
    @Getter private Economy economy;
    @Getter private CommandRegistry commandRegistry;
    @Getter private SignRegistryImpl signRegistry;
    @Getter private DisplayRegistry displayRegistry;
    @Getter private SafeTeleportRegistryImpl safeTeleportRegistry;

    @Override
    public void onLoad() {
        SpigotVersionCheck.checkWithError(this, RESOURCE_ID, "The plugin is no longer up to date, please update the plugin.");
    }

    @Override
    public void onEnable() {
        try {
            api = new APIImpl(this);
            new Metrics(this, BSTATS_ID);

            loadConfig();

            this.database = new SQLiteDatabase(this);
            this.database.connect();

            this.economy = setupEconomy();

            this.countdownRegistry = new FarmWorldCountdownRegistry();
            this.commandRegistry = new CommandRegistry(getName());
            if (pluginConfig.isSpawnCommandEnabled())
                commandRegistry.register(new SpawnCommand(this));
            this.signRegistry = new SignRegistryImpl(getLogger(), database.getSignRepository());
            this.displayRegistry = new DisplayRegistry(this, pluginConfig.getResetDisplay());
            if (pluginConfig.getSafeTeleport().isEnabled())
                this.safeTeleportRegistry = new SafeTeleportRegistryImpl(this);
            registerListeners(
                    new CountdownListener(countdownRegistry),
                    new PlayerEventListeners(),
                    new SignListeners(signRegistry, messageConfig.getCommand()),
                    new JoinListener(this)
            );
            if (pluginConfig.getTicket().isEnabled())
                registerListeners(new TicketListener(pluginConfig.getTicket()));

            loadFarmWorlds();

            getCommand("farming").setExecutor(new FarmingCommand((APIImpl) api, messageConfig));
            getCommand("fwa").setExecutor(new AdminCommand((APIImpl) api, messageConfig.getAdminCommand()));

        } catch (Exception exception) {
            getLogger().severe("Plugin could not be loaded successfully!");
            exception.printStackTrace();
        }
    }

    public void loadConfig() throws IOException {
        messageConfig = Config.load(this, new MessageConfig(), MessageConfig.class);
        pluginConfig = Config.load(this, new PluginConfig(this, getDefaultFallbackLocation()), PluginConfig.class);
    }

    public void loadFarmWorlds() {
        pluginConfig.getFarmWorlds().forEach(farmWorld -> new FarmWorldLoader(this, farmWorld).load());
        checkFarmWorlds();
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new FarmWorldScheduler(api, this), 120L, 1200L);

        if (pluginConfig.getChunkDeletePeriod() <= 0) return;
        final long period = TimeUnit.HOURS.toSeconds(pluginConfig.getChunkDeletePeriod()) * 20;
        final var chunkDeleterRunnable = new ChunkDeleterRunnable(api, pluginConfig.getChunkDeletePeriod());
        getServer().getScheduler().scheduleSyncRepeatingTask(this, chunkDeleterRunnable, 30L, period);
    }

    private void checkFarmWorlds() {
        database.getFarmWorldRepository().getFarmWorlds().forEach(name -> {
            final FarmWorld farmWorld = api.getFarmWorld(name);

            if (farmWorld == null) {
                database.getFarmWorldRepository().delete(name);
                database.getLocationRepository().deleteByFarmWorldName(name);
                database.getSignRepository().delete(name);
            }

            checkLostWorlds(name, farmWorld);
        });
    }

    private void checkLostWorlds(String name, FarmWorld farmWorld) {
        for (File file : getServer().getWorldContainer().listFiles()) {
            if (!file.isDirectory()) continue;
            if (!file.getName().startsWith(name)) continue;

            if (farmWorld != null) {
                final FarmWorldData data = ((FarmWorldImpl) farmWorld).getData();
                if (data.getCurrentWorldName() != null && file.getName().equalsIgnoreCase(data.getCurrentWorldName())) continue;
                if (data.getNextWorldName() != null && file.getName().equalsIgnoreCase(data.getNextWorldName())) continue;
            }

            getLogger().info("Delete " + file.getName() + " (F)");
            FileUtil.deleteFolder(file);
        }
    }

    private void registerListeners(Listener... listeners) {
        for (Listener listener : listeners)
            Bukkit.getPluginManager().registerEvents(listener, this);
    }

    private Economy setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) return null;
        final RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) return null;
        return rsp.getProvider();
    }

    @Override
    public void onDisable() {
        try {
            this.database.disconnect();
        } catch (DatabaseException e) {
            getLogger().severe("Plugin could not be disabled successfully.");
            e.printStackTrace();
        }
    }

    private Location getDefaultFallbackLocation() throws IOException {
        final Properties properties = new Properties();
        properties.load(new FileReader("server.properties"));
        final String levelName = properties.getProperty("level-name");
        final World world = Bukkit.getWorld(levelName);
        if (world == null) throw new NullPointerException("Default fallback location could not be read.");
        return world.getSpawnLocation();
    }
}
