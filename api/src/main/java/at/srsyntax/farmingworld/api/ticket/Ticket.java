package at.srsyntax.farmingworld.api.ticket;

import at.srsyntax.farmingworld.api.farmworld.FarmWorld;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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
 * Represents a ticket that allows players to teleport in a farm world.
 */
public interface Ticket {

    /**
     * @return the farm world to which the user of the ticket is teleported.
     */
    FarmWorld getFarmWorld();

    /**
     * Creates an item that represents a ticket, based on the information in the configuration.
     * @return the ticket item
     */
    ItemStack createItem();

    /**
     * Gives the player an item representing a ticket that can be used to teleport to the farm world.
     * @param player who should receive the item.
     * @return the item that the player has received.
     * @see Ticket#createItem()
     * @see Ticket#getFarmWorld()
     */
    ItemStack giveItem(Player player);

    /**
     * Removes the item representing the ticket from the player's inventory.
     * @param player from which the item is to be removed.
     */
    void removeItem(Player player);

    /**
     * Teleport a player to the farm world.
     * @param player
     */
    void teleport(Player player);

    /**
     * Teleport a player to the farm world immediately or with countdown.
     * @param player
     * @param instantly
     */
    void teleport(Player player, boolean instantly);
}
