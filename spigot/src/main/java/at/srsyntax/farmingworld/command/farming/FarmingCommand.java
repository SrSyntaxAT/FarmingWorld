package at.srsyntax.farmingworld.command.farming;

import at.srsyntax.farmingworld.APIImpl;
import at.srsyntax.farmingworld.api.handler.HandleException;
import at.srsyntax.farmingworld.api.handler.economy.Economy;
import at.srsyntax.farmingworld.api.message.Message;
import at.srsyntax.farmingworld.command.FarmWorldTeleportCommand;
import at.srsyntax.farmingworld.command.TabCompleterFilter;
import at.srsyntax.farmingworld.config.MessageConfig;
import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.command.Command;
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
 * Copyright (c) 2022-2024 Marcel Haberl
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
public class FarmingCommand extends FarmWorldTeleportCommand implements TabCompleter, TabCompleterFilter {

    public FarmingCommand(APIImpl api, MessageConfig config) {
        super(api, config, config.getCommand());
    }

    //               0           1
    // farming [world/player] [player]
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        Economy economy = null;

        try {
            if (!(commandSender instanceof Player sender))
                throw new CommandException(new Message(messages.getCommand().getMustBeAPlayer()));

            final TeleportData data = TeleportData.create(commandMessages, commandSender, strings);
            checkPermission(sender, data);

            if (!data.getFarmWorld().isActive()) throw new HandleException(messages.getCommand().getDisabled());

            economy = api.createEconomy(data.getFarmWorld(), data.getPlayer());
            callHandlers(data, economy);

            return true;
        } catch (CommandException exception) {
            exception.getMessages().send(commandSender);
            refund(economy);
        } catch (HandleException exception) {
            new Message(exception.getMessage(), ChatMessageType.SYSTEM)
                    .send(commandSender);
            refund(economy);
        }
        return false;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        final List<String> result = new ArrayList<>();

        if (strings.length == 1) {
            final String arg = strings[0];
            result.addAll(filterOnlinePlayers(arg));
            result.addAll(filterFarmWorlds(arg));
        } else if (strings.length == 2) {
            result.addAll(filterOnlinePlayers(strings[1]));
        }

        return result;
    }

    @Override
    public void teleport(TeleportData data) {
        data.getFarmWorld().teleportSpawn(data.getPlayer());
    }
}
