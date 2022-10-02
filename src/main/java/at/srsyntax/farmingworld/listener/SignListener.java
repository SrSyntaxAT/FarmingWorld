package at.srsyntax.farmingworld.listener;

import at.srsyntax.farmingworld.FarmingWorldPlugin;
import at.srsyntax.farmingworld.api.FarmingWorld;
import at.srsyntax.farmingworld.api.exception.TeleportFarmingWorldException;
import at.srsyntax.farmingworld.api.message.Message;
import at.srsyntax.farmingworld.api.message.MessageBuilder;
import at.srsyntax.farmingworld.config.FarmingWorldConfig;
import at.srsyntax.farmingworld.config.SignConfig;
import at.srsyntax.farmingworld.config.SpawnConfig;
import at.srsyntax.farmingworld.sign.SignCache;
import at.srsyntax.farmingworld.sign.SignRegistry;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.sql.SQLException;

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
public class SignListener implements Listener {

    private final SignRegistry registry;

    @EventHandler
    public void onSignChangeEvent(SignChangeEvent event) {
        final Player player = event.getPlayer();
        if (!(player.hasPermission("farmingworld.admin.*") || player.hasPermission("farmingworld.admin.sign"))) return;
        if (event.getLines().length < 2) return;
        if (!event.getLines()[0].equalsIgnoreCase("[fw]")) return;

        final FarmingWorld farmingWorld = FarmingWorldPlugin.getApi().getFarmingWorld(event.getLines()[1]);
        if (farmingWorld == null) return;

        final Location location = event.getBlock().getLocation();
        final SignCache cache = new SignCache((FarmingWorldConfig) farmingWorld, location);

        try {
            if (registry.getCaches().containsKey(location)) {
                registry.delete(cache);
                registry.getCaches().replace(location, cache);
            } else {
                registry.getCaches().put(location, cache);
            }

            registry.save(cache);
            cache.farmingWorld().replaceSignLines((Sign) event.getBlock().getState());
        } catch (SQLException exception) {
            player.sendMessage(exception.getMessage());
        }
    }

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        final Block block = event.getClickedBlock();
        if (block == null) return;

        if (block.getState() instanceof Sign) {
            final SignCache cache = registry.get(block.getLocation());
            if (cache == null || event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

            try {
                cache.farmingWorld().teleport(event.getPlayer());
            } catch (TeleportFarmingWorldException e) {
                event.getPlayer().sendMessage(e.getMessage());
            }
        }
    }
}