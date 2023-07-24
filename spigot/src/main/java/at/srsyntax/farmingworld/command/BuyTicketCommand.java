package at.srsyntax.farmingworld.command;

import at.srsyntax.farmingworld.APIImpl;
import at.srsyntax.farmingworld.FarmingWorldPlugin;
import at.srsyntax.farmingworld.api.handler.HandleException;
import at.srsyntax.farmingworld.api.message.Message;
import at.srsyntax.farmingworld.command.farming.CommandException;
import at.srsyntax.farmingworld.config.MessageConfig;
import at.srsyntax.farmingworld.ticket.TicketEconomyHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/*
 * CONFIDENTIAL
 *  Unpublished Copyright (c) 2023 Marcel Haberl, All Rights Reserved.
 *
 * NOTICE:
 * All information contained herein is, and remains the property of Marcel Haberl. The intellectual and
 * technical concepts contained herein are proprietary to Marcel Haberl and may be covered by U.S. and Foreign
 * Patents, patents in process, and are protected by trade secret or copyright law. Dissemination of this
 * information or reproduction of this material is strictly forbidden unless prior written permission is obtained
 * from Marcel Haberl.  Access to the source code contained herein is hereby forbidden to anyone without written
 * permission Confidentiality and Non-disclosure agreements explicitly covering such access.
 *
 * The copyright notice above does not evidence any actual or intended publication or disclosure  of  this source code,
 * which includes information that is confidential and/or proprietary, and is a trade secret, of Marcel Haberl.
 * ANY REPRODUCTION, MODIFICATION, DISTRIBUTION, PUBLIC  PERFORMANCE, OR PUBLIC DISPLAY OF OR THROUGH USE  OF THIS
 * SOURCE CODE  WITHOUT  THE EXPRESS WRITTEN CONSENT OF Marcel Haberl IS STRICTLY PROHIBITED, AND IN VIOLATION OF
 * APPLICABLE LAWS AND INTERNATIONAL TREATIES.  THE RECEIPT OR POSSESSION OF  THIS SOURCE CODE AND/OR RELATED
 * INFORMATION DOES NOT CONVEY OR IMPLY ANY RIGHTS TO REPRODUCE, DISCLOSE OR DISTRIBUTE ITS CONTENTS, OR TO
 * MANUFACTURE, USE, OR SELL ANYTHING THAT IT  MAY DESCRIBE, IN WHOLE OR IN PART.
 */
public class BuyTicketCommand extends Command implements TabCompleterFilter {

    private final MessageConfig messages;

    public BuyTicketCommand(@NotNull String name, MessageConfig messages) {
        super(name);
        this.messages = messages;
    }

    @Override
    public boolean execute(@NotNull CommandSender commandSender, @NotNull String s, @NotNull String[] strings) {
        try {
            if (!(commandSender instanceof Player player))
                throw new BuyTicketException(messages.getCommand().getMustBeAPlayer());

            if (strings.length == 0)
                throw new BuyTicketException(messages.getBuyTicketCommand().getUsage());

            final var api = FarmingWorldPlugin.getApi();
            final var farmWorld = api.getFarmWorld(strings[0]);
            if (farmWorld == null)
                throw new BuyTicketException(messages.getCommand().getFarmWorldNotFound());

            final var permission = "farmworld.buyticket.";
            if (!(player.hasPermission(permission + "*") || player.hasPermission(permission + farmWorld.getName())))
                throw new BuyTicketException(messages.getBuyTicketCommand().getNoPermission());

            final var plugin = ((APIImpl) FarmingWorldPlugin.getApi()).getPlugin();
            final var handler = new TicketEconomyHandler(plugin, player, farmWorld.getPrice());
            handler.handle();

            new Message(messages.getBuyTicketCommand().getMessage()).replace("%s", farmWorld.getName()).send(player);
            final var ticket = api.createTicket(farmWorld);
            ticket.giveItem(player);
        } catch (CommandException exception) {
            exception.getMessages().send(commandSender);
        } catch (HandleException exception) {
            new Message(messages.getNotEnoughMoney()).send(commandSender);
        }
        return false;
    }

    @NotNull
    @Override
    public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
        if (args.length != 1) return List.of();
        return filterFarmWorlds(args[0]);
    }

    private final class BuyTicketException extends CommandException {

        public BuyTicketException(Message messages) {
            super(messages);
        }


        public BuyTicketException(String message) {
            this(new Message(message));
        }
    }
}
