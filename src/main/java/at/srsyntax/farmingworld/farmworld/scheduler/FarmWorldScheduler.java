package at.srsyntax.farmingworld.farmworld.scheduler;

import at.srsyntax.farmingworld.FarmingWorldPlugin;
import at.srsyntax.farmingworld.api.API;
import at.srsyntax.farmingworld.api.farmworld.FarmWorld;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

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
public class FarmWorldScheduler implements Runnable {

    private final API api;
    private final FarmingWorldPlugin plugin;

    @Getter private final List<FarmWorld> updaterList = new CopyOnWriteArrayList<>();
    private final FarmWorldUpdater updater;

    public FarmWorldScheduler(API api, FarmingWorldPlugin plugin) {
        this.api = api;
        this.plugin = plugin;
        this.updater = new FarmWorldUpdater(this);
    }

    @Override
    public void run() {
        for (FarmWorld farmWorld : api.getFarmWorlds()) {
            if (needSecondUpdater(farmWorld) || updaterList.contains(farmWorld)) {
                addUpdater(farmWorld);
            } else {
                updater.update(farmWorld, false);
            }
        }
    }

    private boolean needSecondUpdater(FarmWorld farmWorld) {
        return farmWorld.getResetDate() - System.currentTimeMillis() <= TimeUnit.MINUTES.toMillis(2);
    }

    private void addUpdater(FarmWorld farmWorld) {
        if (updaterList.contains(farmWorld)) return;
        updaterList.add(farmWorld);
        createScheduler();
    }

    private void createScheduler() {
        if (updater.getTaskId() != -1) return;
        final int id = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, updater, 20L, 20L);
        updater.setTaskId(id);
    }

    protected void removeUpdater(FarmWorld farmWorld) {
        updaterList.remove(farmWorld);
        if (!updaterList.isEmpty()) return;
        Bukkit.getScheduler().cancelTask(updater.getTaskId());
        updater.setTaskId(-1);
    }
}
