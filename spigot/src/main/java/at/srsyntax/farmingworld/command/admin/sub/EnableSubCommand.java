package at.srsyntax.farmingworld.command.admin.sub;

import at.srsyntax.farmingworld.APIImpl;
import at.srsyntax.farmingworld.api.farmworld.FarmWorld;
import at.srsyntax.farmingworld.api.message.Message;
import at.srsyntax.farmingworld.command.TabCompleterFilter;
import at.srsyntax.farmingworld.command.admin.AdminCommand;
import at.srsyntax.farmingworld.command.admin.FarmWorldSubCommand;
import at.srsyntax.farmingworld.command.admin.cache.CacheData;
import at.srsyntax.farmingworld.config.MessageConfig;
import at.srsyntax.farmingworld.farmworld.FarmWorldImpl;
import at.srsyntax.farmingworld.farmworld.FarmWorldLoader;
import org.bukkit.command.CommandSender;

import java.util.Collections;
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
public class EnableSubCommand extends FarmWorldSubCommand implements TabCompleterFilter {

    private final AdminCommand adminCommand;

    public EnableSubCommand(String usage, MessageConfig.AdminCommandMessages messages, APIImpl api, AdminCommand adminCommand) {
        super(usage, messages, api);
        this.adminCommand = adminCommand;
    }

    @Override
    public void execute(CommandSender sender, String[] args) throws Exception {
        final var farmWorld = getFarmWorld(args, 1);
        final var data = createData(sender, farmWorld);
        checkConfirmed(sender, adminCommand, data, 2, args);
    }

    @Override
    public List<String> tabCompleter(CommandSender sender, String[] args) {
        if (args.length != 2) return Collections.emptyList();
        return filterFarmWorlds(args[1]);
    }

    private CacheData createData(CommandSender sender, FarmWorld farmWorld) {
        return new CacheData(sender, System.currentTimeMillis(), data -> {
            new FarmWorldLoader(api.getPlugin(), (FarmWorldImpl) farmWorld).enable();
            farmWorld.setActive(true);
            new Message(messages.getEnable()).send(sender);
        });
    }
}
