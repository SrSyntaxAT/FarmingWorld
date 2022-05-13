package at.srsyntax.farmingworld.listener;

import at.srsyntax.farmingworld.FarmingWorldPlugin;
import at.srsyntax.farmingworld.api.API;
import at.srsyntax.farmingworld.api.event.ReplacedFarmingWorldEvent;
import at.srsyntax.farmingworld.config.FarmingWorldConfig;
import lombok.AllArgsConstructor;
import org.bukkit.boss.BossBar;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

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
public class BossBarListeners implements Listener {

  private final API api;
  private final FarmingWorldPlugin plugin;

  @EventHandler
  public void onPlayerJoinEvent(PlayerJoinEvent event) {
    plugin.addToBossBar(event.getPlayer());
  }

  @EventHandler
  public void onPlayerQuitEvent(PlayerQuitEvent event) {
    plugin.removeFromBossBar(event.getPlayer(), event.getPlayer().getWorld());
  }

  @EventHandler
  public void onReplacedFarmingWorldEvent(ReplacedFarmingWorldEvent event) {
    final BossBar bossBar = ((FarmingWorldConfig) event.getFarmingWorld()).getDisplayer().getBossBar();
    if (bossBar == null) return;
    bossBar.getPlayers().forEach(player -> {
      if (!event.getWorld().getPlayers().contains(player))
        plugin.removeFromBossBar(player, event.getFarmingWorld());
    });
  }

  @EventHandler
  public void onPlayerChangedWorldEvent(PlayerChangedWorldEvent event) {
    if (api.isFarmingWorld(event.getFrom()))
      plugin.removeFromBossBar(event.getPlayer(), event.getFrom());

    if (api.isFarmingWorld(event.getPlayer().getWorld()))
      plugin.addToBossBar(event.getPlayer());
  }

}
