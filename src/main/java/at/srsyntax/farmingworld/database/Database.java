package at.srsyntax.farmingworld.database;

import at.srsyntax.farmingworld.api.FarmingWorld;
import at.srsyntax.farmingworld.util.location.LocationCache;

import java.sql.SQLException;
import java.util.Map;

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
public interface Database {

  void connect() throws SQLException;
  void disconnect();
  boolean isConnected() throws SQLException;

  boolean exists(FarmingWorld farmingWorld) throws SQLException;
  void deleteFarmingWorld(FarmingWorld farmingWorld) throws SQLException;
  void createFarmingWorld(FarmingWorld farmingWorld) throws SQLException;
  void updateNextWorld(FarmingWorld farmingWorld) throws SQLException;
  void updateWorld(FarmingWorld farmingWorld) throws SQLException;
  FarmingWorldData getData(String name) throws SQLException;

  Map<String, LocationCache> getLocations(FarmingWorld farmingWorld) throws SQLException;
  void removeLocation(String id) throws SQLException;
  void removeLocations(FarmingWorld farmingWorld) throws SQLException;
  void addLocation(FarmingWorld farmingWorld, String id, LocationCache locationCache) throws SQLException;

}