package at.srsyntax.farmingworld.farmworld;

import at.srsyntax.farmingworld.api.farmworld.Border;
import at.srsyntax.farmingworld.api.farmworld.FarmWorld;
import at.srsyntax.farmingworld.api.farmworld.LocationRandomizer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

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
public class LocationRandomizerImpl extends LocationRandomizer {

    public LocationRandomizerImpl(List<Material> blacklist, World world, Border border) {
        super(blacklist, world, border);
    }

    public LocationRandomizerImpl(List<Material> blacklist, FarmWorld farmWorld) {
        super(blacklist, farmWorld);
    }

    @Override
    public Location random() {
        final boolean nether = world.getEnvironment() == World.Environment.NETHER;
        int x, y, z;

        do {
            x = random(border.getCenterX());
            z = random(border.getCenterZ());
            y = world.getHighestBlockYAt(x, z);

            if (nether)
                y = getYInNether(x, y, z);
            else y++;

            if (!isYValid(world.getBlockAt(x, y-1, z)))
                y = 0;
        } while (y == 0);

        return new Location(world, x + .5D, y, z + .5D);
    }

    private int getYInNether(int x, int y, int z) {
        while (y != 0) {
            y = findBlockAtY(world, x, y, z, true);

            if (y <= 0) return 0;

            y--;
            final Block block = world.getBlockAt(x, y, z);
            if (!block.getType().isAir())
                continue;

            y = findBlockAtY(world, x, y, z, false) + 1;
            break;
        }
        return Math.max(y, 0);
    }

    private int findBlockAtY(World world, int x, int y, int z, boolean air) {
        Block block;
        do {
            block = world.getBlockAt(x, y, z);
            if (block.getType().isAir() == air) break;
            y--;
        } while (y > 0);
        return y;
    }

    private boolean isYValid(Block block) {
        return block.getY() != 0 && !blacklist.contains(block.getType());
    }

    private int random(int center) {
        final int size = border.getSize()/2;
        return ThreadLocalRandom.current().nextInt(center-size, center+size);
    }
}
