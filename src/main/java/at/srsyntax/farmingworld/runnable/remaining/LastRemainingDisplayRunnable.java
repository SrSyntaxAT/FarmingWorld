package at.srsyntax.farmingworld.runnable.remaining;

import at.srsyntax.farmingworld.FarmingWorldPlugin;
import at.srsyntax.farmingworld.api.message.Message;
import at.srsyntax.farmingworld.config.FarmingWorldConfig;
import org.bukkit.Bukkit;
import org.bukkit.World;

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
public class LastRemainingDisplayRunnable implements Runnable {

  private final FarmingWorldPlugin plugin;
  private final FarmingWorldConfig farmingWorld;
  private final RemainingRunnable runnable;
  private int taskId;
  
  public LastRemainingDisplayRunnable(FarmingWorldPlugin plugin, FarmingWorldConfig farmingWorld, RemainingRunnable runnable) {
    this.plugin = plugin;
    this.farmingWorld = farmingWorld;
    this.runnable = runnable;
  }

  @Override
  public void run() {
    if (!farmingWorld.isActiv()) {
      runnable.cancelLastRemainingDisplayRunnable(taskId, farmingWorld);
      return;
    }

    if (farmingWorld.needReset())
      resetWorld();
    else
      farmingWorld.updateDisplay();
  }
  
  private void resetWorld() {
    World nextWorld = farmingWorld.getNextWorld();
    if (nextWorld == null)
      nextWorld = FarmingWorldPlugin.getApi().generateFarmingWorld(farmingWorld);
  
    farmingWorld.display(new Message(plugin.getPluginConfig().getMessage().getReset()).replace());
  
    farmingWorld.newWorld(nextWorld);
    farmingWorld.setNextWorld(null);
  
    Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> farmingWorld.updateDisplay(), 40L);
  
    runnable.cancelLastRemainingDisplayRunnable(taskId, farmingWorld);
  }

  public void setTaskId(int taskId) {
    this.taskId = taskId;
  }
}
