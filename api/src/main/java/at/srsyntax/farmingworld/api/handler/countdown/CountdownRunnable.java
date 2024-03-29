package at.srsyntax.farmingworld.api.handler.countdown;

import at.srsyntax.farmingworld.api.handler.countdown.exception.CanceledException;
import at.srsyntax.farmingworld.api.message.Message;

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
public class CountdownRunnable implements Runnable {

    private final CountdownMessage messages;
    protected final Countdown countdown;

    protected int time;

    public CountdownRunnable(CountdownMessage messages, Countdown countdown, int time) {
        this.messages = messages;
        this.countdown = countdown;
        this.time = time;
    }

    @Override
    public void run() {
        try {
            check();

            if (time != 0) {
                new Message(messages.message(), messages.messageType())
                        .replace("%v", time)
                        .send(countdown.getPlayer());
                time--;
            } else {
                countdown.finish();
            }
        } catch (CanceledException exception) {
            countdown.cancel(true, exception.getMessage(), exception.getResult());
        }
    }

    public void check() throws CanceledException {}
}
