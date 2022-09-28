package at.srsyntax.farmingworld.command;

import at.srsyntax.farmingworld.FarmingWorldPlugin;
import at.srsyntax.farmingworld.api.API;
import at.srsyntax.farmingworld.api.FarmingWorld;
import at.srsyntax.farmingworld.api.exception.FarmingWorldException;
import at.srsyntax.farmingworld.api.message.Message;
import at.srsyntax.farmingworld.api.message.MessageBuilder;
import at.srsyntax.farmingworld.command.completer.FWATabCompleter;
import at.srsyntax.farmingworld.command.exception.*;
import at.srsyntax.farmingworld.config.MessageConfig;
import at.srsyntax.farmingworld.config.PluginConfig;
import at.srsyntax.farmingworld.config.SpawnConfig;
import at.srsyntax.farmingworld.registry.CommandRegistry;
import at.srsyntax.farmingworld.util.ConfirmAction;
import at.srsyntax.farmingworld.util.ConfirmData;
import at.srsyntax.farmingworld.util.location.LocationCache;
import lombok.AllArgsConstructor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Arrays;
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
public class FarmingWorldAdminCommand extends Command implements AdminCommand {

  private static final String PERMISSION = "farmingworld.admin.";

  private final API api;
  private final FarmingWorldPlugin plugin;
  private final MessageConfig messageConfig;

  public FarmingWorldAdminCommand(@NotNull String name, API api, FarmingWorldPlugin plugin) {
    super(name, "Admin command.", "/" + name, Arrays.asList("fwa", "fwadmin"));
    this.api = api;
    this.plugin = plugin;
    this.messageConfig = plugin.getPluginConfig().getMessage();
  }

  @Override
  public boolean execute(@NotNull CommandSender sender, @NotNull String s, @NotNull String[] args) {
    try {
      if (!hasPermissionToUse(sender)) throw new NoPermissionException(this.messageConfig);
      if (args.length == 0) return sendUsage(sender);
      return switch (args[0].toLowerCase()) {
        case "info" -> info(sender, args);
        case "reload" -> sendConfirm(sender, ConfirmAction.RELOAD, null);
        case "delete" -> sendConfirm(sender, ConfirmAction.DELETE, args);
        case "reset" -> sendConfirm(sender, ConfirmAction.RESET, args);
        case "confirm" -> confirm(sender);
        case "enable" -> enable(sender, args);
        case "disable" -> sendConfirm(sender, ConfirmAction.DISABLE, args);
        case "list" -> list(sender);
        case "togglespawn" -> toggleSpawn(sender);
        case "setspawn" -> setSpawn(sender);
        default -> sendUsage(sender);
      };
    } catch (FarmingWorldException exception) {
      sender.sendMessage(exception.getMessage());
    } catch (Exception exception) {
      sender.sendMessage(exception.getMessage());
      exception.printStackTrace();
    }
    return false;
  }

