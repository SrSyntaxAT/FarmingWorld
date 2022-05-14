package at.srsyntax.farmingworld.util.world;

import at.srsyntax.farmingworld.APIImpl;
import at.srsyntax.farmingworld.FarmingWorldPlugin;
import at.srsyntax.farmingworld.api.API;
import at.srsyntax.farmingworld.config.FarmingWorldConfig;
import at.srsyntax.farmingworld.database.Database;
import lombok.AllArgsConstructor;
import org.bukkit.Location;
import org.bukkit.World;

import java.io.File;
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
@AllArgsConstructor
public class FarmingWorldDeleter {

  private final API api;
  private final FarmingWorldPlugin plugin;
  private final FarmingWorldConfig farmingWorld;

  public void delete() {
    this.plugin.getPluginConfig().getFarmingWorlds().remove(this.farmingWorld);

    if (this.farmingWorld.isActiv()) {
      this.farmingWorld.setActiv(false);
      deleteWhenEnabled(this.api);
    } else {
      deleteWhenDisabled((APIImpl) this.api);
    }

    final Database database = this.plugin.getDatabase();
    try {
      database.deleteFarmingWorld(this.farmingWorld);
      database.removeLocations(this.farmingWorld);
    } catch (SQLException exception) {
      exception.printStackTrace();
    }

    this.farmingWorld.setData(null);
    this.farmingWorld.save();
  }

  private void deleteWhenEnabled(API api) {
    deleteWorld(api, this.farmingWorld.getWorld());
    deleteWorld(api, this.farmingWorld.getNextWorld());
  }

  private void deleteWhenDisabled(APIImpl api) {
    deleteWorldByName(api, this.farmingWorld.getData().getCurrentWorldName());
    deleteWorldByName(api, this.farmingWorld.getData().getNextWorldName());
  }

  private void deleteWorldByName(APIImpl api, String name) {
    if (name == null) return;
    api.deleteFolder(new File(name));
  }

  private void deleteWorld(API api, World world) {
    if (world == null) return;

    if (!world.getPlayers().isEmpty()) {
      try {
        final Location fallback = api.getFallbackWorld().getSpawnLocation();
        world.getPlayers().forEach(player -> player.teleport(fallback));
      } catch (Exception exception) {
        exception.printStackTrace();
      }
    }

    api.deleteFarmingWorld(this.farmingWorld, world);
  }
}
