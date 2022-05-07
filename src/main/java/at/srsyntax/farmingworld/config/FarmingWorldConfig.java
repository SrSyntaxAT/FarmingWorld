package at.srsyntax.farmingworld.config;

import at.srsyntax.farmingworld.FarmingWorldPlugin;
import at.srsyntax.farmingworld.api.API;
import at.srsyntax.farmingworld.api.FarmingWorld;
import at.srsyntax.farmingworld.api.exception.GenerateLocationException;
import at.srsyntax.farmingworld.api.exception.TeleportFarmingWorldException;
import at.srsyntax.farmingworld.database.FarmingWorldData;
import at.srsyntax.farmingworld.util.AsyncTasks;
import at.srsyntax.farmingworld.util.Displayer;
import at.srsyntax.farmingworld.util.world.FarmingWorldDeleter;
import at.srsyntax.farmingworld.util.world.FarmingWorldLoader;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

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
@Getter
public class FarmingWorldConfig implements FarmingWorld, AsyncTasks {

  private transient FarmingWorldPlugin plugin;
  @Setter private transient FarmingWorldData data;
  private final transient Displayer displayer;
  private final transient Unsafe unsafe;

  private String name;
  @Setter private boolean activ = false;

  private String permission, generator;
  private int timer, rtpArenaSize;
  private double borderSize;
  private World.Environment environment;

  public FarmingWorldConfig(String name, String permission, String currentWorldName, String nextWorldName, long created, int timer, World.Environment environment, int rtpArenaSize, double borderSize, String generator) {
    this.name = name;
    this.permission = permission;
    this.data = new FarmingWorldData(created, currentWorldName, nextWorldName);
    this.timer = timer;
    this.environment = environment;
    this.rtpArenaSize = rtpArenaSize;
    this.borderSize = borderSize;
    this.generator = generator;

    this.displayer = new Displayer(this);
    this.unsafe = new Unsafe(this);
  }

  public FarmingWorldConfig() {
    this.displayer = new Displayer(this);
    this.unsafe = new Unsafe(this);
  }

  @Override
  public long getCreated() {
    return data.getCreated();
  }

  @Override
  public long getReset() {
    return getCreated() + TimeUnit.MINUTES.toMillis(timer);
  }

  @Override
  public boolean needReset() {
    return getReset() <= System.currentTimeMillis();
  }

  @Override
  public long getRemaining() {
    return getReset() - System.currentTimeMillis();
  }

  @Override
  public Location randomLocation() {
    if (!isActiv() || this.data == null) throw new GenerateLocationException();
    return this.data.getRandomLocation();
  }

  @Override
  public void updateDisplay() {
    this.displayer.updateDisplay();
  }

  @Override
  public void updateDisplay(Player player) {
    this.displayer.updateDisplay(player);
  }

  @Override
  public World getWorld() {
    return this.data.getWorld();
  }

  @Override
  public void newWorld(@NotNull World world) {
    this.data.newWorld(world);
  }

  @Override
  public void setNextWorld(World world) {
    this.data.setNextWorld(world);
  }

  public void disable() {
    this.plugin.getLogger().info("Disable " + this);
    setActiv(false);
    FarmingWorldPlugin.getApi().unloadWorlds(this);
    save();
  }

  @Override
  public void enable() {
    setActiv(true);
    new FarmingWorldLoader(getPlugin().getLogger(), FarmingWorldPlugin.getApi(), getPlugin(), getPlugin().getDatabase()).enable(this);
    save();
  }

  @Override
  public void delete() {
    new FarmingWorldDeleter(FarmingWorldPlugin.getApi(), getPlugin(), this).delete();
  }
  @Override
  public void save() {
    try {
      this.plugin.getPluginConfig().save(this.plugin);
    } catch (IOException e) {
      this.plugin.getLogger().severe(getName() + " could not be saved!");
      e.printStackTrace();
    }
  }

  @Override
  public @Nullable World getNextWorld() {
    return this.data.getNextWorld();
  }

  @Override
  public boolean hasNext() {
    return this.data.hasNext();
  }

  @Override
  public void teleport(@NotNull Player player) throws TeleportFarmingWorldException {
    FarmingWorldPlugin.getApi().randomTeleport(player, this);
  }

  @Override
  public void kickAll() throws IOException {
    kickAll(null);
  }

  @Override
  public void kickAll(String reason) throws IOException {
    final API api = FarmingWorldPlugin.getApi();
    final World world = getWorld(), fallbackWorld = api.getFallbackWorld();

    if (world == null || fallbackWorld == null) return;
    final Location location = fallbackWorld.getSpawnLocation();

    Bukkit.getScheduler().runTask(this.plugin, () -> world.getPlayers().forEach(player -> {
      if (reason != null)
        player.sendMessage(reason);
      player.teleport(location);
    }));
  }

  @Override
  public boolean isFarming(@NotNull Player player) {
    final World world = getWorld();
    if (world != null)
      return world.getPlayers().contains(player);
    return false;
  }

  public void setPlugin(FarmingWorldPlugin plugin) {
    this.plugin = plugin;
  }

  public void setRtpArenaSize(int rtpArenaSize) {
    this.rtpArenaSize = rtpArenaSize;
  }

  @AllArgsConstructor
  public static class Unsafe {

    private final FarmingWorldConfig farmingWorld;

    public void teleport(Player player) {
      try {
        this.farmingWorld.teleport(player);
      } catch (TeleportFarmingWorldException e) {
        throw new RuntimeException(e);
      }
    }
  }
}