  @NotNull
  @Override
  public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
    return new FWATabCompleter(this.api, this).onTabComplete(sender, args);
  }

  public boolean hasPermissionToUse(CommandSender sender) {
    return hasPermission(sender, "reload") || hasPermission(sender, "info")
        || hasPermission(sender, "delete") || hasPermission(sender, "reset")
        || hasPermission(sender, "list") || hasPermission(sender, "activ");
  }

  private boolean sendUsage(CommandSender sender) {
    final MessageBuilder builder = new MessageBuilder();
    builder.addLine("&6Farming World Administration Commands&8:");

    if (hasPermission(sender, "reload"))
      builder.addLine("§8/&ffwa reload &8-&7 Reload the plugin");
    if (hasPermission(sender, "info"))
      builder.addLine("§8/&ffwa info <farmingworld> &8- &7Show current information");
    if (hasPermission(sender, "delete"))
      builder.addLine("§8/&ffwa delete <farmingworld> &8- &7Reset a farmworld");
    if (hasPermission(sender, "reset"))
      builder.addLine("§8/&ffwa reset <farmingworld> &8- &7Delete a farmworld");
    if (hasPermission(sender, "list"))
      builder.addLine("§8/&ffwa list &8- &7List all farmworlds");
    if (hasPermission(sender, "activ")) {
      builder.addLine("§8/&ffwa enable <farmingworld> &8- &7Enable a farmworld");
      builder.addLine("§8/&ffwa disable <farmingworld> &8- &7Disable a farmworld");
    }

    sender.sendMessage(builder.toString());
    return true;
  }

  public boolean hasAdminPermission(CommandSender sender) {
    return sender.hasPermission(PERMISSION + "*");
  }

  public boolean hasPermission(CommandSender sender, String permission) {
    return hasAdminPermission(sender) || sender.hasPermission(PERMISSION + permission);
  }

  private void checkPermission(CommandSender sender, String permission) throws NoPermissionException {
    if (hasPermission(sender, permission)) return;
    throw new NoPermissionException(this.messageConfig);
  }

  private boolean info(CommandSender sender, String[] args) throws FarmingWorldException {
    checkPermission(sender, "info");
    sendFarmingWorldInfo(sender, getFarmingWorld(args, (byte) 1, "info <farmingworld>"));
    return true;
  }

  private void sendFarmingWorldInfo(CommandSender sender, FarmingWorld farmingWorld) {
    final MessageBuilder builder = new MessageBuilder()
        .addLine("§6" + farmingWorld.getName() + " §einformation§8:")
        .addLine("&eActiv&8: &7" + farmingWorld.isActiv());
    if (farmingWorld.getPermission() != null)
      builder.addLine("§ePermission§8:§7 " + farmingWorld.getPermission());
    if (farmingWorld.getWorld() != null)
      builder.addLine("&eCurrent world&8: &7" + farmingWorld.getWorld().getName());
    if (farmingWorld.getNextWorld() != null)
      builder.addLine("&eNext world&8:&7 " + farmingWorld.getNextWorld().getName());

    if (farmingWorld.isActiv()) {
      builder.addLine("§eCreated§8: §7" + this.api.getDate(farmingWorld.getCreated()))
          .addLine("§eReset§8: §7" + this.api.getDate(farmingWorld.getReset()));

      if (farmingWorld.getWorld() != null)
        builder.addLine("§ePlayers§8: §7" + farmingWorld.getWorld().getPlayers().size());
    }

    sender.sendMessage(builder.toString());
  }

  private boolean list(CommandSender sender) throws NoPermissionException {
    checkPermission(sender, "list");

    final StringBuilder list = new StringBuilder();
    this.api.getFarmingWorlds().forEach(farmingWorld -> {
      if (!list.isEmpty())
        list.append("&f,&7 ");
      list.append(farmingWorld.getName());
    });

    final String message;
    if (list.isEmpty())
      message = new Message(this.messageConfig.getNoWorlds()).replace();
    else
      message = new Message(this.messageConfig.getFarmingWorldList())
          .add("<list>", list.toString())
          .replace();

    sender.sendMessage(message);
    return true;
  }

  private boolean sendConfirm(CommandSender sender, ConfirmAction action, String[] args) throws FarmingWorldException {
    checkPermission(sender, action.name().toLowerCase());
    final ConfirmData data = new ConfirmData(action);

    if (action.isNeedFarmingWorld()) {
      final FarmingWorld farmingWorld = getFarmingWorld(args, (byte) 1, action.name().toLowerCase() + " <farmingworld>");
      data.addData("farmingworld", farmingWorld);

      if (action == ConfirmAction.DISABLE && !farmingWorld.isActiv())
        throw new FarmingWorldException(new Message(this.messageConfig.getAlreadyDisabled()).replace());
    }

    this.plugin.getNeedConfirm().put(sender, data);
    sender.sendMessage(new Message(this.messageConfig.getConfirm()).replace());
    return true;
  }

  private FarmingWorld getFarmingWorld(String[] args, byte pos, String usage) throws FarmingWorldException {
    if (args.length <= pos) throw new InvalidArgsException(this.messageConfig, "fwa " + usage);
    final FarmingWorld farmingWorld = this.api.getFarmingWorld(args[pos]);
    if (farmingWorld == null) throw new FarmingWorldNotFoundException(this.messageConfig);
    return farmingWorld;
  }

  private boolean confirm(CommandSender sender) throws FarmingWorldException, IOException {
    final ConfirmData data = this.plugin.getNeedConfirm().remove(sender);
    if (data == null) throw new NothingToConfirmException(this.messageConfig);
    if (data.isExpired()) throw new ConfirmExpiredException(this.messageConfig);
    return switch (data.getAction()) {
      case RELOAD -> reloadConfirmed(sender);
      case DELETE -> deleteConfirmed(sender, data.getData("farmingworld"));
      case RESET -> resetConfirmed(sender, data.getData("farmingworld"));
      case DISABLE -> disableConfirmed(sender, data.getData("farmingworld"));
    };
  }

  private boolean reloadConfirmed(CommandSender sender) {
    this.api.reload();
    sender.sendMessage(new Message(this.messageConfig.getReload()).replace());
    return true;
  }

  private boolean deleteConfirmed(CommandSender sender, FarmingWorld farmingWorld) throws FarmingWorldException {
    if (farmingWorld == null) throw new FarmingWorldNotFoundException(this.messageConfig);
    farmingWorld.delete();
    sender.sendMessage(new Message(this.messageConfig.getDelete()).replace());
    return true;
  }


  private boolean resetConfirmed(CommandSender sender, FarmingWorld farmingWorld) {
    farmingWorld.setActiv(false);
    if (!farmingWorld.hasNext()) {
      final World world = this.api.generateFarmingWorld(farmingWorld);
      farmingWorld.setNextWorld(world);
    }

    this.api.deleteFarmingWorld(farmingWorld);
    farmingWorld.newWorld(Objects.requireNonNull(farmingWorld.getNextWorld()));
    farmingWorld.setNextWorld(null);

    try {
      this.plugin.getPluginConfig().save(this.plugin);
    } catch (IOException e) {
      final String message = "Config could not be saved!";
      this.plugin.getLogger().severe(message);
      sender.sendMessage("§4" + message);
      e.printStackTrace();
    }
    sender.sendMessage(new Message(this.messageConfig.getReset()).replace());
    farmingWorld.setActiv(true);
    return true;
  }

  private boolean enable(CommandSender sender, String[] args) throws FarmingWorldException {
    checkPermission(sender, "activ");
    final FarmingWorld world = getFarmingWorld(args, (byte) 1, "enable <farmingworld>");
    if (world.isActiv())
      throw new FarmingWorldException(new Message(this.messageConfig.getAlreadyEnabled()).replace());
    sender.sendMessage(new Message(this.messageConfig.getEnabled()).replace());
    world.enable();
    return true;
  }

  private boolean disableConfirmed(CommandSender sender, FarmingWorld farmingWorld) throws FarmingWorldException, IOException {
    if (!farmingWorld.isActiv())
      throw new FarmingWorldException(new Message(this.messageConfig.getAlreadyDisabled()).replace());

    final String message = new Message(this.messageConfig.getDisabled()).replace();
    final boolean needMessage = !(sender instanceof Player) || !farmingWorld.isFarming((Player) sender);

    farmingWorld.kickAll(message);
    farmingWorld.disable();

    if (needMessage)
      sender.sendMessage(message);
    return true;
  }

  private boolean toggleSpawn(CommandSender sender) throws FarmingWorldException {
    checkPermission(sender, "spawn.toggle");
    try {
      final SpawnConfig config = plugin.getPluginConfig().getSpawn();
      final Message message;
      final CommandRegistry registry = plugin.getCommandRegistry();

      if (config.isEnabled()) {
        message = new Message(messageConfig.getSpawnDisabled());
        registry.unregister(Objects.requireNonNull(plugin.getCommand("spawn")));
      } else {
        message = new Message(messageConfig.getSpawnEnabled());
        registry.register(new SpawnCommand("spawn", plugin));
      }

      config.setEnabled(!config.isEnabled());
      plugin.getPluginConfig().save(plugin);
      sender.sendMessage(message.replace());
      return true;
    } catch (Exception exception) {
      exception.printStackTrace();
      sender.sendMessage(exception.getMessage());
      return false;
    }
  }

  public boolean setSpawn(CommandSender sender) throws FarmingWorldException {
    checkPermission(sender, "spawn.set");
    if (!(sender instanceof Player player))
      throw new FarmingWorldException(new Message(messageConfig.getOnlyPlayers()).replace());

    try {
      plugin.getPluginConfig().getSpawn().setLocation(new LocationCache(player.getLocation()));
      plugin.getPluginConfig().save(plugin);
      player.sendMessage(new Message(messageConfig.getSpawnSet()).replace());
      return true;
    } catch (Exception exception) {
      exception.printStackTrace();
      sender.sendMessage(exception.getMessage());
      return false;
    }
  }
}
