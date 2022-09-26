package at.srsyntax.farmingworld.command;

import at.srsyntax.farmingworld.FarmingWorldPlugin;
import at.srsyntax.farmingworld.api.API;
import at.srsyntax.farmingworld.api.CooldownHandler;
import at.srsyntax.farmingworld.api.FarmingWorld;
import at.srsyntax.farmingworld.api.message.Message;
import at.srsyntax.farmingworld.command.completer.DefaultTabCompleter;
import at.srsyntax.farmingworld.api.exception.FarmingWorldException;
import at.srsyntax.farmingworld.command.exception.CooldownException;
import at.srsyntax.farmingworld.command.exception.FarmingWorldNotFoundException;
import at.srsyntax.farmingworld.command.exception.NoPermissionException;
import at.srsyntax.farmingworld.command.exception.TargetHasNoPermissionException;
import at.srsyntax.farmingworld.config.MessageConfig;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
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
public class TeleportFarmingWorldCommand extends Command {

  private static final String PERMISSION, PERMISSION_IGNORE;

  static {
    PERMISSION = "farmingworld.teleport.other";
    PERMISSION_IGNORE = "farmingworld.teleport.other.ignore.check";
  }

  private final API api;
  private final FarmingWorldPlugin plugin;
  private final MessageConfig messageConfig;

  public TeleportFarmingWorldCommand(@NotNull String name, API api, FarmingWorldPlugin plugin) {
    super(name, "Teleport another player to a farming world.", "/" + name, Collections.singletonList("tpfw"));
    this.plugin = plugin;
    this.api = api;
    this.messageConfig = plugin.getPluginConfig().getMessage();
  }

  @Override
  public boolean execute(@NotNull CommandSender sender, @NotNull String s, @NotNull String[] strings) {
    // tfw <player> [world]

    try {
      teleportOther(sender, strings);
      return true;
    } catch (FarmingWorldException exception) {
      sender.sendMessage(exception.getMessage());
      return false;
    }
  }

  @NotNull
  @Override
  public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
    if (!sender.hasPermission(PERMISSION)) return new ArrayList<>(0);
    if (args.length == 1) {
      final List<String> result = new ArrayList<>();
      final String arg = args[0];

      for (Player player : Bukkit.getOnlinePlayers()) {
        if (player.getName().toLowerCase().startsWith(arg.toLowerCase()))
          result.add(player.getName());
      }

      return result;
    }
    return DefaultTabCompleter.onTabComplete(this.api, args, 1);
  }

  private void teleportOther(CommandSender sender, String[] args) throws FarmingWorldException {
    checkPermission(sender);
    checkArgs(args);

    final Player target = getPlayer(args[0]);
    final FarmingWorld farmingWorld = getFarmingWorld(args);

    if (sender.hasPermission(PERMISSION_IGNORE) && !hasDisabledTargetCheck(args))
      checkTarget(target, farmingWorld);


    final CooldownHandler cooldownHandler = api.newCooldownHandler(target, farmingWorld);
    checkCooldown(sender, cooldownHandler);
    cooldownHandler.addCooldown();

    farmingWorld.teleport(target);

    final String message = new Message(this.messageConfig.getTargetTeleported())
        .add("<player>", target.getName())
        .add("<farmingworld>", farmingWorld.getName())
        .replace();
    sender.sendMessage(message);
  }

  private void checkCooldown(CommandSender sender, CooldownHandler cooldownHandler) throws CooldownException {
    if (!cooldownHandler.hasCooldown()) return;
    if (hasCooldownPermission(sender, cooldownHandler.getFarmingWorld().getName())) return;

    final long end = cooldownHandler.getCooldownData().getEnd();
    final Message error = new Message(plugin.getPluginConfig().getMessage().getCooldownOtherError())
        .add("<date>", api.getDate(end))
        .add("<remaining>", api.getRemainingTime(end));
    throw new CooldownException(error.replace());
  }

  private boolean hasCooldownPermission(CommandSender sender, String farmingWorldName) {
    final String permissionPrefix = "farmingworld.cooldown.bypass.other.";
    return sender.hasPermission(permissionPrefix + "*") || sender.hasPermission(permissionPrefix + farmingWorldName);
  }

  private void checkPermission(CommandSender sender) throws NoPermissionException {
    if (!sender.hasPermission(PERMISSION))
      throw new NoPermissionException(this.messageConfig);
  }

  private void checkArgs(String[] args) throws FarmingWorldException {
    if (args.length != 0) return;
    final String message = new Message(this.messageConfig.getUsage())
        .add("<usage>", "tpfw <player> [<farmingworld> -dtc]")
        .replace();
    throw new FarmingWorldException(message);
  }

  private Player getPlayer(String name) throws FarmingWorldException {
    final Player player = Bukkit.getPlayer(name);
    if (player != null && player.isOnline()) return player;
    throw new FarmingWorldException(this.messageConfig);
  }

  private FarmingWorld getFarmingWorld(String[] args) throws FarmingWorldNotFoundException {
    final FarmingWorld farmingWorld;

    if (args.length > 1 && !args[1].startsWith("-"))
      farmingWorld = this.api.getFarmingWorld(args[1]);
    else
      farmingWorld =  this.api.getFarmingWorld(this.plugin.getPluginConfig().getDefaultFarmingWorld());

    if (farmingWorld != null) return farmingWorld;
    throw new FarmingWorldNotFoundException(this.messageConfig);
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
      throw new TargetHasNoPermissionException(this.messageConfig);
  }
}
