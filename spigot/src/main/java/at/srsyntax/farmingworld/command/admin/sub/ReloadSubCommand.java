package at.srsyntax.farmingworld.command.admin.sub;

import at.srsyntax.farmingworld.APIImpl;
import at.srsyntax.farmingworld.FarmingWorldPlugin;
import at.srsyntax.farmingworld.api.API;
import at.srsyntax.farmingworld.api.farmworld.sign.SignRegistry;
import at.srsyntax.farmingworld.api.handler.countdown.AbstractCountdown;
import at.srsyntax.farmingworld.api.handler.countdown.exception.CanceledException;
import at.srsyntax.farmingworld.api.message.Message;
import at.srsyntax.farmingworld.command.admin.AdminCommand;
import at.srsyntax.farmingworld.command.admin.SubCommand;
import at.srsyntax.farmingworld.command.admin.cache.CacheCallback;
import at.srsyntax.farmingworld.command.admin.cache.CacheData;
import at.srsyntax.farmingworld.config.MessageConfig;
import at.srsyntax.farmingworld.farmworld.FarmWorldDeleter;
import at.srsyntax.farmingworld.farmworld.FarmWorldImpl;
import at.srsyntax.farmingworld.handler.countdown.FarmWorldCountdownRegistry;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

/*
 * MIT License
 *
 * Copyright (c) 2022-2023 Marcel Haberl
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
public class ReloadSubCommand extends SubCommand {

    private final AdminCommand adminCommand;

    public ReloadSubCommand(String usage, MessageConfig.AdminCommandMessages messages, APIImpl api, AdminCommand adminCommand) {
        super(usage, messages, api);
        this.adminCommand = adminCommand;
    }

    @Override
    public void execute(CommandSender sender, String[] args) throws Exception {
        final var cache = new CacheData(
                sender, System.currentTimeMillis(),
                createCallback(sender, api.getPlugin(), messages, api)
        );
        checkConfirmed(sender, adminCommand, cache, 1, args);
    }

    private CacheCallback createCallback(CommandSender sender, FarmingWorldPlugin plugin, MessageConfig.AdminCommandMessages messages, API api) {
        return data -> {
            new Message(messages.getReload()).send(sender);

            try {
                final SignRegistry signRegistry = plugin.getSignRegistry();

                Bukkit.getScheduler().cancelTasks(plugin);
                signRegistry.unload();
                checkCountdown(plugin.getCountdownRegistry());
                api.getFarmWorlds().forEach(farmWorld -> new FarmWorldDeleter(plugin, (FarmWorldImpl) farmWorld).disable());

                plugin.loadConfig();
                plugin.loadFarmWorlds();
                signRegistry.load();
                new Message(messages.getReloadFinish()).send(sender);
            } catch (Exception exception) {
                new Message(messages.getReloadError()).send(sender);
                exception.printStackTrace();
            }
        };
    }

    private void checkCountdown(FarmWorldCountdownRegistry countdownRegistry) {
        Bukkit.getOnlinePlayers().forEach(player -> {
            if (countdownRegistry.hasCountdown(player)) {
                final var handler = (AbstractCountdown) countdownRegistry.getCountdown(player);
                final var message = CanceledException.getMessageByResult(CanceledException.Result.RELOAD, handler.getMessages());
                handler.cancel(true, message, CanceledException.Result.RELOAD);
            }
        });

    }
}
