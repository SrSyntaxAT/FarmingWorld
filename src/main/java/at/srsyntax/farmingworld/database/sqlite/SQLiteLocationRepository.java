package at.srsyntax.farmingworld.database.sqlite;

import at.srsyntax.farmingworld.api.farmworld.FarmWorld;
import at.srsyntax.farmingworld.api.farmworld.LocationCache;
import at.srsyntax.farmingworld.database.repository.LocationRepository;
import lombok.SneakyThrows;
import org.bukkit.Location;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/*
 * MIT License
 *
 * Copyright (c) 2022-2023 Marcel Haberl
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
public class SQLiteLocationRepository implements LocationRepository {

    private final Connection connection;

    public SQLiteLocationRepository(Connection connection) throws SQLException {
        this.connection = connection;
        connection.createStatement()
                .execute("CREATE TABLE IF NOT EXISTS location_cache (farm_world TEXT, id TEXT, location TEXT)");
    }

    @SneakyThrows
    @Override
    public void save(FarmWorld farmWorld, String id, Location location) {
        final String sql = "INSERT INTO location_cache (farm_world,id,location) VALUES (?,?,?)";
        final PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, farmWorld.getName());
        statement.setString(2, id);
        statement.setString(3, new LocationCache(location).toString());
        statement.execute();
    }

    @SneakyThrows
    @Override
    public void delete(String id) {
        final String sql = "DELETE FROM location_cache WHERE id = ?";
        final PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, id);
        statement.execute();
    }

    @SneakyThrows
    @Override
    public void deleteByFarmWorldName(String name) {
        final String sql = "DELETE FROM location_cache WHERE farm_world = ?";
        final PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, name);
        statement.execute();
    }

    @Override
    public Map<String, LocationCache> getLocations(FarmWorld farmWorld) {
        final Map<String, LocationCache> map = new LinkedHashMap<>();

        try {
            final String sql = "SELECT id, location FROM location_cache WHERE farm_world = ?";
            final PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, farmWorld.getName());

            final ResultSet resultSet = statement.executeQuery();
            while (resultSet != null && resultSet.next()) {
                final LocationCache cache = LocationCache.fromJson(resultSet.getString("location"));
                map.put(resultSet.getString("id"), cache);
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        return map;
    }

    @Override
    public Map<String, Map<String, LocationCache>> getLocations() {
        final Map<String, Map<String, LocationCache>> map = new LinkedHashMap<>();

        try {
            final String sql = "SELECT * FROM location_cache";
            final PreparedStatement statement = connection.prepareStatement(sql);
            final ResultSet resultSet = statement.executeQuery();

            while (resultSet != null && resultSet.next()) {
                final String farmWorld = resultSet.getString("farm_world");
                map.putIfAbsent(farmWorld, new HashMap<>());
                final LocationCache cache = LocationCache.fromJson(resultSet.getString("location"));
                map.get(farmWorld).put(resultSet.getString("id"), cache);
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        return map;
    }
}
