package at.srsyntax.farmingworld.config;

import at.srsyntax.farmingworld.FarmingWorldPlugin;
import at.srsyntax.farmingworld.api.FarmingWorld;
import at.srsyntax.farmingworld.api.Message;
import at.srsyntax.farmingworld.api.RemainingDisplay;
import at.srsyntax.farmingworld.api.event.ReplacedFarmingWorldEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
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
@NoArgsConstructor
@Getter @Setter
public class FarmingWorldConfig implements FarmingWorld {

  private transient BossBar bossBar;
  private transient FarmingWorldPlugin plugin;

  private String name, permission, currentWorldName, nextWorldName;
  private long created;
  private int timer;
  private World.Environment environment;

  public FarmingWorldConfig(String name, String permission, String currentWorldName, String nextWorldName, long created, int timer, World.Environment environment) {
    this.name = name;
    this.permission = permission;
    this.currentWorldName = currentWorldName;
    this.nextWorldName = nextWorldName;
    this.created = created;
    this.timer = timer;
    this.environment = environment;
  }

  @Override
  public long getReset() {
    return created + TimeUnit.MINUTES.toMillis(timer);
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
  public void updateRemainingDisplay() {
    final MessageConfig messageConfig = plugin.getPluginConfig().getMessage();

    long reset = getReset();
    if (getRemaining() > TimeUnit.SECONDS.toMillis(60))
      reset+=TimeUnit.SECONDS.toMillis(60);

    final Message message = new Message(messageConfig.getRemaining())
        .add("<remaining>", FarmingWorldPlugin.getApi().getRemainingTime(reset))
        .add("<second>", messageConfig.getSecond())
        .add("<seconds>", messageConfig.getSeconds())
        .add("<minute>", messageConfig.getMinute())
        .add("<minutes>", messageConfig.getMinutes())
        .add("<hour>", messageConfig.getHour())
        .add("<hours>", messageConfig.getHours())
        .add("<day>", messageConfig.getDay())
        .add("<days>", messageConfig.getDays());

    display(message.replace());
  }

  public void checkBossbar(String message) {
    if (bossBar == null) {
      bossBar = Bukkit.createBossBar(message, plugin.getPluginConfig().getBarColor(), BarStyle.SEGMENTED_20);
      bossBar.setVisible(true);
      getWorld().getPlayers().forEach(player -> bossBar.addPlayer(player));
    }
  }

  public void display(String message) {
    if (plugin.getPluginConfig().getRemainingDisplay() == RemainingDisplay.BOSS_BAR)
      displayBossBar(message);
    else
      displayActionBar(message);
  }

  private void displayBossBar(String message) {
    checkBossbar(message);
    bossBar.setTitle(message);
  }

  private void displayActionBar(String message) {
    final TextComponent textComponent = new TextComponent(message);
    getWorld().getPlayers().forEach(player -> player.spigot().sendMessage(ChatMessageType.ACTION_BAR, textComponent));
  }

  @Override
  public World getWorld() {
    if (currentWorldName == null)
      return null;
    return Bukkit.getWorld(currentWorldName);
  }

  @Override
  public void newWorld(@NotNull World world) {
    final World old = getWorld();

    setCurrentWorldName(world.getName());
    setCreated(System.currentTimeMillis());

    if (old != null)
      old.getPlayers().forEach(this::teleport);

    final Event event = new ReplacedFarmingWorldEvent(this, world, old);
    Bukkit.getScheduler().runTask(plugin, () -> Bukkit.getPluginManager().callEvent(event));

    if (old != null)
      FarmingWorldPlugin.getApi().deleteFarmingWorld(this, old);

    save();
  }

  @Override
  public void setNextWorld(World world) {
    setNextWorldName(world == null ? null : world.getName());
    save();
  }

  @Override
  public @Nullable World getNextWorld() {
    if (nextWorldName == null)
      return null;
    return Bukkit.getWorld(nextWorldName);
  }

  @Override
  public void teleport(@NotNull Player player) {
    Bukkit.getScheduler().runTask(plugin, () -> FarmingWorldPlugin.getApi().randomTeleport(player, getWorld()));
  }

  private void save() {
    try {
      plugin.getPluginConfig().save(plugin);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
