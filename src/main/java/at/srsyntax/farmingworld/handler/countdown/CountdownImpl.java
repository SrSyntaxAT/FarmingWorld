package at.srsyntax.farmingworld.handler.countdown;

import at.srsyntax.farmingworld.FarmingWorldPlugin;
import at.srsyntax.farmingworld.api.handler.HandleException;
import at.srsyntax.farmingworld.api.handler.countdown.AbstractCountdown;
import at.srsyntax.farmingworld.api.handler.countdown.CountdownCallback;
import at.srsyntax.farmingworld.api.handler.countdown.CountdownRunnable;
import at.srsyntax.farmingworld.api.handler.countdown.exception.AlreadyStartedException;
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
public class CountdownImpl extends AbstractCountdown {

    public CountdownImpl(FarmingWorldPlugin plugin, Player player, CountdownCallback callback) {
        super(plugin, player, callback);
    }

    @Override
    public boolean canBypass() { // TODO: 19.05.2023
        return player.hasPermission("farmingworld.bypass.countdown") || player.hasPermission("farmingworld.bypass.*");
    }

    @Override
    public void handle() throws HandleException {
        if (isRunning())
            throw new AlreadyStartedException(plugin.getMessageConfig().getCountdown().getAlreadyStarted(), this);

        super.handle();
    }

    @Override
    protected CountdownRunnable createRunnable() {
        final var messages = plugin.getMessageConfig().getCountdown().toCountdownMessage();
        final var config = plugin.getPluginConfig().getCountdown();
        return new CountdownRunnableImpl(plugin, messages, this, config);
    }
}
