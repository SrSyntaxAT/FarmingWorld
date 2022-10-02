package at.srsyntax.farmingworld.sign;

import at.srsyntax.farmingworld.FarmingWorldPlugin;
import at.srsyntax.farmingworld.api.FarmingWorld;
import at.srsyntax.farmingworld.database.Database;
import at.srsyntax.farmingworld.listener.SignListener;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.metadata.FixedMetadataValue;

import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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
public class SignRegistry {

    private final FarmingWorldPlugin plugin;
    @Getter private final Map<Location, SignCache> caches = new ConcurrentHashMap<>();

    public SignRegistry(FarmingWorldPlugin plugin) throws SQLException {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(new SignListener(this), plugin);
        load();
    }

    private void load() throws SQLException {
        final Database database = plugin.getDatabase();
        final Logger logger = plugin.getLogger();

        final var signs = database.getSignCache();

        for (SignCache cache : signs) {
            try {
                final Block block = cache.location().getWorld().getBlockAt(cache.location());

                if (block.isEmpty() || !block.getBlockData().getMaterial().name().endsWith("_SIGN")) {
                    logger.info(String.format("Sign was not found at %s.", cache.location()));
                    delete(cache);
                } else {
                    add(cache);
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        logger.info(caches.size() + "/" + signs.size() + " signs found.");
    }

    public void add(SignCache cache) {
        caches.put(cache.location(), cache);
        cache.farmingWorld().getSigns().add(cache);
    }

    public void delete(SignCache cache) throws SQLException {
        plugin.getDatabase().deleteSign(cache.location());
        cache.farmingWorld().getSigns().remove(cache);
    }

    public void save(SignCache cache) throws SQLException {
        plugin.getDatabase().saveSign(cache);
    }

    public SignCache get(Location location) {
        return caches.get(location);
    }
}
