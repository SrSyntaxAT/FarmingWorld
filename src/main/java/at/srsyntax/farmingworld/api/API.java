package at.srsyntax.farmingworld.api;

import at.srsyntax.farmingworld.command.exception.TeleportFarmingWorldException;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.List;

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
public interface API {

  @NotNull List<? extends FarmingWorld> getFarmingWorlds();
  @Nullable FarmingWorld getFarmingWorld(@NotNull String name);
  @Nullable FarmingWorld getFarmingWorld(@NotNull World world);
  boolean isFarmingWorld(World world);

  @NotNull World loadFarmingWorld(@NotNull String name, @NotNull World.Environment environment);
  @NotNull World loadFarmingWorld(@NotNull String name, @NotNull World.Environment environment, @Nullable String generator);
  @NotNull World generateFarmingWorld(@NotNull FarmingWorld farmingWorld);
  void deleteFarmingWorld(@NotNull FarmingWorld farmingWorld, @NotNull World world);
  void deleteFarmingWorld(@NotNull FarmingWorld farmingWorld);
  @Nullable World getFallbackWorld() throws IOException;
  void unloadWorlds(FarmingWorld farmingWorld);

  @NotNull String getRemainingTime(long time);

  void randomTeleport(Player player, FarmingWorld farmingWorld) throws TeleportFarmingWorldException;

  String getDate(long date);
  String getDate();

  void reload();
}
