package at.srsyntax.farmingworld.command;

import at.srsyntax.farmingworld.FarmingWorldPlugin;
import at.srsyntax.farmingworld.api.API;
import at.srsyntax.farmingworld.api.FarmingWorld;
import at.srsyntax.farmingworld.api.message.Message;
import at.srsyntax.farmingworld.command.exception.FarmingWorldException;
import at.srsyntax.farmingworld.command.exception.FarmingWorldNotFoundException;
import at.srsyntax.farmingworld.command.exception.NoPermissionException;
import at.srsyntax.farmingworld.command.exception.TargetHasNoPermissionException;
import at.srsyntax.farmingworld.config.MessageConfig;
import org.bukkit.Bukkit;
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
public class TeleportFarmingWorldCommand implements CommandExecutor, TabCompleter {

  private final API api;
  private final FarmingWorldPlugin plugin;
  private final MessageConfig messageConfig;

  public TeleportFarmingWorldCommand(API api, FarmingWorldPlugin plugin) {
    this.api = api;
    this.plugin = plugin;
    this.messageConfig = plugin.getPluginConfig().getMessage();
  }

  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
    // tfw <player> [world]

    try {
      teleportOther(sender, strings);
      return true;
    } catch (FarmingWorldException exception) {
      sender.sendMessage(exception.getMessage());
      return false;
    }
  }

  @Nullable
  @Override
  public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
    return DefaultTabCompleter.onTabComplete(this.api, args, 1);
  }

  private void teleportOther(CommandSender sender, String[] args) throws FarmingWorldException {
    checkPermission(sender);
    checkArgs(args);

    final Player target = getPlayer(args[0]);
    final FarmingWorld farmingWorld = getFarmingWorld(args);

    if (sender.hasPermission("farmingworld.teleport.other.ignore.check") && !hasDisabledTargetCheck(args))
      checkTarget(target, farmingWorld);

    farmingWorld.teleport(target);

    final String message = new Message(this.messageConfig.getTargetTeleported())
        .add("<player>", target.getName())
        .add("<farmingworld>", farmingWorld.getName())
        .replace();
    sender.sendMessage(message);
  }

  private void checkPermission(CommandSender sender) throws NoPermissionException {
    if (!sender.hasPermission("farmingworld.teleport.other"))
      throw new NoPermissionException(new Message(this.messageConfig.getNoPermission()).replace());
  }

  private void checkArgs(String[] args) throws FarmingWorldException {
    if (args.length == 0) {
      final String message = new Message(this.messageConfig.getUsage())
          .add("<usage>", "tpfw <player> [<farmingworld> -dtc]")
          .replace();
      throw new FarmingWorldException(message);
    }
  }

  private Player getPlayer(String name) throws FarmingWorldException {
    final Player player = Bukkit.getPlayer(name);
    if (player == null || !player.isOnline())
      throw new FarmingWorldException(new Message(this.messageConfig.getTargetNotFound()).replace());
    return player;
  }

  private FarmingWorld getFarmingWorld(String[] args) throws FarmingWorldNotFoundException {
    final FarmingWorld farmingWorld;

    if (args.length > 1 && !args[1].startsWith("-"))
      farmingWorld = this.api.getFarmingWorld(args[1]);
    else
      farmingWorld =  this.api.getFarmingWorld(this.plugin.getPluginConfig().getDefaultFarmingWorld());

    if (farmingWorld == null)
      throw new FarmingWorldNotFoundException(new Message(this.messageConfig.getWorldNotFound()).replace());

    return farmingWorld;
  }

  private boolean hasDisabledTargetCheck(String[] args) {
    final List<String> keys = new ArrayList<>(2);
    keys.add("-disabletargetcheck");
    keys.add("-dtc");

    for (String arg : args) {
      if (keys.contains(arg.toLowerCase()))
        return true;
    }

    return false;
  }

  private void checkTarget(Player target, FarmingWorld farmingWorld) throws TargetHasNoPermissionException {
    if (farmingWorld.getPermission() != null && !target.hasPermission(farmingWorld.getPermission()))
      throw new TargetHasNoPermissionException(new Message(this.messageConfig.getTargetNoPermission()).replace());
  }
}
