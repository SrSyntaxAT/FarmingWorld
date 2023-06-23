package at.srsyntax.farmingworld.api.util;

import at.srsyntax.farmingworld.api.message.Message;
import net.md_5.bungee.api.ChatMessageType;

import java.text.SimpleDateFormat;
import java.util.Date;

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
public class TimeUtil {

    private static final long DAY, HOUR;

    static {
        DAY = 60 * 60 * 24;
        HOUR = 60 * 60;
    }

    private static int[] getRemainingTimeArray(long milli) {
        if (milli < 0) throw new IllegalArgumentException();

        final long currentTime = System.currentTimeMillis();
        long time = currentTime > milli ? currentTime - milli : milli - currentTime;
        int days = 0,
            hours = 0,
            minutes = 0,
            seconds = 0;

        if ((time /= 1000) >= DAY)
            days = (int) (time / DAY);

        if ((time -= days * DAY) > HOUR)
            hours = (int) (time / HOUR);

        if ((time -= hours * HOUR) > 60)
            minutes = (int) (time / 60);

        if ((time -= minutes * 60L) > 0)
            seconds = (int) time;

        return new int[]{days, hours, minutes, seconds};
    }

    public static String getRemainingTime(TimeMessages timeMessage, long time) {
        return getRemainingTime(timeMessage, time, false);
    }

    public static String getRemainingTime(TimeMessages timeMessage, long time, boolean sign) {
        final int[] end = getRemainingTimeArray(time);
        final StringBuilder builder = new StringBuilder();
        int days = end[0],
                hours = end[1],
                minutes = end[2],
                seconds = end[3];

        boolean showSeconds = true, showMinutes = true;

        if (days != 0) {
            builder.append(days == 1 ? days + " <day> " : days + " <days> ");
            if (sign) return  replaceRemaining(timeMessage, builder.toString());
            showSeconds = false;
        }

        if (hours != 0) {
            builder.append(hours == 1 ? hours + " <hour>" : hours + " <hours>");
            if (sign) return  replaceRemaining(timeMessage, builder.toString());
            showSeconds = false;
            if (days != 0) showMinutes = false;
        }

        if (minutes != 0 && showMinutes) {
            if (sign) minutes++;
            builder.append(" ").append(minutes == 1 ? minutes + " <minute>" : minutes + " <minutes>");
            if (sign) return replaceRemaining(timeMessage, builder.toString());
        }

        if (showSeconds)
            builder.append(" ").append(seconds == 1 ? seconds + " <second>" : seconds + " <seconds>");

        return replaceRemaining(timeMessage, builder.toString());
    }

    private static String replaceRemaining(TimeMessages time, String remaining) {
        final Message message = new Message(remaining, ChatMessageType.SYSTEM)
                .replace("<second>", time.getSecond())
                .replace("<seconds>", time.getSeconds())
                .replace("<minute>", time.getMinute())
                .replace("<minutes>", time.getMinutes())
                .replace("<hour>", time.getHour())
                .replace("<hours>", time.getHours())
                .replace("<day>", time.getDay())
                .replace("<days>", time.getDays());
        return message.toString();
    }

    public static String getDate(String format, long date) {
        if (date <= 0) date = System.currentTimeMillis();
        return new SimpleDateFormat(format).format(new Date(date));
    }
}
