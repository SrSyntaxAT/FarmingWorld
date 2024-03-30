package at.srsyntax.farmingworld.command.admin.sub;

import at.srsyntax.farmingworld.APIImpl;
import at.srsyntax.farmingworld.api.message.Message;
import at.srsyntax.farmingworld.command.TabCompleterFilter;
import at.srsyntax.farmingworld.command.admin.FarmWorldSubCommand;
import at.srsyntax.farmingworld.config.MessageConfig;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
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
public class DelWorldSpawnSubCommand extends FarmWorldSubCommand implements TabCompleterFilter {

    public DelWorldSpawnSubCommand(String usage, MessageConfig.AdminCommandMessages messages, APIImpl api) {
        super(usage, messages, api);
    }
    @Override
    public void execute(CommandSender sender, String[] args) throws Exception {
        final var farmWorld = getFarmWorld(args, 1);
        if (!(sender instanceof Player player)) {
            new Message(messages.getIsNotPlayer()).send(sender);
            return;
        }

        farmWorld.setSpawn(null);
        new Message(messages.getDelWorldSpawn()).send(player);
    }

    @Override
    public List<String> tabCompleter(CommandSender sender, String[] args) {
        if (args.length == 2) return filterFarmWorlds(args[1]);
        return Collections.emptyList();
    }
}
