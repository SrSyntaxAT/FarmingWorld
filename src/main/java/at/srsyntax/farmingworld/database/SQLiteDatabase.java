package at.srsyntax.farmingworld.database;

import at.srsyntax.farmingworld.FarmingWorldPlugin;
import at.srsyntax.farmingworld.api.FarmingWorld;
import at.srsyntax.farmingworld.config.FarmingWorldConfig;
import at.srsyntax.farmingworld.database.data.CooldownData;
import at.srsyntax.farmingworld.database.data.FarmingWorldData;
import at.srsyntax.farmingworld.sign.SignCache;
import at.srsyntax.farmingworld.util.location.LocationCache;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import java.io.File;
import java.lang.reflect.Type;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
public final class SQLiteDatabase implements Database {

  private final FarmingWorldPlugin plugin;
  private Connection connection;

  public SQLiteDatabase(FarmingWorldPlugin plugin) {
    this.plugin = plugin;
  }

  @Override
  public void connect() throws SQLException {
    final File file = new File(this.plugin.getDataFolder(), "database.db");
    this.connection = DriverManager.getConnection("jdbc:sqlite:" + file.getPath());

    execute("CREATE TABLE IF NOT EXISTS farmingworld (name TEXT, current_world TEXT, next_world TEXT, created INTEGER)");
    execute("CREATE TABLE IF NOT EXISTS location_cache (farmingworld TEXT, id TEXT, location TEXT)");
    execute("CREATE TABLE IF NOT EXISTS player (id TEXT, cooldown TEXT)");
    execute("CREATE TABLE IF NOT EXISTS sign (farmingWorld TEXT, location TEXT)");
  }

  @Override
  public void disconnect() {
    try {
      if (connection != null && !connection.isClosed())
        connection.close();
    } catch (SQLException ignored) {}
  }

  @Override
  public boolean isConnected() throws SQLException {
    return connection != null && !connection.isClosed();
  }

  @Override
  public boolean exists(FarmingWorld farmingWorld) throws SQLException {
    final String sql = "SELECT name FROM farmingworld WHERE name = ?";
    final PreparedStatement statement = getConnection().prepareStatement(sql);
    statement.setString(1, farmingWorld.getName());
    final ResultSet resultSet = statement.executeQuery();
    return resultSet != null && resultSet.next();
  }

  @Override
  public void deleteFarmingWorld(FarmingWorld farmingWorld) throws SQLException {
    final String sql = "DELETE FROM farmingworld WHERE name=?";
    final PreparedStatement statement = getConnection().prepareStatement(sql);
    statement.setString(1, farmingWorld.getName());
    statement.execute();
  }

  @Override
  public void createFarmingWorld(FarmingWorld farmingWorld) throws SQLException {
    final FarmingWorldData data = ((FarmingWorldConfig) farmingWorld).getData();
    final String sql = "INSERT INTO farmingworld (name, current_world, next_world, created) VALUES (?,?,?,?)";
    final PreparedStatement statement = getConnection().prepareStatement(sql);
    statement.setString(1, farmingWorld.getName());
    statement.setString(2, data.getCurrentWorldName());
    statement.setString(3, data.getNextWorldName());
    statement.setLong(4, data.getCreated());
    statement.execute();
  }

  @Override
  public void updateNextWorld(FarmingWorld farmingWorld) throws SQLException {
    final String sql = "UPDATE farmingworld SET next_world = ? WHERE name = ?";
    final PreparedStatement statement = getConnection().prepareStatement(sql);
    statement.setString(1, ((FarmingWorldConfig) farmingWorld).getData().getNextWorldName());
    statement.setString(2, farmingWorld.getName());
    statement.execute();
  }

  @Override
  public void updateWorld(FarmingWorld farmingWorld) throws SQLException {
    final String sql = "UPDATE farmingworld SET current_world = ?, created = ? WHERE name = ?";
    final PreparedStatement statement = getConnection().prepareStatement(sql);
    statement.setString(1, ((FarmingWorldConfig) farmingWorld).getData().getCurrentWorldName());
    statement.setLong(2, farmingWorld.getCreated());
    statement.setString(3, farmingWorld.getName());
    statement.execute();
  }

  @Override
  public FarmingWorldData getData(String name) throws SQLException {
    final String sql = "SELECT created, current_world, next_world FROM farmingworld WHERE  name = ?";
    final PreparedStatement statement = getConnection().prepareStatement(sql);
    statement.setString(1, name);

    final ResultSet resultSet = statement.executeQuery();
    if (resultSet == null) return null;

    return new FarmingWorldData(resultSet);
  }

  @Override
  public Map<String, LocationCache> getLocations(FarmingWorld farmingWorld) throws SQLException {
    final String sql = "SELECT id, location FROM location_cache WHERE farmingworld = ?";
    final PreparedStatement statement = getConnection().prepareStatement(sql);
    statement.setString(1, farmingWorld.getName());
    final ResultSet resultSet = statement.executeQuery();
    if (resultSet == null) return null;
    return readResultSet(resultSet);
  }

  private Map<String, LocationCache> readResultSet(ResultSet resultSet) throws SQLException {
    final Map<String, LocationCache> result = new HashMap<>(resultSet.getFetchSize());

    while (resultSet.next()) {
      result.put(resultSet.getString("id"), LocationCache.fromJson(resultSet.getString("location")));
    }

    return result;
  }

  @Override
  public void removeLocation(String id) throws SQLException {
    final String sql = "DELETE FROM location_cache WHERE id = ?";
    final PreparedStatement statement = getConnection().prepareStatement(sql);
    statement.setString(1, id);
    statement.execute();
  }

