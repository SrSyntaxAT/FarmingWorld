package at.srsyntax.farmingworld.farmworld.display;

import at.srsyntax.farmingworld.api.event.farmworld.FarmWorldChangeWorldEvent;
import at.srsyntax.farmingworld.api.event.farmworld.player.FarmWorldPlayerEnteredEvent;
import at.srsyntax.farmingworld.api.event.farmworld.player.FarmWorldPlayerLeavingEvent;
import lombok.AllArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

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
public class DisplayerListeners implements Listener {

    private final DisplayRegistry registry;

    @EventHandler
    public void onFarmWorldPlayerEnteredEvent(FarmWorldPlayerEnteredEvent event) {
        final var displayer = registry.getDisplayer(event.getFarmWorld());
        if (displayer != null)
            displayer.addPlayer(event.getPlayer());
    }

    @EventHandler
    public void onFarmWorldPlayerLeavingEvent(FarmWorldPlayerLeavingEvent event) {
        final var displayer = registry.getDisplayer(event.getFarmWorld());
        if (displayer != null)
            displayer.removePlayer(event.getPlayer());
    }

    @EventHandler
    public void onFarmWorldChangeWorldEvent(FarmWorldChangeWorldEvent event) {
        final var displayer = registry.getDisplayer(event.getFarmWorld());
        if (displayer != null)
            displayer.createBossBar();
    }
}
