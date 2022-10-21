package at.srsyntax.farmingworld.handler.countdown;

import at.srsyntax.farmingworld.FarmingWorldPlugin;
import at.srsyntax.farmingworld.api.event.countdown.CountdownCanceledEvent;
import at.srsyntax.farmingworld.api.event.countdown.CountdownFinishedEvent;
import at.srsyntax.farmingworld.api.event.countdown.CountdownStartedEvent;
import at.srsyntax.farmingworld.api.handler.HandleException;
import at.srsyntax.farmingworld.api.handler.countdown.Countdown;
import at.srsyntax.farmingworld.api.handler.countdown.CountdownCallback;
import at.srsyntax.farmingworld.api.handler.countdown.exception.AlreadyStartedException;
import at.srsyntax.farmingworld.api.handler.countdown.exception.CanceledException;
import at.srsyntax.farmingworld.config.MessageConfig;
import at.srsyntax.farmingworld.config.PluginConfig;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

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
public class CountdownImpl implements Countdown {

    private final FarmingWorldPlugin plugin;
    private final MessageConfig.CountdownMessages messages;
    private final PluginConfig.CountdownConfig config;

    private final Player player;
    private final CountdownCallback callback;

    private BukkitTask task;

    public CountdownImpl(FarmingWorldPlugin plugin, Player player, CountdownCallback callback) {
        this.plugin = plugin;
        this.messages = plugin.getPluginConfig().getMessages().getCountdown();
        this.config = plugin.getPluginConfig().getCountdown();
        this.player = player;
        this.callback = callback;
    }

    @Override
    public boolean canBypass() {
        return player.hasPermission("farmingworld.bypass.countdown") || player.hasPermission("farmingworld.bypass.*");
    }

    @Override
    public void handle() throws HandleException {
        final CountdownRegistry registry = plugin.getCountdownRegistry();
        if (registry.hasCountdown(player) || task != null)
            throw new AlreadyStartedException(messages.getAlreadyStarted(), this);

        if (canBypass()) {
            callback.finished(this);
        } else {
            registry.register(this);
            final Runnable runnable = new CountdownRunnable(messages, this, config);
            task = Bukkit.getScheduler().runTaskTimer(plugin, runnable, 0L, 20L);
            Bukkit.getPluginManager().callEvent(new CountdownStartedEvent(this));
        }
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public void finish() {
        if (!isRunning()) return;
        cancel(false, CanceledException.Result.SUCCESSFUL);
        callback.finished(this);
        Bukkit.getPluginManager().callEvent(new CountdownFinishedEvent(this));
    }

    @Override
    public void cancel(boolean event, CanceledException.@NotNull Result result) {
        if (!isRunning()) return;
        task.cancel();
        plugin.getCountdownRegistry().unregister(player);

        if (result != CanceledException.Result.SUCCESSFUL)
            callback.error(this, new CanceledException(messages.getMoved(), this, result));

        if (event)
            Bukkit.getPluginManager().callEvent(new CountdownCanceledEvent(this, result));
    }

    @Override
    public boolean isRunning() {
        return task != null && !task.isCancelled();
    }
}
