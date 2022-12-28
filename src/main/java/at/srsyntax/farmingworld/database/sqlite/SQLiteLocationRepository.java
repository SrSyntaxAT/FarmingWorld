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
 * CONFIDENTIAL
 *  Unpublished Copyright (c) 2022 Marcel Haberl, All Rights Reserved.
 *
 * NOTICE:
 * All information contained herein is, and remains the property of Marcel Haberl. The intellectual and
 * technical concepts contained herein are proprietary to Marcel Haberl and may be covered by U.S. and Foreign
 * Patents, patents in process, and are protected by trade secret or copyright law. Dissemination of this
 * information or reproduction of this material is strictly forbidden unless prior written permission is obtained
 * from Marcel Haberl.  Access to the source code contained herein is hereby forbidden to anyone without written
 * permission Confidentiality and Non-disclosure agreements explicitly covering such access.
 *
 * The copyright notice above does not evidence any actual or intended publication or disclosure  of  this source code,
 * which includes information that is confidential and/or proprietary, and is a trade secret, of Marcel Haberl.
 * ANY REPRODUCTION, MODIFICATION, DISTRIBUTION, PUBLIC  PERFORMANCE, OR PUBLIC DISPLAY OF OR THROUGH USE  OF THIS
 * SOURCE CODE  WITHOUT  THE EXPRESS WRITTEN CONSENT OF Marcel Haberl IS STRICTLY PROHIBITED, AND IN VIOLATION OF
 * APPLICABLE LAWS AND INTERNATIONAL TREATIES.  THE RECEIPT OR POSSESSION OF  THIS SOURCE CODE AND/OR RELATED
 * INFORMATION DOES NOT CONVEY OR IMPLY ANY RIGHTS TO REPRODUCE, DISCLOSE OR DISTRIBUTE ITS CONTENTS, OR TO
 * MANUFACTURE, USE, OR SELL ANYTHING THAT IT  MAY DESCRIBE, IN WHOLE OR IN PART.
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
    public void delete(FarmWorld farmWorld) {
        final String sql = "DELETE FROM location_cache WHERE farm_world = ?";
        final PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, farmWorld.getName());
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
