package at.srsyntax.farmingworld.api.farmworld.sign;

import at.srsyntax.farmingworld.api.farmworld.FarmWorld;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
public interface SignRegistry {

    /**
     * Update all signs for all farm worlds.
     */
    void updateAll();

    /**
     * Update all signs of a farm world.
     * @param farmWorld
     */
    void update(@NotNull FarmWorld farmWorld);

    /**
     * Register a sign.
     * @param sign
     * @param farmWorld
     * @return the cache that has been created.
     */
    @Nullable SignCache register(@NotNull Sign sign, @NotNull FarmWorld farmWorld);

    /**
     * Unregister a sign.
     * @param location
     */
    void unregister(@NotNull Location location);

    /**
     * Unregister all signs of a farm world.
     * @param farmWorld from which all signs are to be deleted.
     */
    void unregister(@NotNull FarmWorld farmWorld);

    /**
     * Clear the sign cache
     */
    void unload();

    /**
     * Load all signs from the database.
     */
    void load();

    /**
     * Get a list of all signs cache.
     * @return list of all signs cache
     */
    @NotNull List<SignCache> getCaches();

    /**
     * Get all the signs cache of a farm world.
     * @param farmWorld
     * @return all the signs cache of a farm world
     */
    @NotNull List<SignCache> getCaches(@NotNull FarmWorld farmWorld);

    /**
     * Get the cache of a sign if one is registered.
     * @param location
     * @return null if not present otherwise the cache.
     */
    @Nullable SignCache getCache(@NotNull Location location);
}
