package at.srsyntax.farmingworld.util;

import at.srsyntax.farmingworld.FarmingWorldPlugin;
import at.srsyntax.farmingworld.config.PluginConfig;
import lombok.AllArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

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
@AllArgsConstructor
public class JoinListener implements Listener {

    private final FarmingWorldPlugin plugin;

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        final var config = plugin.getPluginConfig();

        if (config.getSpawn() == null) return;
        final var location = config.getSpawn();
        if (location == null) return;

        final var player = event.getPlayer();

        if (config.getSpawnType() == PluginConfig.SpawnType.FORCE) {
            player.teleport(location.toBukkit());
        } else if (config.getSpawnType() == PluginConfig.SpawnType.FIRST) {
            if (System.currentTimeMillis() - player.getFirstPlayed() <= TimeUnit.SECONDS.toMillis(5)) {
                player.teleport(location.toBukkit());
            }
        }
    }
}
