package at.srsyntax.farmingworld.command.admin.sub;

import at.srsyntax.farmingworld.APIImpl;
import at.srsyntax.farmingworld.api.farmworld.LocationCache;
import at.srsyntax.farmingworld.api.message.Message;
import at.srsyntax.farmingworld.command.admin.SubCommand;
import at.srsyntax.farmingworld.config.MessageConfig;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
public class SetSpawnSubCommand extends SubCommand {

    public SetSpawnSubCommand(String usage, MessageConfig.AdminCommandMessages messages, APIImpl api) {
        super(usage, messages, api);
    }

    @Override
    public void execute(CommandSender sender, String[] args) throws Exception {
        if (!(sender instanceof Player player))
            throw new Exception(messages.getIsNotPlayer());

        String message;

        try {
            final var config = api.getPlugin().getPluginConfig();
            final var location = player.getLocation();
            config.setSpawn(new LocationCache(location));
            config.save(api.getPlugin());
            message = messages.getSetspawn();
        } catch (Exception exception) {
            message = messages.getSetspawnError();
            exception.printStackTrace();
        }

        new Message(message).send(sender);
    }
}
