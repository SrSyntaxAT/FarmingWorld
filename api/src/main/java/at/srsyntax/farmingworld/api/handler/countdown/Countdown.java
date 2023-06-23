package at.srsyntax.farmingworld.api.handler.countdown;

import at.srsyntax.farmingworld.api.handler.Handler;
import at.srsyntax.farmingworld.api.handler.countdown.exception.CanceledException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
 * Represents the countdown.
 */
public interface Countdown extends Handler {

    /**
     * End the countdown.
     * This triggers the callback and event, unregisters the countdown in the registry, and ends the task.
     * @see at.srsyntax.farmingworld.api.event.countdown.CountdownFinishedEvent
     */
    void finish();

    /**
     * Interrupt the countdown.
     * @param event - Should the CountdownCanceledEvent be fired?
     * @param message Which one should be sent.
     * @param result - The reason why the interrupted.
     * @see at.srsyntax.farmingworld.api.event.countdown.CountdownCanceledEvent
     * @see CanceledException
     */
    void cancel(boolean event, @Nullable String message, @NotNull CanceledException.Result result);

    /**
     * Returns whether the countdown is currently running.
     * @return whether the countdown is currently running.
     */
    boolean isRunning();
}
