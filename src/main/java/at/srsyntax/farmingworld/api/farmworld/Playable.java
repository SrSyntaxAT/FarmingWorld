package at.srsyntax.farmingworld.api.farmworld;

import at.srsyntax.farmingworld.api.handler.cooldown.Cooldown;
import at.srsyntax.farmingworld.api.handler.countdown.Countdown;
import at.srsyntax.farmingworld.api.handler.countdown.CountdownCallback;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

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

/**
 * Represents a farm world which can be entered by players.
 */
public interface Playable {

    /**
     * Get all the players that are currently on the farm world.
     * @return all the players that are currently on the farm world.
     */
    List<Player> getPlayers();

    /**
     * Check if the player has the permission to enter the farm world.
     * @param player which should be checked.
     * @return the value whether the player has permission.
     */
    boolean hasPermission(@NotNull Player player);

    /**
     * Teleport the players to different random location of the farm world.
     * There are no checks to see if the player has to wait for a countdown or cooldown, or has permission to teleport.
     * @param players who are to be teleported.
     */
    void teleport(@NotNull Player... players);

    /**
     * Teleport players to the same or different random locations in the farm world.
     * There are no checks to see if the player has to wait for a countdown or cooldown, or has permission to teleport.
     * @param sameLocation - Specifies whether the players should be teleported to the same location.
     * @param players who are to be teleported.
     */
    void teleport(boolean sameLocation, @NotNull Player... players);
    /**
     * Teleport the players to different random location of the farm world.
     * There are no checks to see if the player has to wait for a countdown or cooldown, or has permission to teleport.
     * @param players who are to be teleported.
     */
    void teleport(@NotNull List<Player> players);
    /**
     * Teleport players to the same or different random locations in the farm world.
     * There are no checks to see if the player has to wait for a countdown or cooldown, or has permission to teleport.
     * @param sameLocation - Specifies whether the players should be teleported to the same location.
     * @param players who are to be teleported.
     */
    void teleport(boolean sameLocation, @NotNull List<Player> players);

    /**
     * Returns the cooldown time for the farm world.
     * @return the cooldown time for the farm world.
     */
    int getCooldown();

    /**
     * Get a cooldown handler for the player.
     * @param player on which the cooldown is related.
     * @return a cooldown handler.
     */
    Cooldown getCooldown(@NotNull Player player);

    /**
     * Get the countdown which expires until the player can teleport.
     * @return the time in seconds.
     */
    int getCountdown();

    /**
     * Get a countdown handler for the player.
     * @param player on which the countdown is related.
     * @param callback which is to be called when the countdown has expired.
     * @return a countdown handler.
     */
    Countdown getCountdown(@NotNull Player player, @NotNull CountdownCallback callback);
}
