package at.srsyntax.farmingworld.api.handler.cooldown;

import at.srsyntax.farmingworld.APIImpl;
import at.srsyntax.farmingworld.FarmingWorldPlugin;
import at.srsyntax.farmingworld.api.handler.HandleException;
import at.srsyntax.farmingworld.api.message.Message;
import at.srsyntax.farmingworld.util.TimeUtil;
import net.md_5.bungee.api.ChatMessageType;
import org.jetbrains.annotations.NotNull;

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

/**
 * Triggers when a player is unable to teleport because they still have a cooldown.
 */
public class CooldownException extends HandleException {

    private final Cooldown cooldown;

    public CooldownException(@NotNull String message, @NotNull Cooldown cooldown) {
        super(message);
        this.cooldown = cooldown;
    }

    @Override
    public String getMessage() {
        final FarmingWorldPlugin plugin = ((APIImpl) FarmingWorldPlugin.getApi()).getPlugin();
        final String format = plugin.getPluginConfig().getMessages().getTime().getFormat();
        return new Message(super.getMessage(), ChatMessageType.SYSTEM)
                .replace("%{remaining}", TimeUtil.getRemainingTime(plugin, cooldown.getEnd()))
                .replace("%{date}", TimeUtil.getDate(format, cooldown.getEnd()))
                .toString();
    }

    /**
     * Get the cooldown on which the exception is related.
     * @return the cooldown on which the exception is related.
     */
    public Cooldown getCooldown() {
        return cooldown;
    }
}
