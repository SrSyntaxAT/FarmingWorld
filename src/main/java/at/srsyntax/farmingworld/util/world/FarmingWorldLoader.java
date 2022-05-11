package at.srsyntax.farmingworld.util.world;

import at.srsyntax.farmingworld.FarmingWorldPlugin;
import at.srsyntax.farmingworld.api.API;
import at.srsyntax.farmingworld.api.FarmingWorld;
import at.srsyntax.farmingworld.config.FarmingWorldConfig;
import at.srsyntax.farmingworld.database.Database;
import at.srsyntax.farmingworld.database.data.FarmingWorldData;
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
  private final Database database;

  public void load(FarmingWorldConfig farmingWorld) {
    logger.info("Load " + farmingWorld.getName() + "...");
    farmingWorld.setPlugin(plugin);
    setDatabaseData(farmingWorld);
    if (!farmingWorld.isActiv()) {
      logger.info(farmingWorld.getName() + " is not activ!");
      return;
    }
    enable(farmingWorld);
  }

  public void enable(FarmingWorldConfig farmingWorld) {
    logger.info("Enable " + farmingWorld.getName());
    checkBorder(farmingWorld);
    checkCurrentWorld(farmingWorld);
    checkNextWorld(farmingWorld);
    farmingWorld.async(() -> checkLocations(farmingWorld.getData()));
  }

  private boolean checkDatabase(FarmingWorldConfig farmingWorld) throws SQLException {
    if (database.exists(farmingWorld)) return true;
    if (farmingWorld.getData() == null)
      farmingWorld.setData(new FarmingWorldData(0L, null, null));
    database.createFarmingWorld(farmingWorld);
    return false;
  }

  private void setDatabaseData(FarmingWorldConfig farmingWorld) {
    try {
      if (checkDatabase(farmingWorld)) {
        farmingWorld.setData(plugin.getDatabase().getData(farmingWorld.getName()));
      }
      farmingWorld.getData().setFarmingWorld(farmingWorld);
    } catch (SQLException e) {
      databaseError(e);
    }
  }

  private void checkLocations(FarmingWorldData data) {
    try {
      if (data == null) return;
      data.getLocationCache().putAll(database.getLocations(data.getFarmingWorld()));

      if (data.getLocationCache().size() < 3)
        generateMoreRtpLocations(data.getFarmingWorld().getName(), data);

      data.loadChunks();
    } catch (SQLException e) {
      databaseError(e);
    }
  }

  private void databaseError(Exception exception) {
    logger.severe("Data could not be read from the database!");
    exception.printStackTrace();
  }

  private void generateMoreRtpLocations(String name, FarmingWorldData data) {
    logger.warning( name + " has too few rtp locations. (" + data.getLocationCache().size() + ")");
    logger.warning("New ones are generated...");

    for (byte need = (byte) (3 - data.getLocationCache().size()); need != 0; need--)
      data.getLocationCache().putAll(data.generateLocation());

    data.saveAllLocations();
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
