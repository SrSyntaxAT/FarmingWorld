package at.srsyntax.farmingworld.command.farming;

import at.srsyntax.farmingworld.FarmingWorldPlugin;
import at.srsyntax.farmingworld.api.API;
import at.srsyntax.farmingworld.api.farmworld.FarmWorld;
import at.srsyntax.farmingworld.api.message.Message;
import at.srsyntax.farmingworld.config.MessageConfig;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
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
@Getter
public class TeleportData {

    private final FarmWorld farmWorld;
    private final Player player;

    public TeleportData(FarmWorld farmWorld, Player player, boolean hasArgs) throws TeleportDataException {
        if (farmWorld == null) throw new TeleportDataException(TeleportDataException.Type.FARM_WORLD, hasArgs);
        if (player == null) throw new TeleportDataException(TeleportDataException.Type.PLAYER, hasArgs);
        this.farmWorld = farmWorld;
        this.player = player;
    }

    public static TeleportData create(MessageConfig.CommandMessages messages, CommandSender commandSender, String[] strings) throws CommandException {
        try {
            final API api = FarmingWorldPlugin.getApi();
            if (strings.length == 0) {
                return new TeleportData(api.getDefaultFarmWorld(), (Player) commandSender, false);
            } else {
                final FarmWorld farmWorld = api.getFarmWorld(strings[0]);
                if (strings.length == 1) {
                    if (farmWorld == null)
                        return new TeleportData(api.getDefaultFarmWorld(), Bukkit.getPlayer(strings[0]), true);
                    return new TeleportData(farmWorld, (Player) commandSender, true);
                } else {
                    return new TeleportData(farmWorld, Bukkit.getPlayer(strings[1]), true);
                }
            }
        } catch (TeleportDataException exception) {
            final Message message;
            if (strings.length > 1) {
                message = new Message(exception.getMessage(messages), messages.getChatType());;

                if (exception.getType() == TeleportDataException.Type.PLAYER)
                    message.replace("%{player}", strings[strings.length == 2 ? 1 : 0]);
                if (exception.getType() == TeleportDataException.Type.FARM_WORLD)
                    message.replace("%{farm_world}", strings[0]);
            } else if (strings.length == 1) {
                message = new Message(messages.getPlayerOrfarmWorldNotFound(), messages.getChatType());
                message.replace("%{value}", strings[0]);
            } else {
                message = new Message(messages.getDefaultFarmWorldNotFound(), messages.getChatType());
            }

            throw new CommandException(message);
        }
    }

    @AllArgsConstructor
    @Getter
    public static class TeleportDataException extends Exception {

        private final Type type;
        private final boolean hasArgs;

        public String getMessage(MessageConfig.CommandMessages messages) {
            if (!hasArgs && type == Type.FARM_WORLD) return messages.getDefaultFarmWorldNotFound();
            return type == Type.FARM_WORLD ? messages.getFarmWorldNotFound() : messages.getPlayerNotFound();
        }

        public enum Type {
            FARM_WORLD, PLAYER
        }
    }
}
