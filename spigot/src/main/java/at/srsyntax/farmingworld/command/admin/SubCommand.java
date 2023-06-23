package at.srsyntax.farmingworld.command.admin;

import at.srsyntax.farmingworld.APIImpl;
import at.srsyntax.farmingworld.api.message.Message;
import at.srsyntax.farmingworld.command.admin.cache.CacheData;
import at.srsyntax.farmingworld.config.MessageConfig;
import lombok.AllArgsConstructor;
import lombok.Getter;
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
@AllArgsConstructor
@Getter
public abstract class SubCommand {

    private final String usage;

    protected final MessageConfig.AdminCommandMessages messages;
    protected final APIImpl api;

    public String getName() {
        return usage.split(" ")[0];
    }

    public abstract void execute(CommandSender sender, String[] args) throws Exception;
    public List<String> tabCompleter(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }

    protected void checkConfirmed(CommandSender sender, AdminCommand adminCommand, CacheData data, int index, String[] args) {
        boolean confirmed = false;
        if (args.length > index)
            confirmed = args[index].equalsIgnoreCase("confirm");

        if (confirmed)
            data.callback().call(data);
        else {
            new Message(messages.getConfirm()).send(sender);
            adminCommand.getConfirmCache().put(sender, data);
        }
    }
}
