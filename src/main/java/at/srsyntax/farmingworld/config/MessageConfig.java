package at.srsyntax.farmingworld.config;

import at.srsyntax.farmingworld.api.handler.countdown.CountdownMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.md_5.bungee.api.ChatMessageType;

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
@Getter
public class MessageConfig extends Config {

    private transient final String fileName = "messages.json";

    private final String notEnoughMoney;
    private final SpawnMessages spawn;
    private final CountdownMessages countdown;
    private final CooldownMessages cooldown;
    private final CommandMessages command;
    private final AdminCommandMessages adminCommand;
    private final TimeMessages time;

    public MessageConfig(String notEnoughMoney, SpawnMessages spawn, CountdownMessages countdown, CooldownMessages cooldown, CommandMessages command, AdminCommandMessages adminCommand, TimeMessages time) {
        this.notEnoughMoney = notEnoughMoney;
        this.spawn = spawn;
        this.countdown = countdown;
        this.cooldown = cooldown;
        this.command = command;
        this.adminCommand = adminCommand;
        this.time = time;
    }

    public MessageConfig() {
        this(
                "&cYou don't have enough money.",
                new MessageConfig.SpawnMessages(
                        ChatMessageType.ACTION_BAR,
                        "&cNo spawn was found!",
                        "&aYou have been teleported to the spawn."
                ),
                new MessageConfig.CountdownMessages(
                        ChatMessageType.ACTION_BAR,
                        "&cA countdown is already running.",
                        "&cThe countdown was interrupted because you moved.",
                        "&cThe countdown was canceled for an unknown reason.",
                        "&7You will be teleported in &e%v &7seconds."
                ),
                new MessageConfig.CooldownMessages(
                        "&cYou may use the command in &e%{remaining}&7."
                ),
                new MessageConfig.CommandMessages(
                        ChatMessageType.ACTION_BAR,
                        "&cThe farm world is disabled.",
                        "&cPlayer not found!",
                        "&cFarm world not found!",
                        "&cFarm world was not found.",
                        "&cPlayer or farm world was not found.",
                        "&cYou have no rights to enter the farm world.",
                        "&cYou have no rights to teleport other players to the farm world.",
                        "&aYou have been teleported to &e%{farmworld}&a.",
                        "&aYou teleported &e%{player} &ato &e%{farmworld}&a."
                ),
                new MessageConfig.AdminCommandMessages(
                        "&cThe sender must be a player.",
                        "&cYou have no rights to do this.",
                        "&cUsage&8: &f/fwa %s",
                        "&aThe spawn was set.",
                        "&cThere was an error while setting the spawn.",
                        "&cNo farm worlds found.",
                        "&aThe farm world has been reset.",
                        "&fConfirm the action within &a10 seconds &fwith &e/fwa confirm&f.",
                        "&cThe confirmation request has expired or none was found for you.",
                        "&cConfigurations will be reloaded.",
                        "&aThe configuratuion has been reloaded.",
                        "&cAn error occurred while reloading the configuration.",
                        "&cThe countdown has been stopped as the farm worlds are reloaded.",
                        "&cFarm world was deleted.",
                        "&cAn error occurred while deleting the farm world.",
                        "&eFarm world &chas been disabled.",
                        "&eFarm world &ahas been enabled.",
                        new String[]{
                                "&6&l%{name} &r&einformations&8:",
                                "&eActive&8:&7%{active}",
                                "&ePermission&8:&7%{permission}",
                                "&eAliases&8:&7%{aliases}",
                                "&eWorld&8:&7%{world}",
                                "&eReset&8:&7%{reset-date}",
                                "&eEnvironment&8:&7%{environment}",
                                "&eGenerator&8:&7%{generator}",
                                "&ePlayers&8:&7%{players}",
                                "&eSigns&8:&7%{signs}"
                        },
                        "&ePlayers on &e&l%{name} &7(%{size})&8: %{list}",
                        "&eSigns for &e&l%{name} &7(%{size})&8: %{list}"
                ),
                new MessageConfig.TimeMessages(
                        "HH:mm:ss dd.MM.yyyy",
                        "second", "seconds",
                        "minute", "minutes",
                        "hour", "hours",
                        "day", "days"
                )
        );
    }


    @AllArgsConstructor
    @Getter
    public static class CountdownMessages {
        private final ChatMessageType messageType;
        private final String alreadyStarted, moved, unknown;
        private final String message;

        public CountdownMessage toCountdownMessage() {
            return new CountdownMessage(messageType, message);
        }
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
        private final String reload, reloadFinish, reloadError, countdownCanceled;
        private final String delete, deleteError;
        private final String disable, enable;
        private final String[] info;
        private final String infoPlayers, infoSigns;
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
