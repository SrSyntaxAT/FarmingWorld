package at.srsyntax.farmingworld;

import at.srsyntax.farmingworld.api.API;
import at.srsyntax.farmingworld.api.CooldownHandler;
import at.srsyntax.farmingworld.api.FarmingWorld;
import at.srsyntax.farmingworld.api.event.DeleteFarmingWorldEvent;
import at.srsyntax.farmingworld.api.event.GenerateNewFarmingWorldEvent;
import at.srsyntax.farmingworld.api.exception.TeleportFarmingWorldException;
import at.srsyntax.farmingworld.config.FarmingWorldConfig;
import at.srsyntax.farmingworld.util.CooldownHandlerImpl;
import lombok.AllArgsConstructor;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

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
  public @NotNull World generateFarmingWorld(@NotNull FarmingWorld farmingWorld) {
    final FarmingWorldConfig config = (FarmingWorldConfig) farmingWorld;

    final String name = farmingWorld.getName() + "-" + UUID.randomUUID().toString().split("-")[0];
    final World world = loadFarmingWorld(name, config.getEnvironment(), farmingWorld.getGenerator());
    setBorder(world, farmingWorld.getBorderSize());

    callEvent(new GenerateNewFarmingWorldEvent(farmingWorld, world));

    return world;
  }

  private void setBorder(World world, double size) {
    if (size < 10) return;
    if (world.getEnvironment() == World.Environment.THE_END) return;

    final WorldBorder border = world.getWorldBorder();
    border.setCenter(0D, 0D);
    border.setSize(size);
  }

  @Override
  public void deleteFarmingWorld(@NotNull FarmingWorld farmingWorld, @NotNull World world) {
    callEvent(new DeleteFarmingWorldEvent(farmingWorld, world));

    sync(() -> {
      try {
        final Location location = getFallbackWorld().getSpawnLocation();
        world.getPlayers().forEach(player -> player.teleport(location));
      } catch (Exception e) {
        this.plugin.getLogger().severe("No fallback world could be found!");
      }

      for (Chunk chunk : world.getLoadedChunks())
        chunk.unload(false);

      world.getForceLoadedChunks().forEach(chunk -> chunk.unload(false));

      Bukkit.unloadWorld(world, false);

      try {
        this.plugin.getDatabase().deleteFarmingWorld(farmingWorld);
      } catch (SQLException e) {
        e.printStackTrace();
      }
      Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> deleteFolder(world.getWorldFolder()), 60L);
    });
  }

  @Override
  public @Nullable World getFallbackWorld() throws IOException {
    String worldName = this.plugin.getPluginConfig().getFallbackWorld();
    if (worldName == null)
      worldName = readServerPropertiesWorldName();
    if (worldName == null) throw new NullPointerException();
    return Bukkit.getWorld(worldName);
  }

  @Override
  public void unloadWorlds(FarmingWorld farmingWorld) {
    try {
      farmingWorld.kickAll();
    } catch (IOException e) {
      e.printStackTrace();
    }
    unloadWorld(farmingWorld.getWorld());
    unloadWorld(farmingWorld.getNextWorld());
  }

  private void unloadWorld(World world) {
    if (world == null) return;
    sync(() -> Bukkit.unloadWorld(world, true));
  }

  private String readServerPropertiesWorldName() throws IOException {
    final Properties properties = new Properties();
    properties.load(new FileReader("server.properties"));
    return properties.getProperty("level-name");
  }

  @Override
  public void deleteFarmingWorld(@NotNull FarmingWorld farmingWorld) {
    deleteFarmingWorld(farmingWorld, farmingWorld.getWorld());
  }

  public boolean deleteFolder(File folder) {
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
  public @Nullable FarmingWorld getFarmingWorld(@NotNull String name) {
    for (FarmingWorld farmingWorld : getFarmingWorlds()) {
      if (farmingWorld.getName().equalsIgnoreCase(name))
        return farmingWorld;
    }
    return null;
  }

  @Override
  public @Nullable FarmingWorld getFarmingWorld(@NotNull World world) {
    for (FarmingWorld farmingWorld : getFarmingWorlds()) {
      if (world.equals(farmingWorld.getWorld()) || world.equals(farmingWorld.getNextWorld()))
        return farmingWorld;
    }
    return null;
  }

  @Override
  public boolean isFarmingWorld(World world) {
    return getFarmingWorld(world) != null;
  }

  @Override
  public @NotNull World loadFarmingWorld(@NotNull String name, World.@NotNull Environment environment) {
    return loadFarmingWorld(name, environment, null);
  }

  @Override
  public @NotNull World loadFarmingWorld(@NotNull String name, World.@NotNull Environment environment, String generator) {
    final WorldCreator creator = new WorldCreator(name);
    creator.environment(environment);
    if (generator != null) creator.generator(generator);
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
  public void randomTeleport(Player player, FarmingWorld farmingWorld) throws TeleportFarmingWorldException {
    if (!farmingWorld.isActiv()) throw new TeleportFarmingWorldException(this.plugin.getPluginConfig().getMessage());
    player.teleport(farmingWorld.randomLocation());
  }

  public String getDate(long date) {
    final DateFormat format = new SimpleDateFormat(this.plugin.getPluginConfig().getMessage().getDateFormat());
    return format.format(new Date(date));
  }

  public String getDate() {
    return getDate(System.currentTimeMillis());
  }

  @Override
  public CooldownHandler newCooldownHandler(Player player, FarmingWorld farmingWorld) {
    return new CooldownHandlerImpl(this.plugin, player, farmingWorld);
  }

  @Override
  public void reload() {
    this.plugin.onDisable();
    Bukkit.getOnlinePlayers().forEach(player -> this.plugin.removeFromBossBar(player, player.getWorld()));
    this.plugin.onEnable();
    Bukkit.getOnlinePlayers().forEach(this.plugin::addToBossBar);
  }
}
