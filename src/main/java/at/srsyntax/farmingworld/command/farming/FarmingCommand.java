package at.srsyntax.farmingworld.command.farming;

import at.srsyntax.farmingworld.api.API;
import at.srsyntax.farmingworld.api.farmworld.FarmWorld;
import at.srsyntax.farmingworld.api.handler.HandleException;
import at.srsyntax.farmingworld.api.handler.countdown.Countdown;
import at.srsyntax.farmingworld.api.handler.countdown.CountdownCallback;
import at.srsyntax.farmingworld.api.message.Message;
import at.srsyntax.farmingworld.config.MessageConfig;
import lombok.AllArgsConstructor;
import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

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
@AllArgsConstructor
public class FarmingCommand implements CommandExecutor, TabCompleter {

    private final API api;
    private final MessageConfig.CommandMessages messages;

    //               0           1
    // farming [world/player] [player]
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (commandSender instanceof Player sender) {

            try {
                final TeleportData data = TeleportData.create(messages, commandSender, strings);
                checkPermission(sender, data);
                // todo api.getCooldown(data.getPlayer(), data.getFarmWorld()).handle();
                api.getCountdown(data.getPlayer(), teleportPlayer(sender, data)).handle();
            } catch (CommandException exception) {
                exception.getMessages().send(sender);
            } catch (HandleException exception) {
                new Message(exception.getMessage(), ChatMessageType.SYSTEM)
                        .send((Player) commandSender);
            }
        }
        return false;
    }

    private void checkPermission(Player sender, TeleportData data) throws CommandException {
        final ChatMessageType chatType = messages.getChatType();
        if (data.getPlayer().equals(sender) && !data.getFarmWorld().hasPermission(sender)) {
            throw new CommandException(new Message(messages.getNoPermission(), chatType));
        } else if (!data.getPlayer().equals(sender)) {
            if (!sender.hasPermission("farmworld.teleport.other")) {
                throw new CommandException(new Message(messages.getNoPermission(), chatType));
            } else if (!data.getFarmWorld().hasPermission(sender)) {
                throw new CommandException(new Message(messages.getNoPermissionTeleportOther(), chatType));
            }
        }

    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        List<String> result = new ArrayList<>();

        if (strings.length == 1) {
            final String arg = strings[0].toLowerCase();

            result.addAll(filterOnlinePlayers(arg));

            for (FarmWorld farmWorld : api.getFarmWorlds()) {
                if (farmWorld.getName().toLowerCase().startsWith(arg))
                    result.add(farmWorld.getName());
            }
        } else if (strings.length == 2) {
            result.addAll(filterOnlinePlayers(strings[1]));
        }

        return result;
    }

    private List<String> filterOnlinePlayers(String arg) {
        final List<String> names = new ArrayList<>();

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getName().toLowerCase().startsWith(arg.toLowerCase()))
                names.add(player.getName());
        }

        return names;
    }

    private CountdownCallback teleportPlayer(Player sender, TeleportData data) throws HandleException {
        return new CountdownCallback() {
            @Override
            public void finished(Countdown countdown) {
                data.getFarmWorld().teleport(data.getPlayer());

               new Message(messages.getTeleported(), messages.getChatType())
                        .replace("%{farmworld}", data.getFarmWorld().getName())
                        .send(data.getPlayer());

                if (!sender.equals(data.getPlayer()))
                    new Message(messages.getTeleportedOther(), messages.getChatType())
                            .replace("%{farmworld}", data.getFarmWorld().getName())
                            .replace("%{player}", data.getPlayer().getName())
                            .send(sender);
            }

            @Override
            public void error(Countdown countdown, Throwable throwable) {
                new Message(throwable.getMessage(), ChatMessageType.SYSTEM).send(sender);
            }
        };
    }
}
