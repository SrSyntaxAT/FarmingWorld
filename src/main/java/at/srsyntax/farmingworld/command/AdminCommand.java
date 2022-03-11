package at.srsyntax.farmingworld.command;

import at.srsyntax.farmingworld.api.API;
import at.srsyntax.farmingworld.api.FarmingWorld;
import at.srsyntax.farmingworld.api.message.Message;
import at.srsyntax.farmingworld.config.MessageConfig;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

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
public interface AdminCommand extends CommandExecutor, TabCompleter {

  default void sendAllowedWorlds(API api, MessageConfig messageConfig, CommandSender sender) {
    final Message message;

    if (api.getFarmingWorlds().isEmpty())
      message = new Message(messageConfig.getNoWorlds());
    else
      message = new Message(messageConfig.getFarmingWorldList()).add("<list>", listWorlds(api));

    sender.sendMessage(message.replace());
  }

  default String listWorlds(API api) {
    final StringBuilder builder = new StringBuilder();
    for (FarmingWorld farmingWorld : api.getFarmingWorlds()) {
      if (!builder.isEmpty())
        builder.append("&8, ");
      builder.append("&7").append(farmingWorld.getName());
    }
    return builder.toString();
  }

}
