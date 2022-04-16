package at.srsyntax.farmingworld.runnable.remaining;

import at.srsyntax.farmingworld.FarmingWorldPlugin;
import at.srsyntax.farmingworld.api.FarmingWorld;
import at.srsyntax.farmingworld.config.FarmingWorldConfig;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
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
public class RemainingRunnable implements Runnable {

  private final FarmingWorldPlugin plugin;

  private final List<FarmingWorld> runnables = new ArrayList<>(3);
  
  public RemainingRunnable(FarmingWorldPlugin plugin) {
    this.plugin = plugin;
  }
  
  @Override
  public void run() {
    plugin.getPluginConfig().getFarmingWorlds().forEach(farmingWorld -> {
      farmingWorld.updateDisplay();

      if (farmingWorld.getRemaining() <= TimeUnit.MINUTES.toMillis(2)) {
        checkNextRunnable(farmingWorld);
        
        if (farmingWorld.getNextWorld() == null)
          Bukkit.getScheduler().runTask(plugin, () -> farmingWorld.setNextWorld(FarmingWorldPlugin.getApi().generateFarmingWorld(farmingWorld)));
      }
    });
  }
  
  private void checkNextRunnable(FarmingWorldConfig farmingWorld) {
    if (this.runnables.contains(farmingWorld)) return;
    this.runnables.add(farmingWorld);
    final LastRemainingDisplayRunnable runnable = new LastRemainingDisplayRunnable(this.plugin, farmingWorld, this);
    final BukkitTask task = this.plugin.getServer().getScheduler().runTaskTimerAsynchronously(this.plugin, runnable, 20L, 20L);
    runnable.setTaskId(task.getTaskId());
  }
  
  public void cancelLastRemainingDisplayRunnable(int taskId, FarmingWorld farmingWorld) {
    Bukkit.getScheduler().cancelTask(taskId);
    this.runnables.remove(farmingWorld);
  }
}
