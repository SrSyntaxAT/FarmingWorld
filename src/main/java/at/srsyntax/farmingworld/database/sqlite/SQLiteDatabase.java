package at.srsyntax.farmingworld.database.sqlite;

import at.srsyntax.farmingworld.database.Database;
import at.srsyntax.farmingworld.database.DatabaseException;
import at.srsyntax.farmingworld.database.repository.FarmWorldRepository;
import at.srsyntax.farmingworld.database.repository.LocationRepository;
import lombok.Getter;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;

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
public class SQLiteDatabase implements Database {

    private final Plugin plugin;
    @Getter private Connection connection;
    private FarmWorldRepository farmWorldRepository;
    private LocationRepository locationRepository;

    public SQLiteDatabase(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void connect() throws DatabaseException {
        try {
            final File file = new File(plugin.getDataFolder(), "database.db");
            this.connection = DriverManager.getConnection("jdbc:sqlite:" + file.getPath());
            this.farmWorldRepository = new SQLLiteFarmWorldRepository(connection);
            this.locationRepository = new SQLiteLocationRepository(connection);
        } catch (Exception exception) {
            throw new DatabaseException("An error occurred while connecting to the database.", exception);
        }
    }

    @Override
    public void disconnect() throws DatabaseException {
        try {
            if (connection != null && !connection.isClosed())
                connection.close();
        } catch (Exception exception) {
            throw new DatabaseException("The connection to the database could not be closed safely.", exception);
        }
    }

    @Override
    public FarmWorldRepository getFarmWorldRepository() {
        return farmWorldRepository;
    }

    @Override
    public LocationRepository getLocationRepository() {
        return locationRepository;
    }
}
