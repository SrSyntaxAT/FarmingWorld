package at.srsyntax.farmingworld.safeteleport;

import at.srsyntax.farmingworld.api.event.farmworld.player.FarmWorldPlayerEnteredEvent;
import at.srsyntax.farmingworld.api.event.farmworld.player.FarmWorldPlayerLeavingEvent;
import at.srsyntax.farmingworld.api.handler.countdown.exception.CanceledException;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

/*
 * MIT License
 *
 * Copyright (c) 2022-2023 Marcel Haberl
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
public class SafeTeleportListener implements Listener {

    private final SafeTeleportRegistryImpl registry;
    private final boolean canDamagePlayers;

    @EventHandler
    public void onFarmWorldPlayerEnteredEvent(FarmWorldPlayerEnteredEvent event) {
        registry.register(event.getPlayer());
    }

    @EventHandler
    public void onFarmWorldPlayerLeavingEvent(FarmWorldPlayerLeavingEvent event) {
        registry.unregister(event.getPlayer(), CanceledException.Result.QUIT);
    }

    @EventHandler
    public void onEntityDamageEvent(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        event.setCancelled(registry.isInvulnerable(player));
    }

    @EventHandler
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player damager)) return;
        if (!(event.getEntity() instanceof Player)) return;
        if (!registry.isInvulnerable(damager)) return;
        event.setCancelled(!canDamagePlayers);
    }
}
