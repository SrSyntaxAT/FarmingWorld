package at.srsyntax.farmingworld.command;

import at.srsyntax.farmingworld.APIImpl;
import at.srsyntax.farmingworld.api.handler.HandleException;
import at.srsyntax.farmingworld.api.handler.cooldown.Cooldown;
import at.srsyntax.farmingworld.api.handler.countdown.Countdown;
import at.srsyntax.farmingworld.api.handler.countdown.CountdownCallback;
import at.srsyntax.farmingworld.api.handler.economy.Economy;
import at.srsyntax.farmingworld.api.message.Message;
import at.srsyntax.farmingworld.command.farming.CommandException;
import at.srsyntax.farmingworld.command.farming.TeleportData;
import at.srsyntax.farmingworld.config.MessageConfig;
import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;

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
public abstract class FarmWorldTeleportCommand implements CommandExecutor {

    protected final APIImpl api;
    protected final MessageConfig messages;
    protected final MessageConfig.CommandMessages commandMessages;

    public FarmWorldTeleportCommand(APIImpl api, MessageConfig messages, MessageConfig.CommandMessages commandMessages) {
        this.api = api;
        this.messages = messages;
        this.commandMessages = commandMessages;
    }

    protected void checkPermission(Player sender, TeleportData data) throws CommandException {
        final ChatMessageType chatType = commandMessages.getChatType();
        if (data.getPlayer().equals(sender) && !data.getFarmWorld().hasPermission(sender)) {
            throw new CommandException(new Message(commandMessages.getNoPermission(), chatType));
        } else if (!data.getPlayer().equals(sender)) {
            if (!sender.hasPermission("farmingworld.teleport.other")) {
                throw new CommandException(new Message(commandMessages.getNoPermission(), chatType));
            } else if (!data.getFarmWorld().hasPermission(sender)) {
                throw new CommandException(new Message(commandMessages.getNoPermissionTeleportOther(), chatType));
            }
        }
    }

    protected CountdownCallback teleportPlayer(Player sender, TeleportData data, Cooldown cooldown) throws HandleException {
        return new CountdownCallback() {
            @Override
            public void finished(Countdown countdown) {
                teleport(data);

                new Message(commandMessages.getTeleported(), commandMessages.getChatType())
                        .replace("%{farmworld}", data.getFarmWorld().getName())
                        .send(data.getPlayer());

                if (!sender.equals(data.getPlayer()))
                    new Message(commandMessages.getTeleportedOther(), commandMessages.getChatType())
                            .replace("%{farmworld}", data.getFarmWorld().getName())
                            .replace("%{player}", data.getPlayer().getName())
                            .send(sender);
            }

            @Override
            public void error(Countdown countdown, Throwable throwable) {
                cooldown.remove();
                new Message(throwable.getMessage(), commandMessages.getChatType()).send(sender);
            }
        };
    }

    public abstract void teleport(TeleportData data);

    protected void callHandlers(TeleportData data, Economy economy) throws HandleException {
        final var cooldown = api.getCooldown(data.getPlayer(), data.getFarmWorld());
        final var countdown = api.getCountdown(data.getPlayer(), teleportPlayer(data.getPlayer(), data, cooldown));

        if (countdown.isRunning()) throw new HandleException(messages.getCountdown().getAlreadyStarted());
        if (economy != null) economy.handle();
        cooldown.handle();
        countdown.handle();
    }

    protected void refund(Economy economy) {
        if (economy == null) return;
        economy.refund();
    }
}
