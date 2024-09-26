package at.srsyntax.farmingworld.farmworld.display;

import at.srsyntax.farmingworld.api.farmworld.FarmWorld;
import at.srsyntax.farmingworld.api.message.Message;
import at.srsyntax.farmingworld.api.util.TimeUtil;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

/*
 * MIT License
 *
 * Copyright (c) 2022-2024 Marcel Haberl
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
public class Displayer {

    private final DisplayRegistry registry;
    private final FarmWorld farmWorld;
    private BossBar bossBar;

    public Displayer(DisplayRegistry registry, FarmWorld farmWorld) {
        this.registry = registry;
        this.farmWorld = farmWorld;
        createBossBar();
    }

    public void display() {
        if (bossBar == null)
            displayActionBar();
        else
            displayBossBar();
    }

    public void addPlayer(Player player) {
        if (bossBar == null) return;
        bossBar.addPlayer(player);
    }

    public void removePlayer(Player player) {
        if (bossBar == null) return;
        bossBar.removePlayer(player);
    }

    public void removePlayers() {
        if (bossBar == null) return;
        bossBar.removeAll();
    }

    private void displayBossBar() {
        bossBar.setTitle(replaceMessage());
        if (registry.getConfig().isChangeBossBarProgress()) {
            final double baseValue = farmWorld.getResetDate() - farmWorld.getCreated();
            final double percentValue = farmWorld.getResetDate() - System.currentTimeMillis();
            final double progess = 1 - percentValue / baseValue;
            bossBar.setProgress(progess > 1 ? 1 : progess < 0 ? 0 : progess);
        }
    }

    private void displayActionBar() {
        final var component = new TextComponent(replaceMessage());
        farmWorld.getPlayers().forEach(player -> player.spigot().sendMessage(ChatMessageType.ACTION_BAR, component));
    }

    protected void createBossBar() {
        if (!registry.getConfig().isBossBar()) return;
        if (bossBar != null) return;

        final var config = registry.getConfig();
        bossBar = Bukkit.createBossBar(replaceMessage(), config.getBarColor(), config.getBarStyle());

        final World world = farmWorld.getWorld();
        if (world == null) return;
        world.getPlayers().forEach(bossBar::addPlayer);
    }

    private String replaceMessage() {
        final long reset = farmWorld.getResetDate();
        final var timeConfig = registry.getPlugin().getMessageConfig().getTime();
        return new Message(registry.getConfig().getMessage(), ChatMessageType.SYSTEM)
                .replace("%{date}", TimeUtil.getDate(registry.getConfig().getDateFormat(), reset))
                .replace("%{remaining}", TimeUtil.getRemainingTime(timeConfig, reset, true))
                .toString();
    }
}
