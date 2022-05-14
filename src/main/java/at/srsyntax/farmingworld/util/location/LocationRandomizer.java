package at.srsyntax.farmingworld.util.location;

import at.srsyntax.farmingworld.api.FarmingWorld;
import lombok.AllArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

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
@AllArgsConstructor
public class LocationRandomizer {

  private final List<Material> blacklist;
  private final World world;
  private final int rtpSize;

  public LocationRandomizer(List<Material> blacklist, FarmingWorld farmingWorld) {
    this(blacklist, farmingWorld.getWorld(), farmingWorld.getRtpArenaSize() / 2);
  }

  public Location random() {
    final boolean nether = world.getEnvironment() == World.Environment.NETHER;
    int x, y, z;
    final Location spawn = world.getSpawnLocation();

    do {
      x = random(spawn.getBlockX(), rtpSize);
      z = random(spawn.getBlockZ(), rtpSize);
      y = world.getHighestBlockYAt(x, z);


      if (nether)
        y = getYInNether(world, x, y, z);
      else if (y != 0)
        y++;

      if (!isYValid(world.getBlockAt(x, y-1, z).getType()))
        y = 0;

    } while (y == 0);

    return new Location(world, x, y, z, spawn.getYaw(), spawn.getPitch());
  }

  private int getYInNether(World world, int x, int y, int z) {
    while (y != 0) {
      y = findBlockAtY(world, x, y, z, true);

      y--;
      final Block block = world.getBlockAt(x, y, z);
      if (!block.getType().isAir())
        continue;

      y = findBlockAtY(world, x, y, z, false) + 1;
      break;
    }
    return y;
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

  private boolean isYValid(Material material) {
    return !blacklist.contains(material);
  }

  private int random(int current, int size) {
    size/=2;
    return ThreadLocalRandom.current().nextInt(current - size, current + size);
  }
}
