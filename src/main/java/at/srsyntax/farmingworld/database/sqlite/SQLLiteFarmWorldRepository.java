package at.srsyntax.farmingworld.database.sqlite;

import at.srsyntax.farmingworld.database.repository.FarmWorldRepository;
import at.srsyntax.farmingworld.farmworld.FarmWorldData;
import at.srsyntax.farmingworld.farmworld.FarmWorldImpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
public class SQLLiteFarmWorldRepository implements FarmWorldRepository {

    private final Connection connection;

    public SQLLiteFarmWorldRepository(Connection connection) throws SQLException {
        this.connection = connection;
        connection.createStatement()
                .execute("CREATE TABLE IF NOT EXISTS farm_world (name TEXT, current_world TEXT, next_world TEXT, created INTEGER )");
    }

    @Override
    public boolean exists(FarmWorldImpl farmWorld) {
        try {
            final String sql = "SELECT name FROM farm_world WHERE name = ?";
            final PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, farmWorld.getName());
            final ResultSet resultSet = statement.executeQuery();
            return resultSet != null && resultSet.next();
        } catch (SQLException ignored) {}
        return false;
    }

    @Override
    public void save(FarmWorldImpl farmWorld) {
        try {
            if (!exists(farmWorld)) create(farmWorld);
            else update(farmWorld);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    private void create(FarmWorldImpl farmWorld) throws SQLException {
        final FarmWorldData data = farmWorld.getData();
        final String sql = "INSERT INTO farm_world (name, current_world, next_world, created) VALUES (?,?,?,?)";
        final PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, farmWorld.getName());
        statement.setString(2, data.getCurrentWorldName());
        statement.setString(3, data.getNextWorldName());
        statement.setLong(4, data.getCreated());
        statement.execute();
    }

    private void update(FarmWorldImpl farmWorld) throws SQLException {
        final FarmWorldData data = farmWorld.getData();
        final String sql = "UPDATE farm_world SET current_world = ?, next_world = ?, created = ? WHERE name = ?";
        final PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, data.getCurrentWorldName());
        statement.setString(2, data.getNextWorldName());
        statement.setLong(3, data.getCreated());
        statement.setString(4, farmWorld.getName());
        statement.execute();
    }

    @Override
    public void delete(FarmWorldImpl farmWorld) {
        try {
            final String sql = "DELETE FROM farm_world WHERE name = ?";
            final PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, farmWorld.getName());
            statement.execute();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public FarmWorldData getFarmWorldData(String farmWorld) {
        try {
            final String sql = "SELECT current_world, next_world, created FROM farm_world WHERE name = ?";
            final PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, farmWorld);

            final ResultSet resultSet = statement.executeQuery();
            if (resultSet == null || !resultSet.next()) return null;

            return new FarmWorldData(resultSet);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return null;
    }
}
