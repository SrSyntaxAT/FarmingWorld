package at.srsyntax.farmingworld.farmworld.sign;

import at.srsyntax.farmingworld.FarmingWorldPlugin;
import at.srsyntax.farmingworld.api.event.farmworld.FarmWorldChangeWorldEvent;
import at.srsyntax.farmingworld.api.event.farmworld.player.FarmWorldPlayerEnteredEvent;
import at.srsyntax.farmingworld.api.event.farmworld.player.FarmWorldPlayerLeavingEvent;
import at.srsyntax.farmingworld.api.farmworld.sign.SignCache;
import at.srsyntax.farmingworld.api.message.Message;
import at.srsyntax.farmingworld.config.MessageConfig;
import lombok.AllArgsConstructor;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

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
public class SignListeners implements Listener {

    private final SignRegistryImpl registry;
    private final MessageConfig.CommandMessages messages;

    @EventHandler
    public void onSignChangeEvent(SignChangeEvent event) {
        if (!isSign(event.getBlock())) return;
        if (!event.getLine(0).equalsIgnoreCase(SignRegistryImpl.SIGN_TITLE)) return;
        if (!hasSignPermission(event.getPlayer())) return;
        final var farmWorld = FarmingWorldPlugin.getApi().getFarmWorld(event.getLine(1));
        if (farmWorld == null) return;
        registry.register((Sign) event.getBlock().getState(), farmWorld);
    }

    private boolean hasSignPermission(Player player) {
        return player.hasPermission("farmingworld.sign") || player.hasPermission("farmingworld.admin");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreakEvent(BlockBreakEvent event) {
        final var block = event.getBlock();
        if (!isSign(block)) return;
        if (!registry.isTeleportSign(block.getLocation())) return;

        if (hasSignPermission(event.getPlayer())) {
            registry.unregister(block.getLocation());
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onFarmWorldPlayerEnteredEvent(FarmWorldPlayerEnteredEvent event) {
        event.getFarmWorld().updateSigns();
    }

    @EventHandler
    public void onFarmWorldPlayerLeavingEvent(FarmWorldPlayerLeavingEvent event) {
        event.getFarmWorld().updateSigns();
    }

    @EventHandler
    public void onFarmWorldChangeWorldEvent(FarmWorldChangeWorldEvent event) {
        event.getFarmWorld().updateSigns();
    }

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        if (!event.getAction().name().endsWith("_BLOCK")) return;
        final var block = event.getClickedBlock();
        if (!isSign(block)) return;
        final var cache = registry.getCache(block.getLocation());
        if (cache == null) return;
        event.setCancelled(true);

        final var player = event.getPlayer();
        if (hasSignPermission(player) && event.getAction() == Action.LEFT_CLICK_BLOCK && player.getGameMode() == GameMode.CREATIVE) {
            registry.unregister(event.getClickedBlock().getLocation());
        } else {
            teleportPlayer(cache, event.getPlayer());
        }
    }

    private void teleportPlayer(SignCache cache, Player player) {
        if (!cache.getFarmWorld().isActive()) {
            new Message(messages.getDisabled(), messages.getChatType()).send(player);
        } else if (cache.getFarmWorld().hasPermission(player)) {
            cache.getFarmWorld().teleport(player);
        } else {
            new Message(messages.getNoPermission(), messages.getChatType()).send(player);
        }
    }

    private boolean isSign(Block block) {
        return block != null && block.getType().name().endsWith("_SIGN");
    }
}
