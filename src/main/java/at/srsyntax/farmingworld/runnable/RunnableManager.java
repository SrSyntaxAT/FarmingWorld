package at.srsyntax.farmingworld.runnable;

import at.srsyntax.farmingworld.FarmingWorldPlugin;
import at.srsyntax.farmingworld.api.API;
import at.srsyntax.farmingworld.api.display.DisplayType;
import at.srsyntax.farmingworld.runnable.date.DateCheckRunnable;
import at.srsyntax.farmingworld.runnable.date.DateDisplayRunnable;
import at.srsyntax.farmingworld.runnable.date.DateRunnable;
import at.srsyntax.farmingworld.runnable.remaining.RemainingRunnable;
import org.bukkit.Bukkit;

import java.util.Date;
import java.util.Timer;
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
public class RunnableManager {

  private final API api;
  private final FarmingWorldPlugin plugin;

  private Timer timer;

  public RunnableManager(API api, FarmingWorldPlugin plugin) {
    this.api = api;
    this.plugin = plugin;
  }

  public void startScheduler() {
    if (plugin.getPluginConfig().getDisplayType() == DisplayType.REMAINING)
      startRemainingScheduler();
    else
      startDateScheduler();

    final long refresh = plugin.getPluginConfig().getDateRefresh();
    if (refresh > 0) {
      final long time = refresh * 20L;
      Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, new DateDisplayRunnable(api), time, time);
    }
  }

  public void purge() {
    if (this.timer != null)
      this.timer.purge();
  }

  private void startRemainingScheduler() {
    final Runnable runnable = new RemainingRunnable(plugin);
    Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, runnable, 90L, 1200L);
  }

  private void startDateScheduler() {
    this.timer = new Timer();
    api.getFarmingWorlds().forEach(farmingWorld -> {
      final Date checkDate = new Date(farmingWorld.getReset() - TimeUnit.SECONDS.toMillis(5));
      this.timer.schedule(new DateCheckRunnable(api, farmingWorld), checkDate);
      this.timer.schedule(new DateRunnable(api, farmingWorld), new Date(farmingWorld.getReset()));

      farmingWorld.updateDisplay();
    });
  }
}
