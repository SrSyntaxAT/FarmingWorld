package at.srsyntax.farmingworld.database.data;

import at.srsyntax.farmingworld.FarmingWorldPlugin;
import at.srsyntax.farmingworld.api.WorldManager;
import at.srsyntax.farmingworld.api.event.ReplacedFarmingWorldEvent;
import at.srsyntax.farmingworld.database.Database;
import at.srsyntax.farmingworld.util.location.LocationHelper;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;

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
@Getter @Setter
public class FarmingWorldData extends LocationHelper implements WorldManager {

  private long created;
  private String currentWorldName, nextWorldName;

  public FarmingWorldData(long created, String currentWorldName, String nextWorldName) {
    this.created = created;
    this.currentWorldName = currentWorldName;
    this.nextWorldName = nextWorldName;
  }

  public FarmingWorldData(ResultSet resultSet) throws SQLException {
    this(
        resultSet.getLong("created"),
        resultSet.getString("current_world"),
        resultSet.getString("next_world")
    );
  }

  @Override
  public String toString() {
    return "FarmingWorldData{" +
        "created=" + created +
        ", currentWorldName='" + currentWorldName + '\'' +
        ", nextWorldName='" + nextWorldName + '\'' +
        '}';
  }

  @Override
  public @Nullable World getWorld() {
    if (currentWorldName == null) return null;
    return Bukkit.getWorld(this.currentWorldName);
  }

  @Override
  public void newWorld(@NotNull World world) {
    final World old = getWorld();

    setCurrentWorldName(world.getName());
    setCreated(System.currentTimeMillis());

    if (old != null)
      old.getPlayers().forEach(player -> this.farmingWorld.getUnsafe().teleport(player));

    final Event event = new ReplacedFarmingWorldEvent(this.farmingWorld, world, old);
    this.farmingWorld.sync(() -> Bukkit.getPluginManager().callEvent(event));

    if (old != null)
      FarmingWorldPlugin.getApi().deleteFarmingWorld(this.farmingWorld, old);

    this.farmingWorld.async(() -> {
      try {
        final Database database = this.farmingWorld.getPlugin().getDatabase();
        database.updateWorld(this.farmingWorld);
        database.removeLocations(this.farmingWorld);
      } catch (SQLException e) {
        e.printStackTrace();
      }

      this.locationCache.clear();
      this.locationCache.putAll(generateRandomLocations());
      loadChunks();
    });
  }

  @Override
  public void setNextWorld(@Nullable World world) {
    final FarmingWorldPlugin plugin = this.farmingWorld.getPlugin();
    setNextWorldName(world == null ? null : world.getName());
    try {
      plugin.getDatabase().updateNextWorld(this.farmingWorld);
    } catch (SQLException e) {
      plugin.getLogger().severe(this.farmingWorld.getName() + " could not be saved!");
      e.printStackTrace();
    }
  }

  @Override
  public @Nullable World getNextWorld() {
    if (this.nextWorldName == null) return null;
    return Bukkit.getWorld(this.nextWorldName);
  }

  @Override
  public boolean hasNext() {
    return this.nextWorldName != null;
  }

}
