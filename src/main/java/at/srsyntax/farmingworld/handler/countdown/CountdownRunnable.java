package at.srsyntax.farmingworld.handler.countdown;

import at.srsyntax.farmingworld.api.handler.countdown.Countdown;
import at.srsyntax.farmingworld.api.handler.countdown.exception.CanceledException;
import at.srsyntax.farmingworld.api.message.Message;
import at.srsyntax.farmingworld.config.MessageConfig;
import at.srsyntax.farmingworld.config.PluginConfig;
import org.bukkit.Location;

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
public class CountdownRunnable implements Runnable {

    private final MessageConfig.CountdownMessages messages;
    private final Countdown countdown;
    private final PluginConfig.CountdownConfig config;

    private int time;
    private final Location location;

    public CountdownRunnable(MessageConfig.CountdownMessages messages, Countdown countdown, PluginConfig.CountdownConfig config) {
        this.messages = messages;
        this.countdown = countdown;
        this.config = config;
        this.time = config.getTime();
        this.location = config.isMovementAllowed() ? null : countdown.getPlayer().getLocation().clone();
    }

    @Override
    public void run() {
        if (location != null && location.distance(countdown.getPlayer().getLocation()) > config.getPermittedDistance()) {
            countdown.cancel(true, CanceledException.Result.MOVED);
            return;
        }

        if (time != 0) {
            new Message(messages.getMessage(), config.getMessageType())
                    .replace("%s", time)
                    .send(countdown.getPlayer());
            time--;
        } else {
            countdown.finish();
        }
    }
}
