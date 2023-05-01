package at.srsyntax.farmingworld.handler.economy;

import at.srsyntax.farmingworld.FarmingWorldPlugin;
import at.srsyntax.farmingworld.api.handler.HandleException;
import at.srsyntax.farmingworld.api.handler.economy.Economy;
import at.srsyntax.farmingworld.api.handler.economy.EconomyException;
import at.srsyntax.farmingworld.config.PluginConfig;
import net.milkbowl.vault.economy.EconomyResponse;
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
public class EconomyImpl implements Economy {

    private final FarmingWorldPlugin plugin;
    private final PluginConfig config;

    private final Player player;
    private final double price;

    public EconomyImpl(FarmingWorldPlugin plugin, Player player, double price) {
        this.plugin = plugin;
        this.config = plugin.getPluginConfig();
        this.player = player;
        this.price = price;
    }

    @Override
    public boolean canBypass() {
        return player.hasPermission("farmingworld.bypass.countdown") || player.hasPermission("farmingworld.bypass.*");
    }

    @Override
    public void handle() throws HandleException {
        if (!FarmingWorldPlugin.getApi().vaultSupported() || price <= 0 || canBypass()) return;
        if (!canBuy()) {
            final EconomyResponse response = new EconomyResponse(
                    price,
                    plugin.getEconomy().getBalance(player) - price,
                    EconomyResponse.ResponseType.FAILURE,
                    "Not enough money"
            );
            throw new EconomyException(plugin.getMessageConfig().getNotEnoughMoney(), this, response);
        }
        final EconomyResponse response = plugin.getEconomy().withdrawPlayer(player, price);
        if (response.type != EconomyResponse.ResponseType.SUCCESS)
            throw new EconomyException("&c" + response.errorMessage, this, response);
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public boolean canBuy() {
        return plugin.getEconomy().getBalance(player) >= price;
    }

    @Override
    public double refund() {
        if (!FarmingWorldPlugin.getApi().vaultSupported()) return 0;
        final double refund = price * config.getRefund();
        plugin.getEconomy().depositPlayer(player, refund);
        return refund;
    }
}
