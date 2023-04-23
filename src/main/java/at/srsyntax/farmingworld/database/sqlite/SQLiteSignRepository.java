package at.srsyntax.farmingworld.database.sqlite;

import at.srsyntax.farmingworld.FarmingWorldPlugin;
import at.srsyntax.farmingworld.api.API;
import at.srsyntax.farmingworld.api.farmworld.LocationCache;
import at.srsyntax.farmingworld.api.farmworld.sign.SignCache;
import at.srsyntax.farmingworld.database.repository.SignRepository;
import at.srsyntax.farmingworld.farmworld.sign.SignCacheImpl;
import lombok.SneakyThrows;
import org.bukkit.block.Sign;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
public class SQLiteSignRepository implements SignRepository {

    private final Connection connection;

    public SQLiteSignRepository(Connection connection) throws SQLException {
        this.connection = connection;
        connection.createStatement()
                .execute("CREATE TABLE IF NOT EXISTS sign (farm_world TEXT, location TEXT)");
    }

    @SneakyThrows
    @Override
    public void save(SignCacheImpl cache) {
        final String sql = "INSERT INTO sign (farm_world, location) VALUES (?,?)";
        final PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, cache.getFarmWorld().getName());
        statement.setString(2, new LocationCache(cache.getSign().getLocation()).toString());
        statement.execute();
    }

    @SneakyThrows
    @Override
    public void delete(LocationCache cache) {
        final String sql = "DELETE FROM sign WHERE location = ?";
        final PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, cache.toString());
        statement.execute();
    }

    @SneakyThrows
    @Override
    public void delete(String farmWorld) {
        final String sql = "DELETE FROM sign WHERE farm_world = ?";
        final PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, farmWorld);
        statement.execute();
    }

    @SneakyThrows
    @Override
    public List<SignCache> getCache() {
        final String sql = "SELECT * FROM sign";
        final PreparedStatement statement = connection.prepareStatement(sql);

        final API api = FarmingWorldPlugin.getApi();
        final List<SignCache> result = new ArrayList<>();
        final ResultSet resultSet = statement.executeQuery();
        final List<String> deleted = new ArrayList<>();

        while (resultSet != null && resultSet.next()) {
            final String worldName = resultSet.getString("farm_world");
            if (worldName == null) continue;
            if (deleted.contains(worldName)) continue;

            final var farmWorld = api.getFarmWorld(worldName);
            if (farmWorld == null) {
                deleted.add(worldName);
                delete(worldName);
                continue;
            }

            final var cache = LocationCache.fromJson(resultSet.getString("location"));
            final var location = cache.toBukkit();

            if (location.getWorld() == null || !location.getBlock().getType().name().endsWith("_SIGN")) {
                delete(cache);
                continue;
            }

            final Sign sign = (Sign) location.getBlock().getState();
            result.add(new SignCacheImpl(sign, cache, farmWorld));
        }

        return result;
    }
}
