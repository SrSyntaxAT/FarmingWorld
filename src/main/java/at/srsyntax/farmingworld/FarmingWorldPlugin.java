package at.srsyntax.farmingworld;

import at.srsyntax.farmingworld.api.API;
import at.srsyntax.farmingworld.api.DisplayPosition;
import at.srsyntax.farmingworld.api.DisplayType;
import at.srsyntax.farmingworld.command.*;
import at.srsyntax.farmingworld.config.FarmingWorldConfig;
import at.srsyntax.farmingworld.config.MessageConfig;
import at.srsyntax.farmingworld.config.PluginConfig;
import at.srsyntax.farmingworld.listener.ActionBarListeners;
import at.srsyntax.farmingworld.listener.BossBarListeners;
import at.srsyntax.farmingworld.listener.ConfirmListener;
import at.srsyntax.farmingworld.runnable.RunnableManager;
import at.srsyntax.farmingworld.util.FarmingWorldLoader;
import at.srsyntax.farmingworld.util.ResetData;
import at.srsyntax.farmingworld.util.VersionCheck;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

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
  @Getter private final Map<CommandSender, ResetData> needConfirm = new ConcurrentHashMap<>();
  private RunnableManager runnableManager;

  @Override
  public void onLoad() {
    checkVersion();
  }

  @Override
  public void onEnable() {
    try {
      pluginConfig = loadConfig();
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
  }

  private void registerCommands(MessageConfig messageConfig) {
    getCommand("farming").setExecutor(new FarmingCommand(api, this));
    getCommand("farmingworldinfo").setExecutor(new FarmingWorldInfoCommand(api, messageConfig));
    getCommand("farmingworldreset").setExecutor(new FarmingWorldResetCommand(api, this, messageConfig));
    getCommand("teleportfarmingworld").setExecutor(new TeleportFarmingWorldCommand(api, this));
    getCommand("farmingworldadmin").setExecutor(new FarmingWorldAdminCommand(api, this, messageConfig));
  }

  private void registerListeners() {
    final PluginManager pluginManager = getServer().getPluginManager();
    if (pluginConfig.getDisplayPosition() == DisplayPosition.BOSS_BAR)
      pluginManager.registerEvents(new BossBarListeners(api), this);
    else if (pluginConfig.getDisplayPosition() == DisplayPosition.ACTION_BAR)
      pluginManager.registerEvents(new ActionBarListeners(api), this);
    pluginManager.registerEvents(new ConfirmListener(this), this);
  }

  private void loadFarmingWorlds() {
    final FarmingWorldLoader loader = new FarmingWorldLoader(getLogger(), api, this);
    this.pluginConfig.getFarmingWorlds().forEach(loader::load);
  }

  private void checkVersion() {
    try {
      final VersionCheck check = new VersionCheck(getDescription().getVersion(), RESOURCE_ID);
      if (check.check()) return;
      getLogger().warning("The plugin is no longer up to date, please update the plugin.");
    } catch (Exception ignored) {}
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
            "world",
            DisplayPosition.BOSS_BAR,
            DisplayType.REMAINING,
            30*60,
            Arrays.asList(Material.LAVA, Material.AIR, Material.WATER),
            BarColor.BLUE,
            farmingWorldTemplate.getName(),
            Collections.singletonList(farmingWorldTemplate),

            new MessageConfig(
                "&eFarming worlds&8: <list>",
                "&cYou have no rights to do that!",
                "&cFarming world not found!",
                "&cUsage&8:&f /<usage>",
                "&cPlayer not found!",
                "&cThe player does not have the rights to be teleported to the farmworld!",
                "&e<player> &awas teleported to farmworld &e<farmingworld>&a.",
                "&4The world is reset.",
                "&4Reset in &e<remaining>",
                "second", "seconds",
                "minute", "minutes",
                "hour", "hours",
                "day", "days",
              "&cNo worlds found!",
              "dd.MM.yyyy - HH:mm:ss",
                "&aFarming world has been reset.",
                "&cYou didn't want to reset a world, so you can't confirm anything.",
                "&cThe time to confirm has expired.",
                "&fConfirm your intention in the next &a10 seconds&f with the command \"&a/fwr confirm&f\"."
            )
        )
    );
  }
}
