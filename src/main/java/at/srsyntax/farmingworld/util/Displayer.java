package at.srsyntax.farmingworld.util;

import at.srsyntax.farmingworld.FarmingWorldPlugin;
import at.srsyntax.farmingworld.api.API;
import at.srsyntax.farmingworld.api.display.DisplayPosition;
import at.srsyntax.farmingworld.api.display.Displayable;
import at.srsyntax.farmingworld.api.message.Message;
import at.srsyntax.farmingworld.config.FarmingWorldConfig;
import at.srsyntax.farmingworld.config.MessageConfig;
import lombok.Getter;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

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
public class Displayer implements Displayable {

  private final FarmingWorldConfig farmingWorld;
  @Getter private BossBar bossBar;

  public Displayer(FarmingWorldConfig farmingWorld) {
    this.farmingWorld = farmingWorld;
  }

  private String getUpdateMessage(MessageConfig messageConfig) {
    long reset = this.farmingWorld.getReset();

    if (this.farmingWorld.getRemaining() > TimeUnit.SECONDS.toMillis(60))
      reset+=TimeUnit.SECONDS.toMillis(60);

    final API api = FarmingWorldPlugin.getApi();
    final Message message = new Message(messageConfig.getRemaining())
        .add("<remaining>", api.getRemainingTime(reset))
        .add("<date>", api.getDate(reset))
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

  @Override
  public void updateDisplay() {
    final World world = this.farmingWorld.getWorld();
    if (world == null) return;
    final String message = getUpdateMessage(this.farmingWorld.getPlugin().getPluginConfig().getMessage());
    world.getPlayers().forEach(player -> display(player, message));
  }

  @Override
  public void updateDisplay(Player player) {
    display(player, getUpdateMessage(this.farmingWorld.getPlugin().getPluginConfig().getMessage()));
  }


  public void display(String message) {
    final World world = this.farmingWorld.getWorld();
    if (world == null) return;
    world.getPlayers().forEach(player -> display(player, message));
  }

  private void display(Player player, String message) {
    if (this.farmingWorld.getPlugin().getPluginConfig().getDisplayPosition() == DisplayPosition.BOSS_BAR)
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

  public void checkBossbar(String message) {
    if (this.bossBar != null) return;
    this.bossBar = Bukkit.createBossBar(message, this.farmingWorld.getPlugin().getPluginConfig().getBarColor(), BarStyle.SEGMENTED_20);
    this.bossBar.setVisible(true);

    final World world = this.farmingWorld.getWorld();
    if (world == null) return;
    world.getPlayers().forEach(player -> this.bossBar.addPlayer(player));
  }

  public void removeFromBossBar(Player player) {
    if (this.bossBar == null) return;
    this.bossBar.removePlayer(player);
  }
}
