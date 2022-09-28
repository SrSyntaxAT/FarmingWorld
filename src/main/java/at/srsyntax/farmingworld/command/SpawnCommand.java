package at.srsyntax.farmingworld.command;

import at.srsyntax.farmingworld.FarmingWorldPlugin;
import at.srsyntax.farmingworld.api.message.Message;
import at.srsyntax.farmingworld.config.MessageConfig;
import at.srsyntax.farmingworld.config.SpawnConfig;
import at.srsyntax.farmingworld.countdown.Countdown;
import at.srsyntax.farmingworld.countdown.CountdownCallback;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

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
public class SpawnCommand extends Command {

    private final FarmingWorldPlugin plugin;
    private final MessageConfig messageConfig;

    public SpawnCommand(@NotNull String name, FarmingWorldPlugin plugin) {
        super(name, "Teleport you to the spawn.", "/" + name, new ArrayList<>());
        this.plugin = plugin;
        this.messageConfig = plugin.getPluginConfig().getMessage();
    }

    @Override
    public boolean execute(@NotNull CommandSender commandSender, @NotNull String s, @NotNull String[] strings) {
        try {
            final SpawnConfig config = plugin.getPluginConfig().getSpawn();
            canTeleport(commandSender, config);

            final Player player = (Player) commandSender;
            final var callback = createCallback(player, config.getLocation().toBukkit());
            if (config.getCountdown() > 0)
                new Countdown(plugin, callback, player).start(config.getCountdown());
            else
                callback.done();

            return true;
        } catch (Exception exception) {
            exception.printStackTrace();
            commandSender.sendMessage(exception.getMessage());
            return false;
        }
    }

    private void canTeleport(CommandSender sender, SpawnConfig config) {
        if (!(sender instanceof Player))
            throw new RuntimeException(new Message(messageConfig.getOnlyPlayers()).replace());
        if (!config.isEnabled())
            throw new RuntimeException(new Message(messageConfig.getSpawnDisabledError()).replace());
    }

    private CountdownCallback createCallback(Player player, Location location) {
        return () -> {
            Bukkit.getScheduler().runTask(plugin, () -> {
                player.teleport(location);
                player.sendMessage(new Message(messageConfig.getSpawnTeleported()).replace());
            });
        };
    }
}
