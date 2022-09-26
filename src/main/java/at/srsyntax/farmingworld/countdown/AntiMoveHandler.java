package at.srsyntax.farmingworld.countdown;

import at.srsyntax.farmingworld.api.message.Message;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

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
public class AntiMoveHandler implements Listener {

    private final Location location;
    private final Countdown countdown;

    @EventHandler
    public void onPlayerMoveEvent(PlayerMoveEvent event) {
        if (!event.getPlayer().equals(countdown.getPlayer())) return;
        if (!countdown.isActiv()) return;

        final Player player = countdown.getPlayer();
        if (player.getLocation().distance(location) > 0.7D) {
            countdown.cancel();
            final var rawMessage = countdown.getPlugin().getPluginConfig().getMessage().getCountdownCanceledMoved();
            player.sendMessage(new Message(rawMessage).replace());
        }
    }

    public void register() {
        Bukkit.getPluginManager().registerEvents(this, countdown.getPlugin());
    }

    public void unregister() {
        HandlerList.unregisterAll(this);
    }
}
