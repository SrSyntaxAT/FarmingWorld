package at.srsyntax.farmingworld.api.handler.countdown.exception;

import at.srsyntax.farmingworld.api.handler.countdown.Countdown;
import lombok.AllArgsConstructor;
import lombok.Getter;
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
 * Signals that the countdown has not elapsed correctly.
 */
public class CanceledException extends CountdownException {

    private final Result result;

    public CanceledException(@NotNull String message, @NotNull Countdown countdown, @NotNull Result result) {
        super(message, countdown);
        this.result = result;
    }

    public CanceledException(@NotNull String message, @NotNull Countdown countdown) {
        super(message, countdown);
        this.result = Result.UNKNOWN;
    }

    public static String getMessageByResult(Result result, Messages messages) {
        return switch (result) {
            case MOVED -> messages.getMoved();
            case RELOAD -> messages.getCanceled();
            default -> messages.getUnknown();
        };
    }

    /**
     * Get the reason why the countdown has been interrupted with an error.
     * @return the reason for the error.
     */
    public Result getResult() {
        return result;
    }

    public enum Result {
        SUCCESSFUL,
        RELOAD,
        QUIT,
        MOVED,
        UNKNOWN
    }

    @AllArgsConstructor
    @Getter
    public static final class Messages {
        private final String moved, canceled, unknown;
    }
}
