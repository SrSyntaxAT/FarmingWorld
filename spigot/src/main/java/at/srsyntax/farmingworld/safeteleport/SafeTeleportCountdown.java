package at.srsyntax.farmingworld.safeteleport;

import at.srsyntax.farmingworld.FarmingWorldPlugin;
import at.srsyntax.farmingworld.api.handler.countdown.AbstractCountdown;
import at.srsyntax.farmingworld.api.handler.countdown.CountdownCallback;
import at.srsyntax.farmingworld.api.handler.countdown.CountdownMessage;
import at.srsyntax.farmingworld.api.handler.countdown.CountdownRunnable;
import at.srsyntax.farmingworld.api.handler.countdown.exception.CanceledException;
import at.srsyntax.farmingworld.config.MessageConfig;
import at.srsyntax.farmingworld.handler.countdown.CountdownMessages;
import org.bukkit.entity.Player;

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
public class SafeTeleportCountdown extends AbstractCountdown implements CountdownMessages {

    private final MessageConfig.SafeTeleportMessages messages;

    public SafeTeleportCountdown(FarmingWorldPlugin plugin, Player player, CountdownCallback callback) {
        super(plugin, player, callback, plugin.getSafeTeleportRegistry());
        this.messages = plugin.getMessageConfig().getSafeTeleport();
    }

    @Override
    public boolean canBypass() {
        return false;
    }

    @Override
    protected CountdownRunnable createRunnable() {
        final var countdownMessage = new CountdownMessage(messages.getMessageType(), messages.getCountdown());
        final var plugin = (FarmingWorldPlugin) this.plugin;
        return new CountdownRunnable(countdownMessage, this, plugin.getPluginConfig().getSafeTeleport().getTime());
    }

    @Override
    public CanceledException.Messages getMessages() {
        return getMessages((FarmingWorldPlugin) plugin);
    }
}
