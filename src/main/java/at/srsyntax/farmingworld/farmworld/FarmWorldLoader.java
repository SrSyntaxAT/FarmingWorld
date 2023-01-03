package at.srsyntax.farmingworld.farmworld;

import at.srsyntax.farmingworld.FarmingWorldPlugin;
import at.srsyntax.farmingworld.api.farmworld.Border;
import at.srsyntax.farmingworld.api.farmworld.LocationCache;
import at.srsyntax.farmingworld.api.farmworld.LocationRandomizer;
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
        if (farmWorld.needReset() || farmWorld.needNextWorld()) {
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
        int need = plugin.getPluginConfig().getLocationCache() - farmWorld.getLocations().size();
        if (need > 0) {
            plugin.getLogger().info(String.format("%d new locations are generated for %s.", need, farmWorld.getName()));
            for (; need > 0; need--)
                generateLocation(true);
        }
    }

    public void generateLocation(boolean save) {
        final LocationRandomizer randomizer = FarmingWorldPlugin.getApi().createLocationRandomizer(farmWorld);
        loadLocation(UUID.randomUUID().toString(), randomizer.random(), save);
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
