package at.srsyntax.farmingworld.util;

import at.srsyntax.farmingworld.FarmingWorldPlugin;
import at.srsyntax.farmingworld.api.API;
import at.srsyntax.farmingworld.api.FarmingWorld;
import at.srsyntax.farmingworld.config.FarmingWorldConfig;
import at.srsyntax.farmingworld.database.Database;
import at.srsyntax.farmingworld.database.FarmingWorldData;
import lombok.AllArgsConstructor;

import java.sql.SQLException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

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
public class FarmingWorldLoader {

  private final Logger logger;
  private final API api;
  private final FarmingWorldPlugin plugin;

  public void load(FarmingWorldConfig farmingWorld) {
    farmingWorld.setPlugin(plugin);
    setDatabaseData(farmingWorld);
    if (farmingWorld.isActiv())
      enable(farmingWorld);
  }

  public void enable(FarmingWorldConfig farmingWorld) {
    checkBorder(farmingWorld);
    checkCurrentWorld(farmingWorld);
    checkNextWorld(farmingWorld);
  }

  private boolean checkDatabase(FarmingWorldConfig farmingWorld) throws SQLException {
    final Database database = plugin.getDatabase();
    if (database.exists(farmingWorld)) return true;
    if (farmingWorld.getData() == null)
      farmingWorld.setData(new FarmingWorldData(0L, null, null));
    database.createFarmingWorld(farmingWorld);
    return false;
  }

  private void setDatabaseData(FarmingWorldConfig farmingWorld) {
    try {
      if (!checkDatabase(farmingWorld)) return;
      farmingWorld.setData(plugin.getDatabase().getData(farmingWorld.getName()));
    } catch (SQLException e) {
      logger.severe("Data could not be read from the database!");
      e.printStackTrace();
    }
  }

  private void checkBorder(FarmingWorld farmingWorld) {
    if (farmingWorld.getBorderSize() < 10) return;
    if (farmingWorld.getBorderSize() >= farmingWorld.getRtpArenaSize()) return;
    final String prefix = farmingWorld.getName() + ": ";
    final int newRTPArenaSize = (int) (farmingWorld.getBorderSize() - 2);
    logger.severe(prefix + "The RTP arena size must be smaller than the world border.");
    logger.severe(prefix + "The RTP arena size is therefore changed from " + farmingWorld.getRtpArenaSize() + " to " + newRTPArenaSize + ".");
    ((FarmingWorldConfig) farmingWorld).setRtpArenaSize(newRTPArenaSize);
  }

  private void checkCurrentWorld(FarmingWorldConfig farmingWorld) {
    if (farmingWorld.getData().getCurrentWorldName() == null) {
      farmingWorld.newWorld(api.generateFarmingWorld(farmingWorld));
    } else {
      api.loadFarmingWorld(farmingWorld.getData().getCurrentWorldName(), farmingWorld.getEnvironment());
    }
  }

  private void checkNextWorld(FarmingWorldConfig farmingWorld) {
    if (farmingWorld.getData().getNextWorldName() == null) {
      final long remaining = farmingWorld.getReset() - System.currentTimeMillis();
      if (farmingWorld.needReset() || (remaining <= TimeUnit.MINUTES.toMillis(5) && remaining > 0)) {
        farmingWorld.setNextWorld(api.generateFarmingWorld(farmingWorld));
      }
    } else {
      api.loadFarmingWorld(farmingWorld.getData().getNextWorldName(), farmingWorld.getEnvironment());
    }
  }
}
