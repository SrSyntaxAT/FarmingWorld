package at.srsyntax.farmingworld.command.admin.sub;

import at.srsyntax.farmingworld.APIImpl;
import at.srsyntax.farmingworld.api.farmworld.FarmWorld;
import at.srsyntax.farmingworld.api.message.Message;
import at.srsyntax.farmingworld.command.admin.SubCommand;
import at.srsyntax.farmingworld.config.MessageConfig;
import org.bukkit.command.CommandSender;

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
public class ListSubCommand extends SubCommand {

    public ListSubCommand(String usage, MessageConfig.AdminCommandMessages messages, APIImpl api) {
        super(usage, messages, api);
    }

    @Override
    public void execute(CommandSender sender, String[] args) throws Exception {
        new Message(api.getFarmWorlds().isEmpty() ? messages.getNoFarmWorlds() : listWorlds(api)).send(sender);
    }

    private String listWorlds(APIImpl api) {
        final StringBuilder builder = new StringBuilder();

        for (FarmWorld farmWorld : api.getFarmWorlds()) {
            if (builder.isEmpty()) {
                builder.append("&6FarmWorlds &7(").append(api.getFarmWorlds().size()).append(")&f:");
            } else {
                builder.append("&e,");
            }
            builder.append("&7 ").append(farmWorld.getName());
        }

        return builder.toString();
    }
}
