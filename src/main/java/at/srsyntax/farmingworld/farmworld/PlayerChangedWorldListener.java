package at.srsyntax.farmingworld.farmworld;

import at.srsyntax.farmingworld.FarmingWorldPlugin;
import at.srsyntax.farmingworld.api.API;
import at.srsyntax.farmingworld.api.event.farmworld.player.FarmWorldPlayerEnteredEvent;
import at.srsyntax.farmingworld.api.event.farmworld.player.FarmWorldPlayerEvent;
import at.srsyntax.farmingworld.api.event.farmworld.player.FarmWorldPlayerLeavingEvent;
import at.srsyntax.farmingworld.api.farmworld.FarmWorld;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

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
public class PlayerChangedWorldListener implements Listener {

    private final API api = FarmingWorldPlugin.getApi();

    @EventHandler
    public void onPlayerChangedWorldEvent(PlayerChangedWorldEvent event) {
        final var player = event.getPlayer();
        final FarmWorld from, to;
        from = api.getFarmWorld(event.getFrom());
        to = api.getFarmWorld(player.getWorld());

        if (from != null && to == null)
            FarmWorldPlayerEvent.call(FarmWorldPlayerLeavingEvent.class, from, player);
        else if (from == null && to != null)
            FarmWorldPlayerEvent.call(FarmWorldPlayerEnteredEvent.class, to, player);
        else if (from != null && to != null) {
            if (!from.equals(to)) {
                FarmWorldPlayerEvent.call(FarmWorldPlayerLeavingEvent.class, from, player);
                FarmWorldPlayerEvent.call(FarmWorldPlayerEnteredEvent.class, to, player);
            }
        }
    }
}
