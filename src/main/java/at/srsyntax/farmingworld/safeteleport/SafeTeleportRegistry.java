package at.srsyntax.farmingworld.safeteleport;

import at.srsyntax.farmingworld.FarmingWorldPlugin;
import at.srsyntax.farmingworld.api.handler.HandleException;
import at.srsyntax.farmingworld.api.handler.countdown.Countdown;
import at.srsyntax.farmingworld.api.handler.countdown.CountdownCallback;
import at.srsyntax.farmingworld.api.handler.countdown.exception.CanceledException;
import at.srsyntax.farmingworld.api.message.Message;
import at.srsyntax.farmingworld.config.MessageConfig;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
public class SafeTeleportRegistry {

    private final FarmingWorldPlugin plugin;
    private final MessageConfig.SafeTeleportMessages messages;

    private final Map<Player, Countdown> countdownMap = new ConcurrentHashMap<>();

    public SafeTeleportRegistry(FarmingWorldPlugin plugin) {
        this.plugin = plugin;
        this.messages = plugin.getMessageConfig().getSafeTeleport();
        registerListener();
    }

    private void registerListener() {
        final var canDamagePlayers = plugin.getPluginConfig().getSafeTeleport().isCanDamagePlayers();
        final var listener = new SafeTeleportListener(this, canDamagePlayers);
        plugin.getServer().getPluginManager().registerEvents(listener, plugin);
    }

    public void register(Player player) {
        if (!player.hasPermission("farmingworld.safeteleport")) return;

        try {
            final var countdown = new SafeTeleportCountdown(plugin, player, createCountdownCallback(player));
            countdown.handle();
            countdownMap.put(player, countdown);
        } catch (HandleException exception) {
            new Message(exception.getMessage(), messages.getMessageType()).send(player);
        }
    }

    public void unregister(Player player, CanceledException.Result result) {
        if (!countdownMap.containsKey(player)) return;
        final var countdown = countdownMap.remove(player);
        if (!countdown.isRunning()) return;
        countdown.cancel(true, CanceledException.getMessageByResult(result, plugin), result);
    }

    private CountdownCallback createCountdownCallback(Player player) {
        return new CountdownCallback() {
            @Override
            public void finished(Countdown countdown) {
                unregister(player, CanceledException.Result.SUCCESSFUL);
                new Message(messages.getFinish(), messages.getMessageType())
                        .send(player);
            }

            @Override
            public void error(Countdown countdown, Throwable throwable) {
                unregister(player, CanceledException.Result.UNKNOWN);
                new Message(messages.getFinish(), messages.getMessageType())
                        .send(player);
            }
        };
    }

    public boolean isInvulnerable(Player player) {
        return countdownMap.containsKey(player);
    }
}
