package at.srsyntax.farmingworld.database.sqlite;

import at.srsyntax.farmingworld.database.repository.FarmWorldRepository;
import at.srsyntax.farmingworld.farmworld.FarmWorldData;
import at.srsyntax.farmingworld.farmworld.FarmWorldImpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
        delete(farmWorld.getName());
    }

    @Override
    public void delete(String name) {
        try {
            final String sql = "DELETE FROM farm_world WHERE name = ?";
            final PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, name);
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
