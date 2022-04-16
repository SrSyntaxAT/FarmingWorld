package at.srsyntax.farmingworld.command;

import at.srsyntax.farmingworld.FarmingWorldPlugin;
import at.srsyntax.farmingworld.api.API;
import at.srsyntax.farmingworld.api.FarmingWorld;
import at.srsyntax.farmingworld.api.message.Message;
import at.srsyntax.farmingworld.command.exception.*;
import at.srsyntax.farmingworld.config.MessageConfig;
import at.srsyntax.farmingworld.util.ResetData;
import lombok.AllArgsConstructor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
public class FarmingWorldResetCommand implements AdminCommand {

  private static final String PERMISSION = "farmingworld.reset";

  private final API api;
  private final FarmingWorldPlugin plugin;
  private final MessageConfig messageConfig;

  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
    // fwr <world>
    // fwr confirm

    try {
      if (!sender.hasPermission(PERMISSION)) throw new NoPermissionException(this.messageConfig);

      if (args.length > 0) {
        final String arg = args[0];

        if (arg.equalsIgnoreCase("confirm"))
          confirmReset(sender);
        else
          reset(sender, arg);
      } else {
        sendAllowedWorlds(this.api, messageConfig, sender);
      }

      return true;
    } catch (FarmingWorldException exception) {
      sender.sendMessage(exception.getMessage());
    }
    return false;
  }

  private void confirmReset(CommandSender sender) throws NothingToConfirmException, ConfirmExpiredException {
    if (!this.plugin.getNeedConfirm().containsKey(sender)) throw new NothingToConfirmException(this.messageConfig);

    final ResetData data = this.plugin.getNeedConfirm().remove(sender);
    if (data.isExpired()) throw new ConfirmExpiredException(this.messageConfig);
    deleteWorld(data.farmingWorld());

    final String message = new Message(this.messageConfig.getWorldDeleted())
        .add("<farmingworld>", data.farmingWorld().getName())
        .replace();
    sender.sendMessage(message);
  }

  private void deleteWorld(FarmingWorld farmingWorld) {
    if (!farmingWorld.hasNext()) {
      final World world = this.api.generateFarmingWorld(farmingWorld);
      farmingWorld.setNextWorld(world);
    }

    this.api.deleteFarmingWorld(farmingWorld);
    farmingWorld.newWorld(Objects.requireNonNull(farmingWorld.getNextWorld()));
    farmingWorld.setNextWorld(null);
  }

  private void reset(CommandSender sender, String name) throws FarmingWorldNotFoundException {
    final FarmingWorld world = this.api.getFarmingWorld(name);
    if (world == null) throw new FarmingWorldNotFoundException(this.messageConfig);

    final ResetData data = new ResetData(world, System.currentTimeMillis());
    if (this.plugin.getNeedConfirm().containsKey(sender))
      this.plugin.getNeedConfirm().replace(sender, data);
    else
      this.plugin.getNeedConfirm().put(sender, data);

    final String message = new Message(this.messageConfig.getConfirm())
        .add("<farmingworld>", world.getName())
        .replace();
    sender.sendMessage(message);
  }

  @Nullable
  @Override
  public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
    if (commandSender.hasPermission(PERMISSION)) {
      final List<String> result = DefaultTabCompleter.onTabComplete(this.api, args, 0);
      result.add("confirm");
      return result;
    } else
      return new ArrayList<>(0);
  }
}
