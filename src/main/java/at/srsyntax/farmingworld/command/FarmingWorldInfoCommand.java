package at.srsyntax.farmingworld.command;

import at.srsyntax.farmingworld.api.API;
import at.srsyntax.farmingworld.api.FarmingWorld;
import at.srsyntax.farmingworld.api.message.Message;
import at.srsyntax.farmingworld.api.message.MessageBuilder;
import at.srsyntax.farmingworld.config.MessageConfig;
import lombok.AllArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/*
 * CONFIDENTIAL
 *  Unpublished Copyright (c) 2022 Sytonix, All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains the property of Sytonix. The intellectual and
 * technical concepts contained herein are proprietary to Sytonix and may be covered by U.S. and Foreign
 * Patents, patents in process, and are protected by trade secret or copyright law. Dissemination of this
 * information or reproduction of this material is strictly forbidden unless prior written permission is obtained
 * from Sytonix.  Access to the source code contained herein is hereby forbidden to anyone without written
 * permission Confidentiality and Non-disclosure agreements explicitly covering such access.
 *
 * The copyright notice above does not evidence any actual or intended publication or disclosure  of  this source code,
 * which includes information that is confidential and/or proprietary, and is a trade secret, of Sytonix.
 * ANY REPRODUCTION, MODIFICATION, DISTRIBUTION, PUBLIC  PERFORMANCE, OR PUBLIC DISPLAY OF OR THROUGH USE  OF THIS
 * SOURCE CODE  WITHOUT  THE EXPRESS WRITTEN CONSENT OF Sytonix IS STRICTLY PROHIBITED, AND IN VIOLATION OF
 * APPLICABLE LAWS AND INTERNATIONAL TREATIES.  THE RECEIPT OR POSSESSION OF  THIS SOURCE CODE AND/OR RELATED
 * INFORMATION DOES NOT CONVEY OR IMPLY ANY RIGHTS TO REPRODUCE, DISCLOSE OR DISTRIBUTE ITS CONTENTS, OR TO
 * MANUFACTURE, USE, OR SELL ANYTHING THAT IT  MAY DESCRIBE, IN WHOLE OR IN PART.
 */
@AllArgsConstructor
public class FarmingWorldInfoCommand implements CommandExecutor, TabCompleter {
  
  private final API api;
  private final MessageConfig messageConfig;
  
  //  fwi <world>
  @Override
  public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
    if (commandSender.hasPermission("farmingworld.info")) {
      if (args.length == 0)
        sendAllowedWorlds(commandSender);
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
  
  private void sendAllowedWorlds(CommandSender sender) {
    final Message message;
    
    if (this.api.getFarmingWorlds().isEmpty())
      message = new Message(this.messageConfig.getNoWorlds());
    else
      message = new Message(this.messageConfig.getFarmingWorldList()).add("<list>", listWorlds());
    
    sender.sendMessage(message.replace());
  }
  
  private String listWorlds() {
    final StringBuilder builder = new StringBuilder();
    for (FarmingWorld farmingWorld : this.api.getFarmingWorlds()) {
      if (!builder.isEmpty())
        builder.append("&8, ");
      builder.append("&7").append(farmingWorld.getName());
    }
    return builder.toString();
  }
  
  private void sendFarmingWorldInfo(CommandSender sender, String name) throws FarmingWorldNotFoundException {
    final FarmingWorld farmingWorld = getFarmingWorld(name);
    
    final MessageBuilder builder = new MessageBuilder()
      .addLine("§6" + farmingWorld.getName() + " §eInfos§8:")
      .addLine("&eCurrent World&8: &7" + farmingWorld.getWorld().getName())
      .addLine("§eCreated§8: §7" + getDate(farmingWorld.getCreated()))
      .addLine("§eReset§8: §7" + getDate(farmingWorld.getReset()))
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
  
  private String getDate(long date) {
    final DateFormat format = new SimpleDateFormat(this.messageConfig.getDateFormat());
    return format.format(new Date(date));
  }
  
  @Nullable
  @Override
  public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
    final List<String> result = new ArrayList<>(this.api.getFarmingWorlds().size());
    
    if (args.length == 1) {
      final String arg = args[0];
      
      for (FarmingWorld world : this.api.getFarmingWorlds()) {
        if (world.getName().startsWith(arg))
          result.add(world.getName());
      }
    }
    
    return result;
  }
}
