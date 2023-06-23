package at.srsyntax.farmingworld.api.handler.countdown;

import at.srsyntax.farmingworld.api.event.countdown.CountdownCanceledEvent;
import at.srsyntax.farmingworld.api.event.countdown.CountdownFinishedEvent;
import at.srsyntax.farmingworld.api.event.countdown.CountdownStartedEvent;
import at.srsyntax.farmingworld.api.handler.HandleException;
import at.srsyntax.farmingworld.api.handler.countdown.exception.CanceledException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

/*
 * MIT License
 *
 * Copyright (c) 2022-2023 Marcel Haberl
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
public abstract class AbstractCountdown implements Countdown {

    protected JavaPlugin plugin;
    protected final CountdownRegistry countdownRegistry;
    protected final Player player;
    protected final CountdownCallback callback;

    protected BukkitTask task;

    public AbstractCountdown(JavaPlugin plugin, Player player, CountdownCallback callback, CountdownRegistry registry) {
        this.plugin = plugin;
        this.player = player;
        this.callback = callback;
        this.countdownRegistry = registry;
    }

    @Override
    public void handle() throws HandleException {
        if (canBypass()) {
            callback.finished(this);
        } else {
            countdownRegistry.register(this);
            task = Bukkit.getScheduler().runTaskTimer(plugin, createRunnable(), 0L, 20L);
            Bukkit.getPluginManager().callEvent(new CountdownStartedEvent(this));
        }
    }

    protected abstract CountdownRunnable createRunnable();
    public abstract CanceledException.Messages getMessages();

    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public void finish() {
        if (!isRunning()) return;
        cancel(false, null, CanceledException.Result.SUCCESSFUL);
        callback.finished(this);
        Bukkit.getPluginManager().callEvent(new CountdownFinishedEvent(this));
    }

    @Override
    public void cancel(boolean event, String message, CanceledException.@NotNull Result result) {
        if (!isRunning()) return;
        task.cancel();
        countdownRegistry.unregister(player);

        if (result != CanceledException.Result.SUCCESSFUL) {
            if (message == null)
                message = CanceledException.getMessageByResult(result, getMessages());
            callback.error(this, new CanceledException(message, this, result));
            return;
        }

        if (event)
            Bukkit.getPluginManager().callEvent(new CountdownCanceledEvent(this, result));
    }

    @Override
    public boolean isRunning() {
        return task != null && !task.isCancelled();
    }
}
