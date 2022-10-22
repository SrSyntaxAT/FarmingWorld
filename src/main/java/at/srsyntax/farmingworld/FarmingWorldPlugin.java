package at.srsyntax.farmingworld;

import at.srsyntax.farmingworld.api.API;
import at.srsyntax.farmingworld.config.ConfigLoader;
import at.srsyntax.farmingworld.config.PluginConfig;
import at.srsyntax.farmingworld.database.Database;
import at.srsyntax.farmingworld.database.DatabaseException;
import at.srsyntax.farmingworld.database.sqlite.SQLiteDatabase;
import at.srsyntax.farmingworld.handler.countdown.CountdownListener;
import at.srsyntax.farmingworld.handler.countdown.CountdownRegistry;
import at.srsyntax.farmingworld.util.SpigotVersionCheck;
import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

/*
 * MIT License
 *
 * Copyright (c) 2022 Marcel Haberl
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

    private Database database;
    @Getter private PluginConfig pluginConfig;

    @Getter private CountdownRegistry countdownRegistry;
    @Getter private Economy economy;

    @Override
    public void onLoad() {
        SpigotVersionCheck.checkWithError(this, RESOURCE_ID, "The plugin is no longer up to date, please update the plugin.");
    }

    @Override
    public void onEnable() {
        try {
            api = new APIImpl(this);
            new Metrics(this, BSTATS_ID);

            this.database = new SQLiteDatabase(this);
            this.database.connect();

            this.pluginConfig = ConfigLoader.load(this, new PluginConfig(this));

            this.economy = setupEconomy();

            this.countdownRegistry = new CountdownRegistry();
            registerListeners(
                    new CountdownListener(countdownRegistry)
            );

        } catch (Exception exception) {
            getLogger().severe("Plugin could not be loaded successfully!");
            exception.printStackTrace();
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

}
