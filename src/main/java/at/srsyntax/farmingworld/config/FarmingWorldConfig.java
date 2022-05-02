package at.srsyntax.farmingworld.config;

import at.srsyntax.farmingworld.FarmingWorldPlugin;
import at.srsyntax.farmingworld.api.API;
import at.srsyntax.farmingworld.api.FarmingWorld;
import at.srsyntax.farmingworld.api.message.Message;
import at.srsyntax.farmingworld.api.DisplayPosition;
import at.srsyntax.farmingworld.api.event.ReplacedFarmingWorldEvent;
import at.srsyntax.farmingworld.command.exception.TeleportFarmingWorldException;
import at.srsyntax.farmingworld.database.FarmingWorldData;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.sql.SQLException;
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
@NoArgsConstructor
@Getter
public class FarmingWorldConfig implements FarmingWorld {

  private transient BossBar bossBar;
  private transient FarmingWorldPlugin plugin;
  @Setter private transient FarmingWorldData data;

  private String name;
  @Setter private boolean activ = true;

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
  public void updateDisplay() {
    final String message = getUpdateMessage();
    getWorld().getPlayers().forEach(player -> display(player, message));
  }

  @Override
  public void updateDisplay(Player player) {
    display(player, getUpdateMessage());
  }

  private String getUpdateMessage() {
    final MessageConfig messageConfig = plugin.getPluginConfig().getMessage();

    long reset = getReset();
    if (getRemaining() > TimeUnit.SECONDS.toMillis(60))
      reset+=TimeUnit.SECONDS.toMillis(60);

    final API api = FarmingWorldPlugin.getApi();
    final Message message = new Message(messageConfig.getRemaining())
        .add("<remaining>", api.getRemainingTime(reset))
        .add("<date>", api.getDate(getReset()))
        .add("<second>", messageConfig.getSecond())
        .add("<seconds>", messageConfig.getSeconds())
        .add("<minute>", messageConfig.getMinute())
        .add("<minutes>", messageConfig.getMinutes())
        .add("<hour>", messageConfig.getHour())
        .add("<hours>", messageConfig.getHours())
        .add("<day>", messageConfig.getDay())
        .add("<days>", messageConfig.getDays());

    return message.replace();
  }

  public void checkBossbar(String message) {
    if (bossBar == null) {
      bossBar = Bukkit.createBossBar(message, plugin.getPluginConfig().getBarColor(), BarStyle.SEGMENTED_20);
      bossBar.setVisible(true);
      getWorld().getPlayers().forEach(player -> bossBar.addPlayer(player));
    }
  }

  public void display(String message) {
    getWorld().getPlayers().forEach(player -> display(player, message));
  }

  public void display(Player player, String message) {
    if (plugin.getPluginConfig().getDisplayPosition() == DisplayPosition.BOSS_BAR)
      displayBossBar(message);
    else
      displayActionBar(player, message);
  }

  private void displayBossBar(String message) {
    checkBossbar(message);
    bossBar.setTitle(message);
  }

  private void displayActionBar(Player player, String message) {
    final TextComponent textComponent = new TextComponent(message);
    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, textComponent);
  }

  @Override
  public World getWorld() {
    if (this.data.getCurrentWorldName() == null)
      return null;
    return Bukkit.getWorld(this.data.getCurrentWorldName());
  }

  @Override
  public void newWorld(@NotNull World world) {
    final World old = getWorld();

    this.data.setCurrentWorldName(world.getName());
    this.data.setCreated(System.currentTimeMillis());

    if (old != null)
      old.getPlayers().forEach(this::teleportUnsafe);

    final Event event = new ReplacedFarmingWorldEvent(this, world, old);
    Bukkit.getScheduler().runTask(plugin, () -> Bukkit.getPluginManager().callEvent(event));

    if (old != null)
      FarmingWorldPlugin.getApi().deleteFarmingWorld(this, old);

    try {
      this.plugin.getDatabase().updateWorld(this);
    } catch (SQLException e) {
      handleSaveError(e);
    }
  }

  @Override
  public void setNextWorld(World world) {
    this.data.setNextWorldName(world == null ? null : world.getName());
    try {
      this.plugin.getDatabase().updateNextWorld(this);
    } catch (SQLException e) {
      handleSaveError(e);
    }
  }

  private void handleSaveError(SQLException exception) {
    this.plugin.getLogger().severe(getName() + " could not be saved!");
    exception.printStackTrace();
  }

  public void disable() {
    this.plugin.getLogger().info("Disable " + this);
    setActiv(false);
    FarmingWorldPlugin.getApi().unloadWorlds(this);

    try {
      this.plugin.getPluginConfig().save(this.plugin);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public @Nullable World getNextWorld() {
    if (this.data.getNextWorldName() == null)
      return null;
    return Bukkit.getWorld(this.data.getNextWorldName());
  }

  @Override
  public boolean hasNext() {
    return getNextWorld() != null;
  }

  @Override
  public void teleport(@NotNull Player player) throws TeleportFarmingWorldException {
    FarmingWorldPlugin.getApi().randomTeleport(player, this);
  }

  private void teleportUnsafe(Player player) {
    try {
      teleport(player);
    } catch (TeleportFarmingWorldException e) {
      throw new RuntimeException(e);
    }
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

  @Override
  public String toString() {
    return "FarmingWorldConfig{" +
        "data=" + data +
        ", name='" + name + '\'' +
        ", activ=" + activ +
        ", permission='" + permission + '\'' +
        ", generator='" + generator + '\'' +
        ", timer=" + timer +
        ", rtpArenaSize=" + rtpArenaSize +
        ", borderSize=" + borderSize +
        ", environment=" + environment +
        '}';
  }
}
