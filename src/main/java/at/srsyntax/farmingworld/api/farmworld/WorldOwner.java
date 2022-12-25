package at.srsyntax.farmingworld.api.farmworld;

import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
 * Represents a farm world which can own worlds.
 */
public interface WorldOwner {

    /**
     * Get the current world for farming.
     * @return the current world or null should not exist.
     */
    @Nullable World getWorld();

    /**
     * Set the new world for farming.
     * @param world which is now to be farmed
     */
    void newWorld(@Nullable World world);

    /**
     * The new world where farming will take place once the current world is reset.
     * @return the next world for farming.
     */
    @Nullable World getNextWorld();

    /**
     * Sets the next world to farm in when the current world is reset.
     * @param world - the next world for farming.
     */
    void newNextWorld(@Nullable World world);

    /**
     * Delete the next world for farming.
     */
    void deleteNextWorld();

    /**
     * Reset the current world and automatically go to the next farm world.
     */
    void next();

    /**
     * Reset the current world and automatically go to the specified world.
     * @param world - the next world for farming.
     */
    void next(@NotNull World world);

    /**
     * Check if the next farm world has already been pre-generated.
     * @return whether the world is generated
     */
    boolean hasNext();

    /**
     * Generate a new world with the parameters specified for the farm world.
     * @return the newly generated world.
     */
    @NotNull World generateWorld();

    /**
     * Get the enviropment of the farm world.
     * @return the enviropment of the farm world.
     */
    @Nullable World.Environment getEnvironment();

    /**
     * Get the generator for the worlds of the farm world.
     * @return the generator for the worlds.
     */
    @Nullable String getGenerator();

    /**
     * Get the data of the borders for the farm world.
     * @return data of the borders.
     */
    @Nullable Border getBorder();
}
