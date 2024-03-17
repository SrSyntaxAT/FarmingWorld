package at.srsyntax.farmingworld.command;

import at.srsyntax.farmingworld.APIImpl;
import at.srsyntax.farmingworld.api.handler.HandleException;
import at.srsyntax.farmingworld.api.handler.economy.Economy;
import at.srsyntax.farmingworld.api.message.Message;
import at.srsyntax.farmingworld.command.farming.CommandException;
import at.srsyntax.farmingworld.command.farming.TeleportData;
import at.srsyntax.farmingworld.config.MessageConfig;
import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

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
public class RTPCommand extends FarmWorldTeleportCommand implements CommandExecutor {

    private final boolean fee;

    public RTPCommand(APIImpl api, MessageConfig config, boolean fee) {
        super(api, config, config.getCommand());
        this.fee = fee;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        Economy economy = null;

        try {
            if (!(commandSender instanceof Player sender))
                throw new CommandException(new Message(messages.getCommand().getMustBeAPlayer()));

            final var farmworld = api.getFarmWorld(sender.getWorld());
            if (farmworld == null) throw new CommandException(new Message(messages.getNotOnAFarmWorld()));
            final TeleportData data = new TeleportData(farmworld, sender);
            checkPermission(sender, data);

            if (!data.getFarmWorld().isActive()) throw new HandleException(messages.getCommand().getDisabled());

            economy = fee ? api.createEconomy(data.getFarmWorld(), data.getPlayer()) : null;
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
}
