package at.srsyntax.farmingworld.countdown;

import at.srsyntax.farmingworld.FarmingWorldPlugin;
import at.srsyntax.farmingworld.util.EmptyMetadataValue;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

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
public class Countdown {

  private static final String METADATA_KEY = "fw:cd";

  private final FarmingWorldPlugin plugin;
  private final CountdownCallback callback;
  private final Player player;

  private BukkitTask task;

  public Countdown(FarmingWorldPlugin plugin, CountdownCallback callback, Player player) {
    this.plugin = plugin;
    this.callback = callback;
    this.player = player;
  }

  public void start(int time) {
    final CountdownRunnable runnable = new CountdownRunnable(
        plugin.getPluginConfig().getMessage(),
        this,
        player,
        time
    );
    addMetadata();
    task = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, runnable, 0L, 20L);
  }

  public void finish() {
    if (task != null && !task.isCancelled()) task.cancel();
    callback.done();
    removeMetadata();
  }

  public boolean isActiv() {
    return player.hasMetadata(METADATA_KEY);
  }

  private void addMetadata() {
    player.setMetadata(METADATA_KEY, new EmptyMetadataValue(plugin));
  }

  private void removeMetadata() {
    player.removeMetadata(METADATA_KEY, plugin);
  }
}
