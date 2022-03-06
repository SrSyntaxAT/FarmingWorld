package at.srsyntax.farmingworld;

import at.srsyntax.farmingworld.api.API;
import at.srsyntax.farmingworld.api.RemainingDisplay;
import at.srsyntax.farmingworld.command.FarmingCommand;
import at.srsyntax.farmingworld.command.FarmingWorldInfoCommand;
import at.srsyntax.farmingworld.config.FarmingWorldConfig;
import at.srsyntax.farmingworld.config.LocationConfig;
import at.srsyntax.farmingworld.config.MessageConfig;
import at.srsyntax.farmingworld.config.PluginConfig;
import at.srsyntax.farmingworld.runnable.FarmingWorldCheckRunnable;
import lombok.Getter;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

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

  private static final int BSTATS_ID = 14550;
  
  @Getter private static API api;

  @Getter private PluginConfig pluginConfig;

  @Override
  public void onEnable() {
    try {
      pluginConfig = loadConfig();
      api = new APIImpl(this);
      new Metrics(this, BSTATS_ID);
      loadFarmingWorlds();
      startScheduler();

      if (pluginConfig.getRemainingDisplay() == RemainingDisplay.BOSS_BAR)
        getServer().getPluginManager().registerEvents(new PlayerListeners(api), this);

      getCommand("farming").setExecutor(new FarmingCommand(api, this));
      getCommand("farmingworldinfo").setExecutor(new FarmingWorldInfoCommand(api, this.pluginConfig.getMessage()));

    } catch (Exception exception) {
      getLogger().severe("Plugin could not be loaded successfully!");
      exception.printStackTrace();
    }
  }

  @Override
  public void onDisable() {
    try {
      pluginConfig.save(this);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void startScheduler() {
    final Runnable runnable = new FarmingWorldCheckRunnable(this);
    getServer().getScheduler().runTaskTimerAsynchronously(this, runnable, 90L, 1200L);
  }

  private void loadFarmingWorlds() {
    pluginConfig.getFarmingWorlds().forEach(farmingWorld -> {
      farmingWorld.setPlugin(this);
      checkCurrentWorld(farmingWorld);
      checkNextWorld(farmingWorld);
    });
  }

  private void checkCurrentWorld(FarmingWorldConfig farmingWorld) {
    if (farmingWorld.getCurrentWorldName() == null) {
      farmingWorld.newWorld(api.generateFarmingWorld(farmingWorld));
    } else {
      api.loadFarmingWorld(farmingWorld.getCurrentWorldName(), farmingWorld.getEnvironment());
    }
  }

  private void checkNextWorld(FarmingWorldConfig farmingWorld) {
    if (farmingWorld.getNextWorldName() == null) {
      final long remaining = farmingWorld.getReset() - System.currentTimeMillis();
      if (farmingWorld.needReset() || (remaining <= TimeUnit.MINUTES.toMillis(5) && remaining > 0)) {
        farmingWorld.setNextWorld(api.generateFarmingWorld(farmingWorld));
      }
    } else {
      api.loadFarmingWorld(farmingWorld.getNextWorldName(), farmingWorld.getEnvironment());
    }
  }

  private PluginConfig loadConfig() throws IOException {
    final FarmingWorldConfig farmingWorldTemplate = new FarmingWorldConfig(
        "FarmingWorld",
        "farmingworld.world.farmingworld",
        null,
        null,
        0L,
      4320,
        World.Environment.NORMAL
    );

    return PluginConfig.load(
        this,
        new PluginConfig(
            new LocationConfig("world", 0D, 100D, 0D, (short) 0, (short) 0),
            5000,
            RemainingDisplay.BOSS_BAR,
            BarColor.BLUE,
            farmingWorldTemplate.getName(),
            Collections.singletonList(farmingWorldTemplate),

            new MessageConfig(
                "&eFarming world&8: <list>",
                "&cYou have no rights to do that!",
                "&cFarming world not found!",
                "&4The world is reset.",
                "&4Reset in &e<remaining>",
                "second", "seconds",
                "minute", "minutes",
                "hour", "hours",
                "day", "days",
              "&cNo worlds found!",
              "dd.MM.yyyy - HH:mm:ss"
            )
        )
    );
  }
}
