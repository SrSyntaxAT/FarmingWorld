package at.srsyntax.farmingworld.farmworld;

import at.srsyntax.farmingworld.FarmingWorldPlugin;
import at.srsyntax.farmingworld.database.Database;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.io.File;
import java.io.IOException;

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
public class FarmWorldDeleter {

    private final FarmingWorldPlugin plugin;
    private final FarmWorldImpl farmWorld;

    public void delete() throws IOException {
        plugin.getPluginConfig().getFarmWorlds().remove(farmWorld);
        plugin.getPluginConfig().save(plugin);

        final Database database = plugin.getDatabase();
        database.getFarmWorldRepository().delete(farmWorld);

        farmWorld.setActive(false);
        // TODO: 18.12.2022 unregister commands

        if (farmWorld.isEnabled())
            deleteWhenEnabled();
        else deleteWhenDisabled();
    }

    public void disable() {
        if (!farmWorld.isActive() || !farmWorld.isEnabled()) return;

        farmWorld.setEnabled(false);
        farmWorld.setActive(false);

        // TODO: 18.12.2022 unregister commands

        unloadWorld(farmWorld.getWorld());
        unloadWorld(farmWorld.getNextWorld());
    }

    private void unloadWorld(World world) {
        if (world == null) return;
        final Location location = plugin.getPluginConfig().getFallback().toBukkit();
        world.getPlayers().forEach(player -> player.teleport(location));
        Bukkit.unloadWorld(world, true);
    }

    private void deleteWhenEnabled() {
        deleteWorld(farmWorld.getWorld());
        deleteWorld(farmWorld.getNextWorld());
    }

    private void deleteWhenDisabled() {
        final FarmWorldData data = farmWorld.getData();
        deletWorldByName(data.getCurrentWorldName());
        deletWorldByName(data.getNextWorldName());
    }

    private void deletWorldByName(String name) {
        if (name != null) deleteWorld(Bukkit.getWorld(name));
    }

    public void deleteWorld(World world) {
        if (world == null) return;
        plugin.getLogger().info("Delete " + world.getName());

        final Location fallback = plugin.getPluginConfig().getFallback().toBukkit();
        world.getPlayers().forEach(player -> player.teleport(fallback));

        Bukkit.unloadWorld(world, false);
        deleteFolder(world.getWorldFolder());
    }

    private boolean deleteFolder(File folder) {
        if (!folder.exists()) return false;

        if(folder.isDirectory()) {
            final File[] files = folder.listFiles();

            if(files != null) {
                for (File file : files) {
                    if (file.isDirectory())
                        deleteFolder(file);
                    else
                        file.delete();
                }
            }
        }

        return folder.delete();
    }
}
