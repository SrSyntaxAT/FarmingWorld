package at.srsyntax.farmingworld.farmworld.display;

import at.srsyntax.farmingworld.FarmingWorldPlugin;
import at.srsyntax.farmingworld.api.farmworld.FarmWorld;
import at.srsyntax.farmingworld.config.PluginConfig;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
@Getter
public class DisplayRegistry {

    private final FarmingWorldPlugin plugin;
    private final PluginConfig.ResetDisplayConfig config;

    private final Map<String, Displayer> displayers = new ConcurrentHashMap<>();

    public DisplayRegistry(FarmingWorldPlugin plugin, PluginConfig.ResetDisplayConfig config) {
        this.plugin = plugin;
        this.config = config;
        if (config.isBossBar())
            Bukkit.getPluginManager().registerEvents(new DisplayerListeners(this), plugin);
    }

    public void register(FarmWorld farmWorld) {
        displayers.put(farmWorld.getName(), new Displayer(this, farmWorld));
    }

    public void unregister(FarmWorld farmWorld) {
        final Displayer displayer = displayers.remove(farmWorld.getName());
        if (displayer == null) return;
        displayer.removePlayers();
    }

    public Displayer getDisplayer(FarmWorld farmWorld) {
        return displayers.get(farmWorld.getName());
    }
}
