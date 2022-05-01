package at.srsyntax.farmingworld.database;

import at.srsyntax.farmingworld.api.FarmingWorld;
import at.srsyntax.farmingworld.config.FarmingWorldConfig;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.*;

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

  private final JavaPlugin plugin;
  private Connection connection;

  public SQLiteDatabase(JavaPlugin plugin) {
    this.plugin = plugin;
  }

  @Override
  public void connect() throws SQLException {
    final File file = new File(this.plugin.getDataFolder(), "database.db");
    this.connection = DriverManager.getConnection("jdbc:sqlite:" + file.getPath());

    execute("CREATE TABLE IF NOT EXISTS farmingworld (name TEXT, current_world TEXT, next_world TEXT, created INTEGER)");
    // TODO 4 RTP improvement
    //execute("CREATE TABLE IF NOT EXISTS random_locations (farmingworld TEXT, id TEXT, location TEXT)");
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

  private void execute(String sql) throws SQLException {
    this.connection.createStatement().execute(sql);
  }

  private Connection getConnection() throws SQLException {
    if (!isConnected()) connect();
    return connection;
  }
}
