package at.srsyntax.farmingworld.command;

import at.srsyntax.farmingworld.api.API;
import at.srsyntax.farmingworld.api.FarmingWorld;
import at.srsyntax.farmingworld.api.message.Message;
import at.srsyntax.farmingworld.api.message.MessageBuilder;
import at.srsyntax.farmingworld.command.exception.FarmingWorldNotFoundException;
import at.srsyntax.farmingworld.config.MessageConfig;
import lombok.AllArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

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
@AllArgsConstructor
public class FarmingWorldInfoCommand implements AdminCommand {
  
  private final API api;
  private final MessageConfig messageConfig;
  
  //  fwi <world>
  @Override
  public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
    if (commandSender.hasPermission("farmingworld.info")) {
      if (args.length == 0)
        sendAllowedWorlds(this.api, this.messageConfig, commandSender);
      else {
        try {
          sendFarmingWorldInfo(commandSender, args[0]);
        } catch (FarmingWorldNotFoundException exception) {
          commandSender.sendMessage(new Message(this.messageConfig.getWorldNotFound()).replace());
        }
      }
    } else {
      commandSender.sendMessage(new Message(this.messageConfig.getNoPermission()).replace());
    }
    return false;
  }
  
  private void sendFarmingWorldInfo(CommandSender sender, String name) throws FarmingWorldNotFoundException {
    final FarmingWorld farmingWorld = getFarmingWorld(name);
    
    final MessageBuilder builder = new MessageBuilder()
      .addLine("§6" + farmingWorld.getName() + " §eInfos§8:")
      .addLine("&eCurrent World&8: &7" + farmingWorld.getWorld().getName())
      .addLine("§eCreated§8: §7" + this.api.getDate(farmingWorld.getCreated()))
      .addLine("§eReset§8: §7" + this.api.getDate(farmingWorld.getReset()))
      .addLine("§ePlayers§8: §7" + farmingWorld.getWorld().getPlayers().size());
    
    if (farmingWorld.getPermission() != null)
      builder.addLine("§ePermission§8:§7 " + farmingWorld.getPermission());
    
    sender.sendMessage(builder.toString());
  }
  
  private FarmingWorld getFarmingWorld(String name) throws FarmingWorldNotFoundException {
    final FarmingWorld farmingWorld = this.api.getFarmingWorld(name);
    if (farmingWorld == null) throw new FarmingWorldNotFoundException();
    return farmingWorld;
  }

  
  @Nullable
  @Override
  public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
    return DefaultTabCompleter.onTabComplete(this.api, args, 0);
  }
}
