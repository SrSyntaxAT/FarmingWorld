package at.srsyntax.farmingworld.farmworld;

import at.srsyntax.farmingworld.FarmingWorldPlugin;
import at.srsyntax.farmingworld.api.event.farmworld.FarmWorldDeletedEvent;
import at.srsyntax.farmingworld.api.event.farmworld.FarmWorldDisabledEvent;
import at.srsyntax.farmingworld.api.event.farmworld.FarmWorldEvent;
import at.srsyntax.farmingworld.database.Database;
import at.srsyntax.farmingworld.util.FileUtil;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.io.File;
import java.io.IOException;

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
public class FarmWorldDeleter {

    private final FarmingWorldPlugin plugin;
    private final FarmWorldImpl farmWorld;

    public void delete() throws IOException {
        plugin.getPluginConfig().getFarmWorlds().remove(farmWorld);
        plugin.getPluginConfig().save(plugin);

        final Database database = plugin.getDatabase();
        database.getFarmWorldRepository().delete(farmWorld);
        database.getLocationRepository().deleteByFarmWorldName(farmWorld.getName());

        farmWorld.setActive(false);
        plugin.getCommandRegistry().unregister(farmWorld);
        plugin.getDisplayRegistry().unregister(farmWorld);
        plugin.getSignRegistry().unregister(farmWorld);

        if (farmWorld.isEnabled())
            deleteWhenEnabled();
        else deleteWhenDisabled();

        FarmWorldEvent.call(FarmWorldDeletedEvent.class, farmWorld);
    }

    public void disable() {
        if (!farmWorld.isActive() || !farmWorld.isEnabled()) return;

        farmWorld.setEnabled(false);
        farmWorld.setActive(false);

        plugin.getCommandRegistry().unregister(farmWorld);
        plugin.getDisplayRegistry().unregister(farmWorld);

        unloadWorld(farmWorld.getWorld());
        unloadWorld(farmWorld.getNextWorld());

        FarmWorldEvent.call(FarmWorldDisabledEvent.class, farmWorld);
    }

    private void unloadWorld(World world) {
        if (world == null) return;
        final Location location = plugin.getPluginConfig().getFallback().toBukkit();
        world.getPlayers().forEach(player -> player.teleport(location));
        Bukkit.unloadWorld(world, true);
    }

    private void deleteWhenEnabled() {
        final FarmWorldData data = farmWorld.getData();
        deleteWorld(data.getCurrentWorldName());
        deleteWorld(data.getNextWorldName());
    }

    private void deleteWhenDisabled() {
        final FarmWorldData data = farmWorld.getData();
        deleteWorldByName(data.getCurrentWorldName());
        deleteWorldByName(data.getNextWorldName());
    }

    private void deleteWorldByName(String name) {
        if (name != null) deleteWorld(Bukkit.getWorld(name));
    }

    public void deleteWorld(String worldName) {
        if (worldName == null) return;
        plugin.getLogger().info("Delete " + worldName + " (F)");
        FileUtil.deleteFolder(new File(worldName));
    }

    public void deleteWorld(World world) {
        if (world == null) return;
        plugin.getLogger().info("Delete " + world.getName());

        final Location fallback = plugin.getPluginConfig().getFallback().toBukkit();
        world.getPlayers().forEach(player -> player.teleport(fallback));

        Bukkit.unloadWorld(world, false);
        FileUtil.deleteFolder(world.getWorldFolder());
    }
}
