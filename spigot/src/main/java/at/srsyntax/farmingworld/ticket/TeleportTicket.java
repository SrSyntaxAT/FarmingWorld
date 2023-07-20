package at.srsyntax.farmingworld.ticket;

import at.srsyntax.farmingworld.FarmingWorldPlugin;
import at.srsyntax.farmingworld.api.farmworld.FarmWorld;
import at.srsyntax.farmingworld.api.handler.countdown.Countdown;
import at.srsyntax.farmingworld.api.handler.countdown.CountdownCallback;
import at.srsyntax.farmingworld.api.message.Message;
import at.srsyntax.farmingworld.api.ticket.Ticket;
import at.srsyntax.farmingworld.api.util.TimeUtil;
import at.srsyntax.farmingworld.config.PluginConfig;
import at.srsyntax.farmingworld.farmworld.FarmWorldImpl;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;

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
@AllArgsConstructor
public class TeleportTicket implements Ticket {

    private final FarmWorldImpl farmWorld;
    private final PluginConfig.TicketConfig config;

    public TeleportTicket(ItemStack itemStack, PluginConfig.TicketConfig config) {
        this.config = config;
        if (!isTicketMaterial(itemStack)) throw new IllegalArgumentException();
        this.farmWorld = (FarmWorldImpl) findFarmWorldByItem(itemStack);
    }

    private FarmWorld findFarmWorldByItem(ItemStack itemStack) {
        if (itemStack == null) throw new IllegalArgumentException();
        final var lores = itemStack.getItemMeta().getLore();
        if (lores == null || lores.isEmpty()) throw new IllegalArgumentException();

        for (String line : lores) {
            for (FarmWorld farmWorld : FarmingWorldPlugin.getApi().getFarmWorlds()) {
                if (line.contains(farmWorld.getName())) return farmWorld;
            }
        }
        throw new IllegalArgumentException();
    }

    private boolean isTicketMaterial(ItemStack itemStack) {
        return itemStack != null && itemStack.getType() == config.getMaterial();
    }

    @Override
    public FarmWorld getFarmWorld() {
        return farmWorld;
    }

    @Override
    public ItemStack createItem() {
        final var name = new Message(config.getName())
                .replace("%{farmworld}", farmWorld.getName())
                .replace("%{price}", farmWorld.getPrice())
                .toString();

        final var lore = new Message("&6%1 &8-&6 %2")
                .replace("%1", TimeUtil.getDate(config.getDateFormat(), System.currentTimeMillis()))
                .replace("%2", farmWorld.getName())
                .toString();

        final var stack = new ItemStack(config.getMaterial());
        final var meta = stack.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(Collections.singletonList(lore));
        stack.setItemMeta(meta);
        return stack;
    }

    @Override
    public ItemStack giveItem(Player player) {
        final var item = createItem();
        player.getInventory().addItem(item);
        return item;
    }

    @Override
    public void removeItem(Player player) {
        final var inventory = player.getInventory();
        final var iterator = inventory.iterator();

        while (iterator.hasNext()) {
            try {
                final var item = iterator.next();
                if (!isTicketMaterial(item)) continue;
                findFarmWorldByItem(item);
                player.getInventory().remove(item);
                break;
            } catch (Exception ignore) {}
        }
    }

    @Override
    public void teleport(Player player) {
        System.out.println(config.isTeleportInstantly());
        teleport(player, config.isTeleportInstantly());
    }

    @Override
    public void teleport(Player player, boolean instantly) {
        try {
            final var callback = createCallback(player);

            final var countdown = FarmingWorldPlugin.getApi().getCountdown(player, callback);
            if (instantly) {
                callback.finished(countdown);
            } else {
                countdown.handle();
            }
        } catch (Exception exception) {
            new Message(exception.getMessage()).send(player);
        }
    }

    private CountdownCallback createCallback(Player player) {
        return new CountdownCallback(){
            @Override
            public void finished(Countdown countdown) {
                farmWorld.teleport(player);
            }

            @Override
            public void error(Countdown countdown, Throwable throwable) {
                new Message(throwable.getMessage()).send(player);
            }
        };
    }
}
