package at.srsyntax.farmingworld;

import at.srsyntax.farmingworld.api.API;
import at.srsyntax.farmingworld.api.farmworld.FarmWorld;
import at.srsyntax.farmingworld.api.handler.cooldown.Cooldown;
import at.srsyntax.farmingworld.api.handler.countdown.Countdown;
import at.srsyntax.farmingworld.api.handler.countdown.CountdownCallback;
import at.srsyntax.farmingworld.handler.cooldown.CooldownImpl;
import at.srsyntax.farmingworld.handler.countdown.CountdownImpl;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

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
public class APIImpl implements API {

    private final FarmingWorldPlugin plugin;

    @Override
    public Countdown getCountdown(@NotNull Player player, @NotNull CountdownCallback callback) {
        if (hasCountdown(player))
            return plugin.getCountdownRegistry().getCountdown(player);
        return new CountdownImpl(plugin, player, callback);
    }

    @Override
    public boolean hasCountdown(Player player) {
        return plugin.getCountdownRegistry().hasCountdown(player);
    }

    @Override
    public Cooldown getCooldown(Player player, FarmWorld farmWorld) {
        return new CooldownImpl(plugin, plugin.getPluginConfig().getMessages().getCooldown(), player, farmWorld);
    }

    @Override
    public boolean hasCooldown(Player player, FarmWorld farmWorld) {
        return getCooldown(player, farmWorld).hasCooldown();
    }

    @Override
    public boolean vaultSupported() {
        return plugin.getEconomy() != null;
    }
}
