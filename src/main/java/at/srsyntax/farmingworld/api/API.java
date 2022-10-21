package at.srsyntax.farmingworld.api;

import at.srsyntax.farmingworld.api.handler.countdown.Countdown;
import at.srsyntax.farmingworld.api.handler.countdown.CountdownCallback;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

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

/**
 * Represents the plugin interface
 */
public interface API {

    /**
     * Get a countdown handler for the player.
     * @param player on which the countdown is related.
     * @param callback - To be notified when the countdown has ended.
     * @return the current countdown or a new countdown if there is no current countdown.
     */
    Countdown getCountdown(@NotNull Player player, @NotNull CountdownCallback callback);

    /**
     * Check if a player has a countdown running.
     * @param player to which the query refers.
     * @return whether it has a countdown running.
     */
    boolean hasCountdown(Player player);
}
