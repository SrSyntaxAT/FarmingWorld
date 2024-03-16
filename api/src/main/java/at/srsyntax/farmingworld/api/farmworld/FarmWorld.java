package at.srsyntax.farmingworld.api.farmworld;

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

import at.srsyntax.farmingworld.api.farmworld.sign.SignOwner;

import java.util.List;

/**
 * Represents the farm world.
 */
public interface FarmWorld extends WorldOwner, Playable, LocationCacher, SignOwner, Template {

    /**
     * Get the name of the farm world
     * @return name of the farm world
     */
    String getName();

    /**
     * Get the permission to enter the world.
     * @return permission to enter the world.
     */
    String getPermission();

    /**
     * Get the time in minutes when the world should be deleted since the world was created.
     * @return time in minutes
     */
    int getTimer();

    /**
     * Get the value if the farm world is activated.
     * @return whether the farm world is activated
     */
    boolean isActive();

    /**
     * Activate or deactivate the farm world.
     * When deactivated, all worlds belonging to the farm world are unloaded and players are teleported to the fallback location.
     * There is no longer a check to see if the world needs to be reset.
     * @param active - whether to enable or disable the farm world
     */
    void setActive(boolean active);

    /**
     * Delete a farm world.
     */
    void delete();

    /**
     * Get the command alias of the farm world.
     * @return a list of aliases
     */
    List<String> getAliases();


    // TODO: 25.06.2023 Add JDocs
    double getPrice();

}
