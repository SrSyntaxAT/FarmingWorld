package at.srsyntax.farmingworld;

import at.srsyntax.farmingworld.api.API;
import at.srsyntax.farmingworld.api.FarmingWorld;
import at.srsyntax.farmingworld.api.event.DeleteFarmingWorldEvent;
import at.srsyntax.farmingworld.api.event.GenerateNewFarmingWorldEvent;
import at.srsyntax.farmingworld.config.FarmingWorldConfig;
import lombok.AllArgsConstructor;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
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
public final class APIImpl implements API {

  private final FarmingWorldPlugin plugin;

  @Override
  public @NotNull World generateFarmingWorld(FarmingWorld farmingWorld) {
    final FarmingWorldConfig config = (FarmingWorldConfig) farmingWorld;

    final String name = farmingWorld.getName() + "-" + UUID.randomUUID().toString().split("-")[0];
    final World world = loadFarmingWorld(name, config.getEnvironment());

    callEvent(new GenerateNewFarmingWorldEvent(farmingWorld, world));

    return world;
  }

  @Override
  public void deleteFarmingWorld(FarmingWorld farmingWorld, World world) {
    callEvent(new DeleteFarmingWorldEvent(farmingWorld, world));

    sync(() -> {
      final Location location = plugin.getPluginConfig().getFallback().toBukkit();
      world.getPlayers().forEach(player -> player.teleport(location));

      for (Chunk chunk : world.getLoadedChunks())
        chunk.unload(false);

      world.getForceLoadedChunks().forEach(chunk -> chunk.unload(false));

      Bukkit.unloadWorld(world, false);

      Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> deleteFolder(world.getWorldFolder()), 60L);
    });
  }

  @Override
  public void deleteFarmingWorld(FarmingWorld farmingWorld) {
    deleteFarmingWorld(farmingWorld, farmingWorld.getWorld());
  }

  private boolean deleteFolder(File folder) {
    if(folder.exists()) {
      final File[] files = folder.listFiles();

      if(files != null) {
        for (File file : files) {
          if (file.isDirectory())
            deleteFolder(file);
          else
            file.delete();
        }
      }
    }

    return folder.delete();
  }

  private void sync(@NotNull Runnable runnable) {
    Bukkit.getScheduler().runTask(plugin, runnable);
  }

  private void callEvent(Event event) {
    sync(() -> Bukkit.getPluginManager().callEvent(event));
  }

  @Override
  public @NotNull List<? extends FarmingWorld> getFarmingWorlds() {
    return plugin.getPluginConfig().getFarmingWorlds();
  }

  @Override
  public @Nullable FarmingWorld getFarmingWorld(String name) {
    for (FarmingWorld farmingWorld : getFarmingWorlds()) {
      if (farmingWorld.getName().equalsIgnoreCase(name))
        return farmingWorld;
    }
    return null;
  }

  @Override
  public @Nullable FarmingWorld getFarmingWorld(World world) {
    for (FarmingWorld farmingWorld : getFarmingWorlds()) {
      if (farmingWorld.getWorld().equals(world))
        return farmingWorld;
    }
    return null;
  }

  @Override
  public boolean isFarmingWorld(World world) {
    return getFarmingWorld(world) != null;
  }

  @Override
  public @NotNull World loadFarmingWorld(String name, World.Environment environment) {
    final WorldCreator creator = new WorldCreator(name);
    creator.environment(environment);
    return Objects.requireNonNull(Bukkit.createWorld(creator));
  }

  private int[] getRemainingTimeArray(long milli) {
    if (milli < 1000) throw new IllegalArgumentException();

    final long currentTime = System.currentTimeMillis();
    long time = currentTime > milli ? currentTime - milli : milli - currentTime;
    int days = 0,
        hours = 0,
        minutes = 0,
        seconds;

    if ((time /= 1000) >= 86400)
      days = (int)time / 86400;

    if ((time -= days * 86400) > 3600)
      hours = (int)time / 3600;

    if ((time -= hours * 3600) > 60)
      minutes = (int)time / 60;

    seconds = (int) (time - (minutes * 60));

    return new int[]{days, hours, minutes, seconds};
  }

  public @NotNull String getRemainingTime(long time) {
    final int[] end = getRemainingTimeArray(time);
    final StringBuilder stringBuilder = new StringBuilder();
    final int days = end[0],
        hours = end[1],
        minutes = end[2],
        seconds = end[3];

    boolean showSeconds = true;

    if (days != 0) {
      stringBuilder.append(days == 1 ? days + " <day> " : days + " <days> ");
      showSeconds = false;
    }

    if (hours != 0) {
      stringBuilder.append(hours == 1 ? hours + " <hour> " : hours + " <hours> ");
      showSeconds = false;
    }

    if (minutes != 0) {
      stringBuilder.append(minutes == 1 ? minutes + " <minute> " : minutes + " <minutes> ");
      showSeconds = false;
    }

    if (seconds != 0 && showSeconds)
      stringBuilder.append(seconds == 1 ? seconds + " <second> " : seconds + " <seconds> ");

    return stringBuilder.toString();
  }

  @Override
  public void randomTeleport(Player player, World world) {
    player.teleport(randomLocation(world));
  }

  private Location randomLocation(World world) {
    final boolean nether = world.getEnvironment() == World.Environment.NETHER;
    int x, y, z, size = plugin.getPluginConfig().getRtpArenaSize();
    final Location spawn = world.getSpawnLocation();

    do {
      x = random(spawn.getBlockX(), size);
      z = random(spawn.getBlockZ(), size);
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
    return material != Material.AIR && material != Material.LAVA;
  }

  private int random(int current, int size) {
    size/=2;
    return ThreadLocalRandom.current().nextInt(current - size, current + size);
  }

  public String getDate(long date) {
    final DateFormat format = new SimpleDateFormat(this.plugin.getPluginConfig().getMessage().getDateFormat());
    return format.format(new Date(date));
  }

  public String getDate() {
    return getDate(System.currentTimeMillis());
  }
}
