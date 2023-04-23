package at.srsyntax.farmingworld.config;

import at.srsyntax.farmingworld.api.farmworld.Border;
import at.srsyntax.farmingworld.api.farmworld.LocationCache;
import at.srsyntax.farmingworld.farmworld.FarmWorldImpl;
import at.srsyntax.farmingworld.farmworld.display.ResetDisplayType;
import com.google.gson.GsonBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
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
    private final SignConfig sign;
    private final int locationCache;
    private final boolean spawnCommandEnabled;
    private final ResetDisplayConfig resetDisplay;
    private LocationCache fallback;
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
                new SignConfig(
                        "dd.MM.yyyy",
                        "HH:mm",
                        new String[]{
                                "&7[&bFarm World&7]",
                                "&6%{farm_world}",
                                "&cReset at:",
                                "&e%{date}"
                        }
                ),
                3,
                true,
                new ResetDisplayConfig(
                        true,
                        ResetDisplayType.BOSS_BAR,
                        BarStyle.SEGMENTED_20,
                        BarColor.BLUE,
                        true,
                        "&cReset:&e %{date}",
                        "HH:mm:ss dd.MM.yyyy"
                ),
                new LocationCache(fallbackLocation),
                new MessageConfig(
                        "&cYou don't have enough money.",
                        new MessageConfig.SpawnMessages(
                                ChatMessageType.ACTION_BAR,
                                "&cNo spawn was found!",
                                "&aYou have been teleported to the spawn."
                        ),
                        new MessageConfig.CountdownMessages(
                                "&cA countdown is already running.",
                                "&cThe countdown was interrupted because you moved.",
                                "&cThe countdown was canceled for an unknown reason.",
                                "&7You will be teleported in &e%s &7seconds."
                        ),
                        new MessageConfig.CooldownMessages(
                                "&cYou may use the command in &e%{remaining}&7."
                        ),
                        new MessageConfig.CommandMessages(
                                ChatMessageType.ACTION_BAR,
                                "&cThe farm world is disabled.",
                                "&cPlayer not found!",
                                "&cFarm world not found!",
                                "&cFarm world was not found.",
                                "&cPlayer or farm world was not found.",
                                "&cYou have no rights to enter the farm world.",
                                "&cYou have no rights to teleport other players to the farm world.",
                                "&aYou have been teleported to &e%{farmworld}&a.",
                                "&aYou teleported &e%{player} &ato &e%{farmworld}&a."
                        ),
                        new MessageConfig.AdminCommandMessages(
                                "&cThe sender must be a player.",
                                "&cYou have no rights to do this.",
                                "&cUsage&8: &f/fwa %s",
                                "&aThe spawn was set.",
                                "&cThere was an error while setting the spawn.",
                                "&cNo farm worlds found.",
                                "&aThe farm world has been reset.",
                                "&fConfirm the action within &a10 seconds &fwith &e/fwa confirm&f.",
                                "&cThe confirmation request has expired or none was found for you.",
                                "&cConfigurations will be reloaded.",
                                "&cAn error occurred while reloading the configuration.",
                                "&cThe countdown has been stopped as the farm worlds are reloaded.",
                                "&cFarm world was deleted.",
                                "&cAn error occurred while deleting the farm world.",
                                "&eFarm world &chas been disabled.",
                                "&eFarm world &ahas been enabled."
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

    @AllArgsConstructor
    @Getter
    public static class SignConfig {
        private final String daysFormat, hoursFormat;
        private final String[] lines;
    }

    @AllArgsConstructor
    @Getter
    public static class ResetDisplayConfig {
        private final boolean enabled;
        private final ResetDisplayType type;
        private final BarStyle barStyle;
        private final BarColor barColor;
        private final boolean changeBossBarProgress;
        private final String message;
        private final String dateFormat;

        public boolean isBossBar() {
            return type != null && type == ResetDisplayType.BOSS_BAR;
        }
    }
}