  @Override
  public void removeLocations(FarmingWorld farmingWorld) throws SQLException {
    final String sql = "DELETE FROM location_cache WHERE farmingworld = ?";
    final PreparedStatement statement = getConnection().prepareStatement(sql);
    statement.setString(1, farmingWorld.getName());
    statement.execute();
  }

  @Override
  public void addLocation(FarmingWorld farmingWorld, String id, LocationCache locationCache) throws SQLException {
    final String sql = "INSERT INTO location_cache (farmingworld,id,location) VALUES (?,?,?)";
    final PreparedStatement statement = getConnection().prepareStatement(sql);
    statement.setString(1, farmingWorld.getName());
    statement.setString(2, id);
    statement.setString(3, locationCache.toString());
    statement.execute();
  }

  private void execute(String sql) throws SQLException {
    this.connection.createStatement().execute(sql);
  }

  private Connection getConnection() throws SQLException {
    if (!isConnected()) connect();
    return connection;
  }

  @Override
  public boolean existsPlayer(Player player) throws SQLException {
    final String sql = "SELECT id FROM player WHERE id = ?";
    final PreparedStatement statement = getConnection().prepareStatement(sql);
    statement.setString(1, getPlayerId(player));
    final ResultSet resultSet = statement.executeQuery();
    return resultSet != null && resultSet.next();
  }

  @Override
  public void createPlayer(Player player) throws SQLException {
    final String sql = "INSERT INTO player (id, cooldown) VALUES (?,?)";
    final PreparedStatement statement = getConnection().prepareStatement(sql);
    statement.setString(1, getPlayerId(player));
    statement.setString(2, getPlayerCooldownJson(player));
    statement.execute();
  }

  @Override
  public void updateCooldown(Player player) throws SQLException {
    final String sql = "UPDATE player SET cooldown = ? WHERE id = ?";
    final PreparedStatement statement = getConnection().prepareStatement(sql);
    statement.setString(1, getPlayerCooldownJson(player));
    statement.setString(2, getPlayerId(player));
    statement.execute();
  }

  @Override
  public void insertPlayerData(Player player) throws SQLException {
    final ResultSet resultSet = getPlayerData(player);
    if (resultSet == null) return;
    final Type type = new TypeToken<ArrayList<CooldownData>>(){}.getType();
    final List<CooldownData> list = new Gson().fromJson(resultSet.getString("cooldown"), type);

    boolean update = false;

    for (CooldownData data : list) {
      final FarmingWorld farmingWorld = FarmingWorldPlugin.getApi().getFarmingWorld(data.getFarmingWorld());
      if (farmingWorld == null) {
        update = true;
        continue;
      }

      final MetadataValue value = new FixedMetadataValue(this.plugin, data);
      player.setMetadata(CooldownData.METADATA_KEY + data.getFarmingWorld(), value);
    }

    if (update)
      updateCooldown(player);
  }

  private ResultSet getPlayerData(Player player) throws SQLException {
    final String sql = "SELECT cooldown FROM player WHERE id = ?";
    final PreparedStatement statement = getConnection().prepareStatement(sql);
    statement.setString(1, getPlayerId(player));

    final ResultSet resultSet = statement.executeQuery();
    if (resultSet == null || !resultSet.next()) return null;

    return resultSet;
  }

  private String getPlayerId(Player player) {
    return this.plugin.getPluginConfig().isOffline() ? player.getName() : player.getUniqueId().toString();
  }

  private String getPlayerCooldownJson(Player player) {
    final List<CooldownData> data = new ArrayList<>();

    this.plugin.getPluginConfig().getFarmingWorlds().forEach(farmingWorld -> {
      if (player.hasMetadata(CooldownData.METADATA_KEY + farmingWorld.getName())) {
        final MetadataValue value = player.getMetadata(CooldownData.METADATA_KEY + farmingWorld.getName()).get(0);
        data.add((CooldownData) value.value());
      }
    });

    return new GsonBuilder().disableHtmlEscaping().create().toJson(data);
  }

  @Override
  public void saveSign(SignCache cache) throws SQLException {
    final String sql = "INSERT INTO sign (farmingWorld, location) VALUES (?,?)";
    final PreparedStatement statement = getConnection().prepareStatement(sql);
    statement.setString(1, cache.farmingWorld().getName());
    statement.setString(2, new LocationCache(cache.location()).toString());
    statement.execute();
  }

  @Override
  public void deleteSign(Location location) throws SQLException {
    final String sql = "DELETE FROM sign WHERE location = ?";
    final PreparedStatement statement = getConnection().prepareStatement(sql);
    statement.setString(1, new LocationCache(location).toString());
    statement.execute();
  }

  @Override
  public List<SignCache> getSignCache() throws SQLException {
    final List<SignCache> list = new ArrayList<>();
    final String sql = "SELECT * FROM sign";
    final ResultSet resultSet = getConnection().createStatement().executeQuery(sql);

    while (resultSet.next()) {
      final String farmingWorldName = resultSet.getString("farmingWorld");
      final FarmingWorld farmingWorld = FarmingWorldPlugin.getApi().getFarmingWorld(farmingWorldName);
      final Location location = LocationCache.fromJson(resultSet.getString("location")).toBukkit();

      if (farmingWorld == null) {
        plugin.getLogger().severe(String.format("The farm world %s for the sign on %s no longer exists.", farmingWorldName, location));
        deleteSign(location);
      } else {
        list.add(new SignCache((FarmingWorldConfig) farmingWorld, location));
      }
    }

    return list;
  }
}
