package at.srsyntax.farmingworld.command;

import at.srsyntax.farmingworld.FarmingWorldPlugin;
import at.srsyntax.farmingworld.api.handler.HandleException;
import at.srsyntax.farmingworld.api.handler.countdown.Countdown;
import at.srsyntax.farmingworld.api.handler.countdown.CountdownCallback;
import at.srsyntax.farmingworld.api.message.Message;
import at.srsyntax.farmingworld.config.MessageConfig;
import at.srsyntax.farmingworld.config.PluginConfig;
import lombok.Setter;
import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

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
public class SpawnCommand extends Command {

    @Setter private Location location;
    private final MessageConfig.SpawnMessages spawnMessages;
    private final MessageConfig.CountdownMessages countdownMessages;

    public SpawnCommand(PluginConfig config, MessageConfig messageConfig) {
        super("spawn", "Teleport yourself to the spawn.", "/spawn", new ArrayList<>());
        this.location = config.getFallback().toBukkit();
        this.spawnMessages = messageConfig.getSpawn();
        this.countdownMessages = messageConfig.getCountdown();
    }

    @Override
    public boolean execute(@NotNull CommandSender commandSender, @NotNull String s, @NotNull String[] strings) {
        try {
            if (commandSender instanceof Player player) {
                if (location == null) {
                    new Message(spawnMessages.getNotFound(), ChatMessageType.SYSTEM).send(player);
                    return false;
                }

                final var countdown = FarmingWorldPlugin.getApi().getCountdown(player, teleportPlayer(player));
                if (countdown.isRunning()) throw new HandleException(countdownMessages.getAlreadyStarted());
                countdown.handle();

                return true;
            }
        } catch (HandleException exception) {
            new Message(exception.getMessage(), ChatMessageType.SYSTEM)
                    .send(commandSender);
        }
        return false;
    }

    private CountdownCallback teleportPlayer(Player player) {
        return new CountdownCallback() {
            @Override
            public void finished(Countdown countdown) {
                player.teleport(location);
                new Message(spawnMessages.getTeleported(), spawnMessages.getChatType()).send(player);
            }

            @Override
            public void error(Countdown countdown, Throwable throwable) {
                new Message(throwable.getMessage(), spawnMessages.getChatType()).send(player);
            }
        };
    }
}
