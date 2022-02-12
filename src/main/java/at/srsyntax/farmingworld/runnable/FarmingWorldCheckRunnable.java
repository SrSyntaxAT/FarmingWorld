package at.srsyntax.farmingworld.runnable;

import at.srsyntax.farmingworld.FarmingWorldPlugin;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

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
@AllArgsConstructor
public class FarmingWorldCheckRunnable implements Runnable {

  private final FarmingWorldPlugin plugin;

  @Override
  public void run() {
    plugin.getPluginConfig().getFarmingWorlds().forEach(farmingWorld -> {
      farmingWorld.updateRemainingDisplay();

      if (farmingWorld.getRemaining() <= TimeUnit.MINUTES.toMillis(2)) {
        final LastRemainingDisplayRunnable runnable = new LastRemainingDisplayRunnable(plugin, farmingWorld);
        final BukkitTask task = plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, runnable, 20L, 20L);
        runnable.setTask(task);

        if (farmingWorld.getNextWorld() == null)
          Bukkit.getScheduler().runTask(plugin, () -> farmingWorld.setNextWorld(FarmingWorldPlugin.getApi().generateFarmingWorld(farmingWorld)));
      }
    });
  }
}
