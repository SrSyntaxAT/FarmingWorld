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
