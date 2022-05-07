package at.srsyntax.farmingworld.api;

import at.srsyntax.farmingworld.api.display.Displayable;
import at.srsyntax.farmingworld.api.exception.TeleportFarmingWorldException;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

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
public interface FarmingWorld extends Displayable, WorldManager {

    @NotNull String getName();
    @Nullable String getPermission();

    int getRtpArenaSize();

    long getCreated();
    long getReset();
    boolean needReset();
    long getRemaining();
    double getBorderSize();
    String getGenerator();

    Location randomLocation();

    void setActiv(boolean activ);
    boolean isActiv();
    void disable();
    void enable();
    void delete();

    void teleport(@NotNull Player player) throws TeleportFarmingWorldException;
    void kickAll() throws IOException;
    void kickAll(@Nullable String reason) throws IOException;

    boolean isFarming(@NotNull Player player);

    void save();

}
