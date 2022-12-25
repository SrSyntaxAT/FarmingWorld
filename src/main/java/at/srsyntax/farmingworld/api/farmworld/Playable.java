package at.srsyntax.farmingworld.api.farmworld;

import at.srsyntax.farmingworld.api.handler.cooldown.Cooldown;
import at.srsyntax.farmingworld.api.handler.countdown.Countdown;
import at.srsyntax.farmingworld.api.handler.countdown.CountdownCallback;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/*
 * CONFIDENTIAL
 *  Unpublished Copyright (c) 2022 Marcel Haberl, All Rights Reserved.
 *
 * NOTICE:
 * All information contained herein is, and remains the property of Marcel Haberl. The intellectual and
 * technical concepts contained herein are proprietary to Marcel Haberl and may be covered by U.S. and Foreign
 * Patents, patents in process, and are protected by trade secret or copyright law. Dissemination of this
 * information or reproduction of this material is strictly forbidden unless prior written permission is obtained
 * from Marcel Haberl.  Access to the source code contained herein is hereby forbidden to anyone without written
 * permission Confidentiality and Non-disclosure agreements explicitly covering such access.
 *
 * The copyright notice above does not evidence any actual or intended publication or disclosure  of  this source code,
 * which includes information that is confidential and/or proprietary, and is a trade secret, of Marcel Haberl.
 * ANY REPRODUCTION, MODIFICATION, DISTRIBUTION, PUBLIC  PERFORMANCE, OR PUBLIC DISPLAY OF OR THROUGH USE  OF THIS
 * SOURCE CODE  WITHOUT  THE EXPRESS WRITTEN CONSENT OF Marcel Haberl IS STRICTLY PROHIBITED, AND IN VIOLATION OF
 * APPLICABLE LAWS AND INTERNATIONAL TREATIES.  THE RECEIPT OR POSSESSION OF  THIS SOURCE CODE AND/OR RELATED
 * INFORMATION DOES NOT CONVEY OR IMPLY ANY RIGHTS TO REPRODUCE, DISCLOSE OR DISTRIBUTE ITS CONTENTS, OR TO
 * MANUFACTURE, USE, OR SELL ANYTHING THAT IT  MAY DESCRIBE, IN WHOLE OR IN PART.
 */

/**
 * Represents a farm world which can be entered by players.
 */
public interface Playable {

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
