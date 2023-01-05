package at.srsyntax.farmingworld.farmworld;

import at.srsyntax.farmingworld.FarmingWorldPlugin;
import at.srsyntax.farmingworld.api.farmworld.Border;
import at.srsyntax.farmingworld.api.farmworld.LocationCache;
import at.srsyntax.farmingworld.database.repository.FarmWorldRepository;
import at.srsyntax.farmingworld.database.repository.LocationRepository;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldBorder;

import java.util.Map;
import java.util.UUID;

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
@AllArgsConstructor
public class FarmWorldLoader {

    private final FarmingWorldPlugin plugin;
    private final FarmWorldImpl farmWorld;

    public void load() {
        if (farmWorld.isLoaded()) {
            plugin.getLogger().severe(farmWorld.getName() + " is already loaded.");
            return;
        }

        plugin.getLogger().info("Load " + farmWorld.getName() + "...");
        farmWorld.setPlugin(plugin);
        setDataFromDatabase();
        // TODO: 17.12.2022 Register farm world command

        farmWorld.setLoaded(true);
        if (!farmWorld.isActive())
            plugin.getLogger().warning(farmWorld.getName() + " is not active!");
        else enable();
    }

    public void enable() {
        if (!farmWorld.isLoaded()) load();
        plugin.getLogger().info("Enable " + farmWorld.getName() + "...");
        loadCurrentWorld();
        if (farmWorld.hasNext()) generateWorld(farmWorld.getData().getNextWorldName());
        loadLocationCaches();
        setBorder(farmWorld.getWorld());
        setBorder(farmWorld.getNextWorld());
        farmWorld.setEnabled(true);
        farmWorld.save(plugin);
    }

    private void loadCurrentWorld() {
        if (farmWorld.getData().getCurrentWorldName() != null && (farmWorld.needReset() || farmWorld.needNextWorld())) {
            final FarmWorldData data = farmWorld.getData();
            new FarmWorldDeleter(plugin, farmWorld).deleteWorld(data.getCurrentWorldName());
            data.setCurrentWorldName(null);
            farmWorld.next();
        } else generateWorld(farmWorld.getData().getCurrentWorldName());
    }

    private void setDataFromDatabase()  {
        final FarmWorldRepository repository = plugin.getDatabase().getFarmWorldRepository();
        if (!repository.exists(farmWorld)) {
            farmWorld.setData(new FarmWorldData(0L, null, null));
        } else {
            farmWorld.setData(repository.getFarmWorldData(farmWorld.getName()));
        }
    }

    public World generateWorld() {
        final String id = UUID.randomUUID().toString().split("-")[0];
        final String worldName = String.format("%s-%s", farmWorld.getName(), id);
        return generateWorld(worldName);
    }

    public World generateWorld(String worldName) {
        if (worldName == null) return null;
        final World world = Bukkit.createWorld(farmWorld.createWorldCreator(worldName));
        setBorder(world);
        return world;
    }

    public void setBorder(World world) {
        final Border border = farmWorld.getBorder();
        if (border == null || world == null) return;
        if (border.getSize() < 10) return;
        if (world.getEnvironment() == World.Environment.THE_END) return;

        final WorldBorder worldBorder = world.getWorldBorder();
        worldBorder.setSize(border.getSize());
        worldBorder.setCenter(border.getCenterX(), border.getCenterZ());
    }

    private void loadLocationCaches() {
         final Map<String, LocationCache> caches = getLocationRepository().getLocations(farmWorld);
         caches.forEach((id, locationCache) -> {
             if (farmWorld.getData() == null || !farmWorld.getData().getCurrentWorldName().equalsIgnoreCase(locationCache.getWorld()))
                 getLocationRepository().delete(id);
             else loadLocation(id, locationCache.toBukkit(), false);
         });
         checkLocations();
    }

    public void checkLocations() {
        if (plugin.getPluginConfig().getLocationCache() <= 0) return;
        int need = plugin.getPluginConfig().getLocationCache() - farmWorld.getLocations().size();
        if (need > 0) {
            plugin.getLogger().info(String.format("%d new locations are generated for %s.", need, farmWorld.getName()));
            for (; need > 0; need--)
                generateLocation(true);
        }
    }

    public Location generateLocation(boolean save) {
        final Location location = FarmingWorldPlugin.getApi().createLocationRandomizer(farmWorld).random();
        loadLocation(UUID.randomUUID().toString(), location, save);
        return location;
    }

    private void loadLocation(String id, Location location, boolean save) {
        farmWorld.addLocation(id, location);
        if (save) getLocationRepository().save(farmWorld, UUID.randomUUID().toString(), location);
        location.getWorld().loadChunk(location.getChunk());
    }

    private LocationRepository getLocationRepository() {
        return plugin.getDatabase().getLocationRepository();
    }
}
