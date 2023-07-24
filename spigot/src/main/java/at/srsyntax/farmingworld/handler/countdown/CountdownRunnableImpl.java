package at.srsyntax.farmingworld.handler.countdown;

import at.srsyntax.farmingworld.FarmingWorldPlugin;
import at.srsyntax.farmingworld.api.handler.countdown.AbstractCountdown;
import at.srsyntax.farmingworld.api.handler.countdown.Countdown;
import at.srsyntax.farmingworld.api.handler.countdown.CountdownMessage;
import at.srsyntax.farmingworld.api.handler.countdown.CountdownRunnable;
import at.srsyntax.farmingworld.api.handler.countdown.exception.CanceledException;
import at.srsyntax.farmingworld.config.PluginConfig;
import org.bukkit.Location;

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
public class CountdownRunnableImpl extends CountdownRunnable {

    private final Location location;
    private final double permittedDistance;

    public CountdownRunnableImpl(FarmingWorldPlugin plugin, CountdownMessage messages, Countdown countdown, PluginConfig.CountdownConfig config) {
        super(messages, countdown, config.getTime());
        this.location = config.isMovementAllowed() ? null : countdown.getPlayer().getLocation().clone();
        this.permittedDistance = config.getPermittedDistance();
    }

    @Override
    public void check() throws CanceledException {
        if (location != null && location.distance(countdown.getPlayer().getLocation()) > permittedDistance) {
            final var messages = ((AbstractCountdown) countdown).getMessages();
            final var message = CanceledException.getMessageByResult(CanceledException.Result.MOVED, messages);
            throw new CanceledException(message, countdown, CanceledException.Result.MOVED);
        }
    }
}
