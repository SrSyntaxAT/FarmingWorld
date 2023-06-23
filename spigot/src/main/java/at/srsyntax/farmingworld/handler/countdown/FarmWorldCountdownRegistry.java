package at.srsyntax.farmingworld.handler.countdown;

import at.srsyntax.farmingworld.api.handler.countdown.Countdown;
import at.srsyntax.farmingworld.api.handler.countdown.CountdownRegistry;
import at.srsyntax.farmingworld.safeteleport.SafeTeleportCountdown;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
public class FarmWorldCountdownRegistry implements CountdownRegistry {

    private final Map<Player, Countdown> countdowns = new ConcurrentHashMap<>();

    public void register(Countdown countdown) {
        if (countdown instanceof SafeTeleportCountdown) return;
        countdowns.put(countdown.getPlayer(), countdown);
    }

    public void unregister(Player player) {
        countdowns.remove(player);
    }

    public Countdown getCountdown(Player player) {
        return countdowns.get(player);
    }

    public boolean hasCountdown(Player player) {
        return countdowns.containsKey(player);
    }
}
