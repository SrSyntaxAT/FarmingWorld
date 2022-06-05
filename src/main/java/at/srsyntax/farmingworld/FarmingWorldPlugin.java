package at.srsyntax.farmingworld;

import at.srsyntax.farmingworld.api.API;
import at.srsyntax.farmingworld.api.FarmingWorld;
import at.srsyntax.farmingworld.api.display.DisplayPosition;
import at.srsyntax.farmingworld.api.display.DisplayType;
import at.srsyntax.farmingworld.command.*;
import at.srsyntax.farmingworld.config.FarmingWorldConfig;
import at.srsyntax.farmingworld.config.MessageConfig;
import at.srsyntax.farmingworld.config.PluginConfig;
import at.srsyntax.farmingworld.database.Database;
import at.srsyntax.farmingworld.database.SQLiteDatabase;
import at.srsyntax.farmingworld.listener.ActionBarListeners;
import at.srsyntax.farmingworld.listener.BossBarListeners;
import at.srsyntax.farmingworld.listener.ConfirmListener;
import at.srsyntax.farmingworld.listener.PlayerDataListeners;
import at.srsyntax.farmingworld.runnable.RunnableManager;
import at.srsyntax.farmingworld.util.world.FarmingWorldLoader;
import at.srsyntax.farmingworld.util.ConfirmData;
import at.srsyntax.farmingworld.util.version.VersionCheck;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

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

  @Getter private PluginConfig pluginConfig;
  @Getter private Database database;
  @Getter private final Map<CommandSender, ConfirmData> needConfirm = new ConcurrentHashMap<>();
  private RunnableManager runnableManager;

  @Override
  public void onLoad() {
    checkVersion();
  }

  @Override
  public void onEnable() {
    try {
      pluginConfig = loadConfig();
      this.database = new SQLiteDatabase(this);
      this.database.connect();
      api = new APIImpl(this);
      new Metrics(this, BSTATS_ID);
      loadFarmingWorlds();

      this.runnableManager = new RunnableManager(api, this);
      this.runnableManager.startScheduler();
      registerListeners();
      registerCommands(pluginConfig.getMessage());
    } catch (Exception exception) {
      getLogger().severe("Plugin could not be loaded successfully!");
      exception.printStackTrace();
    }
  }

  @Override
  public void onDisable() {
    this.runnableManager.purge();
    this.database.disconnect();
  }

  private void registerCommands(MessageConfig messageConfig) {
    getCommand("farming").setExecutor(new FarmingCommand(api, this));
    getCommand("teleportfarmingworld").setExecutor(new TeleportFarmingWorldCommand(api, this));
    getCommand("farmingworldadmin").setExecutor(new FarmingWorldAdminCommand(api, this, messageConfig));
  }

  private void registerListeners() {
    final PluginManager pluginManager = getServer().getPluginManager();
    if (pluginConfig.getDisplayPosition() == DisplayPosition.BOSS_BAR)
      pluginManager.registerEvents(new BossBarListeners(api, this), this);
    else if (pluginConfig.getDisplayPosition() == DisplayPosition.ACTION_BAR)
      pluginManager.registerEvents(new ActionBarListeners(api), this);
    pluginManager.registerEvents(new ConfirmListener(this), this);
    pluginManager.registerEvents(new PlayerDataListeners(getDatabase()), this);
  }

  private void loadFarmingWorlds() {
    final FarmingWorldLoader loader = new FarmingWorldLoader(getLogger(), api, this, this.database);
    this.pluginConfig.getFarmingWorlds().forEach(loader::load);
  }

  private void checkVersion() {
    try {
      final VersionCheck check = new VersionCheck(getDescription().getVersion(), RESOURCE_ID);
      if (check.check()) return;
      getLogger().warning("The plugin is no longer up to date, please update the plugin.");
    } catch (Exception ignored) {}
  }

  public void addToBossBar(Player player) {
    final World world = player.getWorld();
    if (!api.isFarmingWorld(world)) return;
    final FarmingWorldConfig farmingWorld = (FarmingWorldConfig) api.getFarmingWorld(world);
    farmingWorld.getDisplayer().checkBossbar(null);
    farmingWorld.getDisplayer().getBossBar().addPlayer(player);
    farmingWorld.updateDisplay(player);
  }

  public void removeFromBossBar(Player player, World world) {
    if (!api.isFarmingWorld(world)) return;
    removeFromBossBar(player, api.getFarmingWorld(world));
  }

  public void removeFromBossBar(Player player, FarmingWorld farmingWorld) {
    ((FarmingWorldConfig) farmingWorld).getDisplayer().removeFromBossBar(player);
  }

  private PluginConfig loadConfig() throws IOException {
    final FarmingWorldConfig farmingWorldTemplate = new FarmingWorldConfig(
        "FarmingWorld",
        "farmingworld.world.farmingworld",
        null,
        null,
        0L,
        4320,
        World.Environment.NORMAL,
        10000,
        0,
        null
    );

    return PluginConfig.load(
        this,
        new PluginConfig(
            getDescription().getVersion(),
            "world",
            false,
            DisplayPosition.BOSS_BAR,
            DisplayType.REMAINING,
            30*60,
            Arrays.asList(Material.LAVA, Material.AIR, Material.WATER),
            BarColor.BLUE,
            farmingWorldTemplate.getName(),
            new ArrayList<>(Collections.singletonList(farmingWorldTemplate)),
            new MessageConfig()
        )
    );
  }
}
