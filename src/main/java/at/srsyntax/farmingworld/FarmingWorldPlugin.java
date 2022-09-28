package at.srsyntax.farmingworld;

import at.srsyntax.farmingworld.api.API;
import at.srsyntax.farmingworld.api.FarmingWorld;
import at.srsyntax.farmingworld.api.display.DisplayPosition;
import at.srsyntax.farmingworld.api.display.DisplayType;
import at.srsyntax.farmingworld.command.FarmingCommand;
import at.srsyntax.farmingworld.command.FarmingWorldAdminCommand;
import at.srsyntax.farmingworld.command.SpawnCommand;
import at.srsyntax.farmingworld.command.TeleportFarmingWorldCommand;
import at.srsyntax.farmingworld.config.FarmingWorldConfig;
import at.srsyntax.farmingworld.config.MessageConfig;
import at.srsyntax.farmingworld.config.PluginConfig;
import at.srsyntax.farmingworld.config.SpawnConfig;
import at.srsyntax.farmingworld.database.Database;
import at.srsyntax.farmingworld.database.SQLiteDatabase;
import at.srsyntax.farmingworld.listener.*;
import at.srsyntax.farmingworld.registry.CommandRegistry;
import at.srsyntax.farmingworld.runnable.RunnableManager;
import at.srsyntax.farmingworld.util.ConfirmData;
import at.srsyntax.farmingworld.util.location.LocationCache;
import at.srsyntax.farmingworld.util.version.VersionCheck;
import at.srsyntax.farmingworld.util.world.FarmingWorldLoader;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.FileReader;
import java.io.IOException;
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
  @Getter private CommandRegistry commandRegistry;

  @Override
  public void onLoad() {
    checkVersion();
  }

  @Override
  public void onEnable() {
    try {
      api = new APIImpl(this);
      new Metrics(this, BSTATS_ID);

      pluginConfig = loadConfig();
      this.database = new SQLiteDatabase(this);
      this.database.connect();

      this.commandRegistry = new CommandRegistry(getName());

      loadFarmingWorlds();

      this.runnableManager = new RunnableManager(api, this);
      this.runnableManager.startScheduler();
      registerListeners();
      registerCommands();
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

  private void registerCommands() {
    this.commandRegistry.register(
            new FarmingCommand("farming", api, this),
            new TeleportFarmingWorldCommand("teleportfarmingworld", api, this),
            new FarmingWorldAdminCommand("farmingworldadmin", api, this),
            new SpawnCommand("spawn", this)
    );
  }

  private void registerListeners() {
    final PluginManager pluginManager = getServer().getPluginManager();
    if (pluginConfig.getDisplayPosition() == DisplayPosition.BOSS_BAR)
      pluginManager.registerEvents(new BossBarListeners(api, this), this);
    else if (pluginConfig.getDisplayPosition() == DisplayPosition.ACTION_BAR)
      pluginManager.registerEvents(new ActionBarListeners(api), this);
    pluginManager.registerEvents(new ConfirmListener(this), this);
    pluginManager.registerEvents(new PlayerDataListeners(getDatabase()), this);
    pluginManager.registerEvents(new JoinListener(this), this);
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

    final SpawnConfig spawn = new SpawnConfig(
            true,
            true,
            3,
            new LocationCache(getDefaultSpawnLocation())
    );

    return PluginConfig.load(
        this,
        new PluginConfig(
            getDescription().getVersion(),
            spawn,
            false,
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

  private Location getDefaultSpawnLocation() throws IOException {
    final World world = Bukkit.getWorld(((APIImpl) api).readServerPropertiesWorldName());
    if (world == null)
      return new Location(null, 0, 100, 0);
    return world.getSpawnLocation();
  }
}
