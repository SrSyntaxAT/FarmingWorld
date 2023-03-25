package at.srsyntax.farmingworld.farmworld.sign;

import at.srsyntax.farmingworld.FarmingWorldPlugin;
import at.srsyntax.farmingworld.api.event.farmworld.FarmWorldChangeWorldEvent;
import at.srsyntax.farmingworld.api.event.farmworld.player.FarmWorldPlayerEnteredEvent;
import at.srsyntax.farmingworld.api.event.farmworld.player.FarmWorldPlayerLeavingEvent;
import at.srsyntax.farmingworld.api.message.Message;
import at.srsyntax.farmingworld.config.MessageConfig;
import lombok.AllArgsConstructor;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
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
        if (!event.getPlayer().hasPermission("farmingworld.sign")) return;
        final var farmWorld = FarmingWorldPlugin.getApi().getFarmWorld(event.getLine(1));
        if (farmWorld == null) return;
        registry.register((Sign) event.getBlock().getState(), farmWorld);
    }

    @EventHandler
    public void onBlockBreakEvent(BlockBreakEvent event) {
        if (!isSign(event.getBlock())) return;
        registry.unregister(event.getBlock().getLocation());
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
        if (block == null || !block.getType().name().endsWith("_SIGN")) return;
        final var cache = registry.getCache(block.getLocation());
        if (cache == null) return;
        if (!cache.getFarmWorld().isActive()) {
            new Message(messages.getDisabled(), messages.getChatType()).send(event.getPlayer());
        } else if (cache.getFarmWorld().hasPermission(event.getPlayer())) {
            cache.getFarmWorld().teleport(event.getPlayer());
        } else {
            new Message(messages.getNoPermission(), messages.getChatType()).send(event.getPlayer());
        }
    }

    private boolean isSign(Block block) {
        return block.getType().name().endsWith("_SIGN");
    }
}
