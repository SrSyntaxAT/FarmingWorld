package at.srsyntax.farmingworld.api.event.farmworld.player;

import at.srsyntax.farmingworld.api.event.farmworld.FarmWorldEvent;
import at.srsyntax.farmingworld.api.farmworld.FarmWorld;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
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
 * Represents an event in the farm world that relates to a player.
 */
public abstract class FarmWorldPlayerEvent extends FarmWorldEvent {

    private final Player player;

    public FarmWorldPlayerEvent(@NotNull FarmWorld farmWorld, Player player) {
        super(farmWorld);
        this.player = player;
    }

    public FarmWorldPlayerEvent(boolean isAsync, @NotNull FarmWorld farmWorld, Player player) {
        super(isAsync, farmWorld);
        this.player = player;
    }

    public static void call(Class<? extends FarmWorldEvent> eventClass, FarmWorld farmWorld, Player player) {
        try {
            final var event = eventClass.getConstructor(FarmWorld.class, Player.class).newInstance(farmWorld, player);
            Bukkit.getPluginManager().callEvent(event);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public Player getPlayer() {
        return player;
    }
}
