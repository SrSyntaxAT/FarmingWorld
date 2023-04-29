package at.srsyntax.farmingworld.command.admin.sub;

import at.srsyntax.farmingworld.APIImpl;
import at.srsyntax.farmingworld.api.farmworld.FarmWorld;
import at.srsyntax.farmingworld.api.message.Message;
import at.srsyntax.farmingworld.command.TabCompleterFilter;
import at.srsyntax.farmingworld.command.admin.FarmWorldSubCommand;
import at.srsyntax.farmingworld.config.MessageConfig;
import at.srsyntax.farmingworld.farmworld.FarmWorldImpl;
import at.srsyntax.farmingworld.farmworld.sign.SignCacheImpl;
import at.srsyntax.farmingworld.util.TimeUtil;
import org.bukkit.command.CommandSender;

import java.util.*;

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
public class InfoSubCommand extends FarmWorldSubCommand implements TabCompleterFilter {

    public InfoSubCommand(String usage, MessageConfig.AdminCommandMessages messages, APIImpl api) {
        super(usage, messages, api);
    }

    @Override
    public void execute(CommandSender sender, String[] args) throws Exception {
        final var farmWorld = getFarmWorld(args, 1);

        if (args.length >= 3) {
            if (args[2].equalsIgnoreCase("signs")) {
                sendSigns(sender, farmWorld);
                return;
            } else if (args[2].equalsIgnoreCase("players")) {
                sendPlayers(sender, farmWorld);
                return;
            }
        }

        sendInfo(sender, (FarmWorldImpl) farmWorld);
    }

    @Override
    public List<String> tabCompleter(CommandSender sender, String[] args) {
        if (args.length == 2) return filterFarmWorlds(args[1]);
        if (args.length == 3) return Arrays.asList("signs", "players");
        return Collections.emptyList();
    }

    private void sendInfo(CommandSender sender, FarmWorldImpl farmWorld) {
        final long reset = farmWorld.getResetDate();
        final String dateFormat = farmWorld.getPlugin().getPluginConfig().getResetDisplay().getDateFormat();

        new Message(arrayToString(messages.getInfo(), false))
                .replace("%{name}", farmWorld.getName())
                .replace("%{active}", farmWorld.isActive())
                .replace("%{permission}", farmWorld.getPermission() == null ? "no permission" : farmWorld.getPermission())
                .replace("%{aliases}", arrayToString(farmWorld.getAliases().toArray(new String[0]), true))
                .replace("%{world}", farmWorld.getWorld() == null ? "&cno world" : farmWorld.getWorld().getName())
                .replace("%{reset-date}", TimeUtil.getDate(dateFormat, reset))
                .replace("%{reset-remaining}", TimeUtil.getRemainingTime(farmWorld.getPlugin(), reset, true))
                .replace("%{environment}", farmWorld.getEnvironment())
                .replace("%{generator}", farmWorld.getGenerator() == null ? "default" : farmWorld.getGenerator())
                .replace("%{players}", farmWorld.getPlayers().size())
                .replace("%{signs}", farmWorld.getSigns())
                .send(sender);
    }

    private void sendSigns(CommandSender sender, FarmWorld farmWorld) {
        final var list = farmWorld.getSigns();
        final String message;

        if (!list.isEmpty()) {
            final String[] array = new String[list.size()];

            for (int i = 0; i < list.size(); i++) {
                array[i] = ((SignCacheImpl) list.get(i)).toInfoCommandString();
            }

            message = arrayToString(array, true);
        } else {
            message = "&cno players";
        }


        sendList(sender, messages.getInfoSigns(), list.size(), message);
    }

    private void sendPlayers(CommandSender sender, FarmWorld farmWorld) {
        final var list = farmWorld.getPlayers();
        final String message;

        if (!list.isEmpty()) {
            final String[] array = new String[list.size()];

            for (int i = 0; i < list.size(); i++) {
                array[i] = list.get(i).getName();
            }

            message = arrayToString(array, true);
        } else {
            message = "&cno signs";
        }

        sendList(sender, messages.getInfoPlayers(), list.size(), message);
    }

    private String arrayToString(String[] array, boolean separation) {
        final StringBuilder builder = new StringBuilder();

        for (String line : array) {
            if (separation) {
                builder.append("&7");
                if (!builder.isEmpty()) builder.append("&e, ");
            } else {
                if (!builder.isEmpty()) builder.append("\n");
            }

            builder.append(line);
        }

        return builder.toString();
    }

    private void sendList(CommandSender sender, String message, int size, String list) {
        new Message(message)
                .replace("%{size]", size)
                .replace("%{list}", list)
                .send(sender);
    }
}
