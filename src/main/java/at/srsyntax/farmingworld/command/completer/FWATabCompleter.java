package at.srsyntax.farmingworld.command.completer;

import at.srsyntax.farmingworld.api.API;
import at.srsyntax.farmingworld.command.FarmingWorldAdminCommand;
import lombok.AllArgsConstructor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
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
public class FWATabCompleter {

  private final API api;
  private final String permission;
  private final FarmingWorldAdminCommand command;

  public List<String> onTabComplete(CommandSender commandSender, String[] args) {
    if (!command.hasPermissionToUse(commandSender)) return null;
    return switch (args.length) {
      case 1 -> getTabCompleteFirstArgs(commandSender, args);
      case 2 -> getTabCompleteSecondArgs(commandSender, args);
      default -> null;
    };
  }

  private List<String> getTabCompleteFirstArgs(CommandSender sender, String[] args) {
    final List<String> allowedArgs = Arrays.asList("reload", "confirm", "info", "delete", "reset"),
        result = new ArrayList<>();

    for (String s : allowedArgs) {
      if (s.startsWith(args[0])) {
        if (!needContinue(sender, s))
          result.add(s);
      }
    }

    if (result.isEmpty())
      addAllToResult(result, sender, allowedArgs);

    return result;
  }

  private void addAllToResult(final List<String> result, CommandSender sender, List<String> allowedArgs) {
    allowedArgs.forEach(s -> {
      if (!needContinue(sender, s))
        result.add(s);
    });
  }

  private boolean needContinue(CommandSender sender, String s) {
    if (s.equalsIgnoreCase("confirm") && !hasPermissionToConfirm(sender)) return true;
    return !s.equalsIgnoreCase("confirm") && !(sender.hasPermission(this.permission + s) || sender.hasPermission(this.permission + "*"));
  }

  private boolean hasPermissionToConfirm(CommandSender sender) {
    return sender.hasPermission(this.permission + "*") || sender.hasPermission(this.permission + "reset")
        || sender.hasPermission(this.permission + "reload") || sender.hasPermission(this.permission + "delete");
  }

  private List<String> getTabCompleteSecondArgs(CommandSender sender, String[] args) {
    final List<String> allowedArgs = Arrays.asList("info", "delete", "reset");
    if (!allowedArgs.contains(args[0].toLowerCase()) || !hasPermissionToCompleteSecondArgs(sender)) return null;
    return DefaultTabCompleter.onTabComplete(api, args, 1);
  }

  private boolean hasPermissionToCompleteSecondArgs(CommandSender sender) {
    return sender.hasPermission(this.permission + "*") || sender.hasPermission(this.permission + "reset")
        || sender.hasPermission(this.permission + "info") || sender.hasPermission(this.permission + "delete");
  }
}
