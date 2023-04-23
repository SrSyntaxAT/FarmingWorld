package at.srsyntax.farmingworld.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.md_5.bungee.api.ChatMessageType;

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
@AllArgsConstructor
@Getter
public class MessageConfig {

    private final String notEnoughMoney;
    private final SpawnMessages spawn;
    private final CountdownMessages countdown;
    private final CooldownMessages cooldown;
    private final CommandMessages command;
    private final AdminCommandMessages adminCommand;
    private final TimeMessages time;

    @AllArgsConstructor
    @Getter
    public static class CountdownMessages {
        private final String alreadyStarted, moved, unknown;
        private final String message;
    }

    @AllArgsConstructor
    @Getter
    public static class CooldownMessages {
        private final String hasCooldown;
    }

    @AllArgsConstructor
    @Getter
    public static class CommandMessages {
        private final ChatMessageType chatType;
        private final String disabled;
        private final String playerNotFound, farmWorldNotFound, defaultFarmWorldNotFound, playerOrfarmWorldNotFound;
        private final String noPermission, noPermissionTeleportOther;
        private final String teleported, teleportedOther;
    }

    @AllArgsConstructor
    @Getter
    public static class AdminCommandMessages {
        private final String isNotPlayer, noPermission, usage;
        private final String setspawn, setspawnError;
        private final String noFarmWorlds, farmWorldReset;
        private final String confirm, nothingToConfirm;
        private final String reload, reloadError, countdownCanceled;
        private final String delete, deleteError;
        private final String disable, enable;
    }

    @AllArgsConstructor
    @Getter
    public static class TimeMessages {
        private final String format;

        private final String second, seconds;
        private final String minute, minutes;
        private final String hour, hours;
        private final String day, days;
    }

    @AllArgsConstructor
    @Getter
    public static class SpawnMessages {
        private final ChatMessageType chatType;
        private final String notFound, teleported;
    }
}
