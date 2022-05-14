package at.srsyntax.farmingworld.util.location;

import at.srsyntax.farmingworld.config.FarmingWorldConfig;
import at.srsyntax.farmingworld.database.Database;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

import java.sql.SQLException;
import java.util.*;

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
public abstract class LocationHelper {

  protected FarmingWorldConfig farmingWorld;
  protected final LinkedHashMap<String, LocationCache> locationCache = new LinkedHashMap<>();

  public Map<String, LocationCache> generateRandomLocations() {
    final Map<String, LocationCache> locations = new HashMap<>(3);

    for (byte i = 0; i < 3; i++)
      locations.putAll(generateLocation());

    return locations;
  }

  public Location getRandomLocation() {
    final Map.Entry<String, LocationCache> random = this.locationCache.entrySet().stream().findFirst().get();
    this.locationCache.remove(random.getKey());

    this.farmingWorld.async(() -> {
      try {
        final Database database = this.farmingWorld.getPlugin().getDatabase();
        database.removeLocation(random.getKey());
        newLocation(database);
      } catch (SQLException e) {
        throw new RuntimeException(e);
      }
    });

    return random.getValue().toBukkit();
  }

  private void newLocation(Database database) throws SQLException {
    final Map.Entry<String, LocationCache> locationCache = generateLocation().entrySet().stream().findFirst().get();
    database.addLocation(this.farmingWorld, locationCache.getKey(), locationCache.getValue());
    this.locationCache.put(locationCache.getKey(), locationCache.getValue());
    loadChunk(locationCache.getValue().toBukkit());
  }

  public LinkedHashMap<String, LocationCache> generateLocation() {
    final String id = UUID.randomUUID().toString().split("-")[0];
    final List<Material> blacklist = this.farmingWorld.getPlugin().getPluginConfig().getSpawnBlockBlacklist();
    final Location location = new LocationRandomizer(blacklist, this.farmingWorld).random();
    return new LinkedHashMap<>(Collections.singletonMap(id, new LocationCache(location)));
  }

  public void loadChunks() {
    this.locationCache.forEach((s, cache) -> loadChunk(cache.toBukkit()));
  }
  private void loadChunk(Location location) {
    Bukkit.getScheduler().runTask(this.farmingWorld.getPlugin(), () -> location.getChunk().load(true));
  }

  public void saveAllLocations() {
    this.farmingWorld.async(() -> {
      this.locationCache.forEach((id, cache) -> {
        try {
          this.farmingWorld.getPlugin().getDatabase().addLocation(this.farmingWorld, id, cache);
        } catch (SQLException e) {
          throw new RuntimeException(e);
        }
      });
    });
  }
}
