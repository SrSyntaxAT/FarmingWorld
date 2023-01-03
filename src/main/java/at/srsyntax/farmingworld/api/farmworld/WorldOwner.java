package at.srsyntax.farmingworld.api.farmworld;

import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    /**
     * Check if the farm world needs to be reset.
     * @return whether the farm world needs to be reset.
     */
    boolean needReset();

    /**
     * Checks if it is necessary to generate the next farm world.
     * @return whether it is necessary to generate the next farm world.
     */
    boolean needNextWorld();

}
