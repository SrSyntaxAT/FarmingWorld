package at.srsyntax.farmingworld.api.event.countdown;

import at.srsyntax.farmingworld.api.handler.countdown.Countdown;
import at.srsyntax.farmingworld.api.handler.countdown.exception.CanceledException;
import org.bukkit.event.HandlerList;
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

/**
 * The event is fired when the countdown is stopped by an error.
 */
public class CountdownCanceledEvent extends CountdownEvent {

    private static final HandlerList handlers = new HandlerList();

    private final CanceledException.Result result;

    public CountdownCanceledEvent(@NotNull Countdown countdown, @NotNull CanceledException.Result result) {
        super(countdown);
        this.result = result;
    }

    public CountdownCanceledEvent(boolean isAsync, @NotNull Countdown countdown, @NotNull CanceledException.Result result) {
        super(isAsync, countdown);
        this.result = result;
    }


    @NotNull
    public HandlerList getHandlers() {
        return handlers;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return handlers;
    }

    /**
     * Returns the reason why the countdown was canceled.
     * @return the reason why the countdown was canceled.
     */
    public @NotNull CanceledException.Result getResult() {
        return result;
    }
}
