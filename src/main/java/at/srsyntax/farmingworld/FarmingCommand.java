package at.srsyntax.farmingworld;

import at.srsyntax.farmingworld.api.API;
import at.srsyntax.farmingworld.api.FarmingWorld;
import at.srsyntax.farmingworld.api.Message;
import at.srsyntax.farmingworld.config.MessageConfig;
import lombok.AllArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
public class FarmingCommand implements CommandExecutor, TabCompleter {

  private final API api;
  private final FarmingWorldPlugin plugin;

  @Override
  public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
    if (commandSender instanceof Player player) {

      if (strings.length == 0) {
        final String defaultFarmingWorldName = plugin.getPluginConfig().getDefaultFarmingWorld();

        if (defaultFarmingWorldName == null) {
          return listAllFarmingWorlds(player);
        } else {
          return randomTeleport(player, defaultFarmingWorldName);
        }
      } else {
        randomTeleport(player, strings[0]);
      }

    } else {
      commandSender.sendMessage("Only one player can use this command!");
    }
    return false;
  }

  @Nullable
  @Override
  public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
    if (commandSender instanceof Player player) {
      if (strings.length == 0)
        return farmingWorlds(player);
    }
    return new ArrayList<>();
  }

  private boolean listAllFarmingWorlds(Player player) {
    final StringBuilder builder = new StringBuilder();

    farmingWorlds(player).forEach(farmingWorld -> {
        if (!builder.isEmpty())
          builder.append("&7, ");
        builder.append("&e").append(farmingWorld);
    });

    final String message = new Message(plugin.getPluginConfig().getMessage().getFarmingWorldList())
        .add("<list>", builder.toString())
        .replace();

    player.sendMessage(message);
    return false;
  }

  private List<String> farmingWorlds(Player player) {
    final List<String> worlds = new ArrayList<>();

    api.getFarmingWorlds().forEach(farmingWorld -> {
      if (farmingWorld.getPermission() == null || player.hasPermission(farmingWorld.getPermission()))
        worlds.add(farmingWorld.getName().toLowerCase());
    });

    return worlds;
  }

  private boolean randomTeleport(Player player, String name) {
    final MessageConfig messageConfig = plugin.getPluginConfig().getMessage();
    final FarmingWorld farmingWorld = api.getFarmingWorld(name);

    if (farmingWorld == null || farmingWorld.getWorld() == null) {
      player.sendMessage(new Message(messageConfig.getWorldNotFound()).replace());
      return false;
    }

    if (farmingWorld.getPermission() != null && !player.hasPermission(farmingWorld.getPermission())) {
      player.sendMessage(new Message(messageConfig.getNoPermission()).replace());
      return false;
    }

    api.randomTeleport(player, farmingWorld.getWorld());
    return true;
  }
}
