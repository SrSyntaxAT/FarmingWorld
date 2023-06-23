package at.srsyntax.farmingworld.command.admin;

import at.srsyntax.farmingworld.APIImpl;
import at.srsyntax.farmingworld.api.message.Message;
import at.srsyntax.farmingworld.command.admin.cache.CacheData;
import at.srsyntax.farmingworld.command.admin.sub.*;
import at.srsyntax.farmingworld.config.MessageConfig;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.Getter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

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
public class AdminCommand implements CommandExecutor, TabCompleter {

    private final MessageConfig.AdminCommandMessages messages;
    private final Map<String, SubCommand> commandMap;
    @Getter private final Cache<CommandSender, CacheData> confirmCache;

    public AdminCommand(APIImpl api, MessageConfig.AdminCommandMessages messages) {
        this.messages = messages;
        this.commandMap = createCommandMap(
                new ListSubCommand("list", messages, api),
                new SetSpawnSubCommand("setspawn", messages, api),
                new InfoSubCommand("info <farmworld> [signs/players]", messages, api),
                new ResetSubCommand("reset <farmworld>", messages, api, this),
                new DeleteSubCommand("delete <farmworld>", messages, api, this),
                new EnableSubCommand("enable <farmworld>", messages, api, this),
                new DisableSubCommand("disable <farmworld>", messages, api, this),
                new ReloadSubCommand("reload", messages, api, this),
                new ConfirmSubCommand("confirm", messages, api, this)

        );
        this.confirmCache = CacheBuilder.newBuilder()
                .expireAfterWrite(10L, TimeUnit.SECONDS)
                .build();
    }

    private Map<String, SubCommand> createCommandMap(SubCommand... subCommands) {
        final var map = new ConcurrentHashMap<String, SubCommand>();
        for (SubCommand command : subCommands) {
            map.put(command.getName(), command);
        }
        return map;
    }

    /*
    -   list
    info world
    -   enable/disable world
    -   delete world
    -   setspawn
    -   reset <world>
    -   reload
    -   confirm
     */
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        try {
            if (!commandSender.hasPermission("farmingworld.admin"))
                throw new Exception(messages.getNoPermission());

            if (strings.length == 0) {
                throw new Exception(getUsage());
            }

            final String arg = strings[0].toLowerCase();
            final SubCommand subCommand = commandMap.get(arg);
            if (subCommand == null)
                throw new Exception(getUsage());

            subCommand.execute(commandSender, strings);
            return true;
        } catch (Exception exception) {
            new Message(exception.getMessage()).send(commandSender);
        }
        return false;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        final List<String> result = new ArrayList<>();

        if (strings.length == 1) {
            for (String key : commandMap.keySet()) {
                if (key.startsWith(strings[0].toLowerCase()))
                    result.add(key);
            }
        } else if (strings.length > 1) {
            final SubCommand subCommand = commandMap.get(strings[0].toLowerCase());
            if (subCommand != null)
                return subCommand.tabCompleter(commandSender, strings);
        }

        return result;
    }

    private String getUsage() {
        final String newLine = "\n";
        final StringBuilder builder = new StringBuilder("&4&lFarmWorld Admin Command");
        commandMap.values().forEach(subCommand -> builder.append(newLine).append("&7/fwa ").append(subCommand.getUsage()));
        return builder.toString();
    }
}
