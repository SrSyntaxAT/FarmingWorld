package at.srsyntax.farmingworld.command;

import at.srsyntax.farmingworld.FarmingWorldPlugin;
import at.srsyntax.farmingworld.api.API;
import at.srsyntax.farmingworld.api.FarmingWorld;
import at.srsyntax.farmingworld.api.CooldownHandler;
import at.srsyntax.farmingworld.api.message.Message;
import at.srsyntax.farmingworld.api.exception.FarmingWorldException;
import at.srsyntax.farmingworld.command.exception.CooldownException;
import at.srsyntax.farmingworld.command.exception.FarmingWorldNotFoundException;
import at.srsyntax.farmingworld.command.exception.NoPermissionException;
import at.srsyntax.farmingworld.config.MessageConfig;
import at.srsyntax.farmingworld.countdown.Countdown;
import at.srsyntax.farmingworld.countdown.CountdownCallback;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
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
public class FarmingCommand implements CommandExecutor, TabCompleter {

  private final API api;
  private final FarmingWorldPlugin plugin;

  public FarmingCommand(API api, FarmingWorldPlugin plugin) {
    this.api = api;
    this.plugin = plugin;
  }

  @Override
  public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
    if (!(commandSender instanceof final Player player))
      return new TeleportFarmingWorldCommand(this.api, this.plugin).onCommand(commandSender, command, s, strings);

    try {
      if (strings.length == 0)
        return noArgRandomTeleport(player);
      else
        return randomTeleport(player, strings[0]);
    } catch (FarmingWorldException exception) {
      return handleException(commandSender, exception);
    }
  }

  private boolean handleException(CommandSender sender, Exception exception) {
    sender.sendMessage(exception.getMessage());
    return false;
  }

  @Nullable
  @Override
  public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
    final List<String> result = new ArrayList<>();

    if (args.length == 1) {
      final String arg = args[0];

      for (FarmingWorld world : this.api.getFarmingWorlds()) {
        boolean hasPermission = world.getPermission() == null || sender.hasPermission(world.getPermission());
        if (world.getName().startsWith(arg) && hasPermission)
          result.add(world.getName());
      }
    }

    return result;
  }

  private boolean noArgRandomTeleport(Player player) throws FarmingWorldException {
    final String defaultFarmingWorldName = plugin.getPluginConfig().getDefaultFarmingWorld();

    if (defaultFarmingWorldName == null) {
      return listAllFarmingWorlds(player);
    } else {
      return randomTeleport(player, defaultFarmingWorldName);
    }
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

  private boolean randomTeleport(Player player, String name) throws FarmingWorldException {
    final MessageConfig messageConfig = plugin.getPluginConfig().getMessage();
    final FarmingWorld farmingWorld = api.getFarmingWorld(name);

    if (farmingWorld == null || farmingWorld.getWorld() == null)
      throw new FarmingWorldNotFoundException(messageConfig);

    if (farmingWorld.getPermission() != null && !player.hasPermission(farmingWorld.getPermission()))
      throw new NoPermissionException(messageConfig);


    final CountdownCallback callback = createCallback(player, farmingWorld);
    final Countdown countdown = new Countdown(plugin, callback, player);

    if (countdown.isActiv()) {
      final Message message = new Message(messageConfig.getCountdownActivError());
      throw new FarmingWorldException(message.replace());
    }

    final CooldownHandler cooldownHandler = api.newCooldownHandler(player, farmingWorld);
    if (cooldownHandler.hasCooldown()) {
      final long end = cooldownHandler.getCooldownData().getEnd();
      final Message message = new Message(plugin.getPluginConfig().getMessage().getCooldownError())
          .add("<date>", api.getDate(end))
          .add("<remaining>", api.getRemainingTime(end));
      throw new CooldownException(message.replace());
    }
    cooldownHandler.addCooldown();

    if (hasCountdown(player, farmingWorld)) {
      countdown.start(farmingWorld.getCountdowm());
    } else {
      callback.done();
    }

    return true;
  }

  private boolean hasCountdown(Player player, FarmingWorld farmingWorld) {
    return farmingWorld.getCountdowm() > 0 && !hasCountdownBypassPermission(player, farmingWorld);
  }

  private boolean hasCountdownBypassPermission(Player player, FarmingWorld farmingWorld) {
    final String prefix = "farmingworld.countdown.bypass.";
    return player.hasPermission(prefix + "*") || player.hasPermission(prefix + farmingWorld.getName());
  }

  private CountdownCallback createCallback(Player player, FarmingWorld farmingWorld) {
    return () -> {
      try {
        api.randomTeleport(player, farmingWorld);
      } catch (Exception exception) {
        handleException(player, exception);
      }
    };
  }

}
