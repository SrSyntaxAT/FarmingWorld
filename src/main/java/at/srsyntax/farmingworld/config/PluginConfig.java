package at.srsyntax.farmingworld.config;

import at.srsyntax.farmingworld.api.farmworld.Border;
import at.srsyntax.farmingworld.api.farmworld.LocationCache;
import at.srsyntax.farmingworld.farmworld.FarmWorldImpl;
import com.google.gson.GsonBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
@AllArgsConstructor
@Getter @Setter
public class PluginConfig {

    private String version;
    private final double refund;
    private final CountdownConfig countdown;
    private final String defaultFarmWorld;
    private final List<FarmWorldImpl> farmWorlds;
    private final List<Material> blacklist;
    private final int locationCache;
    private final LocationCache fallback;
    private final MessageConfig messages;

    public PluginConfig(Plugin plugin, Location fallbackLocation) {
        this(
                plugin.getDescription().getVersion(),
                1D,
                new CountdownConfig(
                        5,
                        .7D,
                        false,
                        ChatMessageType.ACTION_BAR
                ),
                "FarmWorld",
                Collections.singletonList(
                        new FarmWorldImpl(
                                "FarmWorld", 
                                null,
                                1800, 5, 43_200,
                                World.Environment.NORMAL, null,
                                new Border(10000, 0, 0),
                                Collections.singletonList("FarmWorld")
                        )
                ),
                Arrays.asList(Material.AIR, Material.LAVA, Material.WATER),
                3,
                new LocationCache(fallbackLocation),
                new MessageConfig(
                        "&cYou don't have enough money.",
                        new MessageConfig.CountdownMessages(
                                "&cA countdown is already running.",
                                "&cThe countdown was interrupted because you moved.",
                                "&7You will be teleported in &e%s &7seconds."
                        ),
                        new MessageConfig.CooldownMessages(
                                "&cYou may use the command in &e%{remaining}&7."
                        ),
                        new MessageConfig.CommandMessages(
                                ChatMessageType.ACTION_BAR,
                                "&cPlayer not found!",
                                "&cFarm world not found!",
                                "&cFarm world was not found.",
                                "&cPlayer or farm world was not found.",
                                "&cYou have no rights to enter the farm world.",
                                "&cYou have no rights to teleport other players to the farm world.",
                                "&aYou have been teleported to &e%{farmworld}&a.",
                                "&aYou teleported &e%{player} &ato &e%{farmworld}&a."
                        ),
                        new MessageConfig.TimeMessages(
                                "HH:mm:ss dd.MM.yyyy",
                                "second", "seconds",
                                "minute", "minutes",
                                "hour", "hours",
                                "day", "days"
                        )
                )
        );
    }

    public void save(Plugin plugin) throws IOException {
        final File file = new File(plugin.getDataFolder(), ConfigLoader.CONFIG_FILENAME);

        if (!plugin.getDataFolder().exists()) plugin.getDataFolder().mkdirs();
        if (!file.exists()) file.createNewFile();

        final String json = new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .create()
                .toJson(this);
        Files.write(
                file.toPath(),
                Arrays.asList(json.split("\n")),
                StandardCharsets.UTF_8
        );
    }

    @AllArgsConstructor
    @Getter
    public static class CountdownConfig {
        private final int time;
        private final double permittedDistance;
        private final boolean movementAllowed;
        private final ChatMessageType messageType;
    }

}
