package at.srsyntax.farmingworld.api;

import at.srsyntax.farmingworld.api.farmworld.FarmWorld;
import at.srsyntax.farmingworld.api.handler.cooldown.Cooldown;
import at.srsyntax.farmingworld.api.handler.countdown.Countdown;
import at.srsyntax.farmingworld.api.handler.countdown.CountdownCallback;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

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

    /**
     * Get a cooldown handler for the player and farm world.
     * @param player on which the cooldown is related.
     * @param farmWorld on which the cooldown is related.
     * @return a cooldown handler.
     */
    Cooldown getCooldown(Player player, FarmWorld farmWorld);

    /**
     * Check if a player currently has a cooldown.
     * @param player on which the cooldown is related.
     * @param farmWorld on which the cooldown is related.
     * @return whether the player currently has a cooldown.
     */
    boolean hasCooldown(Player player, FarmWorld farmWorld);

    /**
     * Returns if Vault Economy is supported.
     * @return if Vault Economy is supported.
     */
    boolean vaultSupported();

    /**
     * Teleport the players to different random location of the farm world.
     * There are no checks to see if the player has to wait for a countdown or cooldown, or has permission to teleport.
     * @param players who are to be teleported.
     */
    void teleport(@NotNull FarmWorld farmWorld, @NotNull Player... players);

    /**
     * Teleport players to the same or different random locations in the farm world.
     * There are no checks to see if the player has to wait for a countdown or cooldown, or has permission to teleport.
     * @param sameLocation - Specifies whether the players should be teleported to the same location.
     * @param players who are to be teleported.
     */
    void teleport(@NotNull FarmWorld farmWorld, boolean sameLocation, @NotNull Player... players);

    /**
     * Teleport the players to different random location of the farm world.
     * There are no checks to see if the player has to wait for a countdown or cooldown, or has permission to teleport.
     * @param players who are to be teleported.
     */
    void teleport(@NotNull FarmWorld farmWorld, @NotNull List<Player> players);

    /**
     * Teleport players to the same or different random locations in the farm world.
     * There are no checks to see if the player has to wait for a countdown or cooldown, or has permission to teleport.
     * @param sameLocation - Specifies whether the players should be teleported to the same location.
     * @param players who are to be teleported.
     */
    void teleport(@NotNull FarmWorld farmWorld, boolean sameLocation, @NotNull List<Player> players);

    /**
     * Generate a new world with the parameters specified for the farm world.
     * @return the newly generated world.
     */
    @NotNull World generateWorld(FarmWorld farmWorld);

    /**
     * Find a farm world with his name.
     * @param name of the farm world.
     * @return the farm world with the name or null if there is no farm world with the name.
     */
    @Nullable FarmWorld getFarmWorld(String name);
}
