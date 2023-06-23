package at.srsyntax.farmingworld.api.event.farmworld;

import at.srsyntax.farmingworld.api.farmworld.FarmWorld;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
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
 * Represents a farm world related event.
 */
public abstract class FarmWorldEvent extends Event {

    private final FarmWorld farmWorld;

    public FarmWorldEvent(@NotNull FarmWorld farmWorld) {
        this.farmWorld = farmWorld;
    }

    public FarmWorldEvent(boolean isAsync, @NotNull FarmWorld farmWorld) {
        super(isAsync);
        this.farmWorld = farmWorld;
    }

    public static void call(Class<? extends FarmWorldEvent> eventClass, FarmWorld farmWorld) {
        try {
            final var event = eventClass.getConstructor(FarmWorld.class).newInstance(farmWorld);
            Bukkit.getPluginManager().callEvent(event);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Get the affected farm world.
     * @return the affected farm world.
     */
    public @NotNull FarmWorld getFarmWorld() {
        return farmWorld;
    }
}
