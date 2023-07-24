package at.srsyntax.farmingworld.config;

import at.srsyntax.farmingworld.api.farmworld.Border;
import at.srsyntax.farmingworld.api.farmworld.LocationCache;
import at.srsyntax.farmingworld.farmworld.FarmWorldImpl;
import at.srsyntax.farmingworld.farmworld.display.ResetDisplayType;
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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
@Getter @Setter
public class PluginConfig extends Config {

    private transient final String fileName = "config.json";

    private String version;
    private final double refund;
    private final CountdownConfig countdown;
    private final String defaultFarmWorld;
    private final List<FarmWorldImpl> farmWorlds;
    private final List<Material> blacklist;
    private final SignConfig sign;
    private final int locationCache;
    private final boolean spawnCommandEnabled, buyTicketCommandEnabled;
    private final SpawnType spawnType;
    private final ResetDisplayConfig resetDisplay;
    private LocationCache spawn;
    private final SafeTeleportConfig safeTeleport;
    private final int chunkDeletePeriod;
    private final TicketConfig ticket;

    public PluginConfig(String version, double refund, CountdownConfig countdown, String defaultFarmWorld, List<FarmWorldImpl> farmWorlds, List<Material> blacklist, SignConfig sign, int locationCache, boolean spawnCommandEnabled, boolean buyTicketCommandEnabled, SpawnType spawnType, ResetDisplayConfig resetDisplay, LocationCache spawn, SafeTeleportConfig safeTeleport, int chunkDeletePeriod, TicketConfig ticket) {
        this.version = version;
        this.refund = refund;
        this.countdown = countdown;
        this.defaultFarmWorld = defaultFarmWorld;
        this.farmWorlds = farmWorlds;
        this.blacklist = blacklist;
        this.sign = sign;
        this.locationCache = locationCache;
        this.spawnCommandEnabled = spawnCommandEnabled;
        this.buyTicketCommandEnabled = buyTicketCommandEnabled;
        this.spawnType = spawnType;
        this.resetDisplay = resetDisplay;
        this.spawn = spawn;
        this.safeTeleport = safeTeleport;
        this.chunkDeletePeriod = chunkDeletePeriod;
        this.ticket = ticket;
    }

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
                                1800, 43_200, 0,
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
                        },
                        new String[]{
                                "&7[&bFarm World&7]",
                                "&6%{farm_world}",
                                "&4&lDISABLED"
                        }
                ),
                3,
                true, true,
                SpawnType.FIRST,
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
                new SafeTeleportConfig(true, false, 15),
                336,
                new TicketConfig(true, "HH:mm:ss dd.MM.yyyy", "&6&lTeleport Ticket", Material.PAPER, true)
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
        private final String[] linesWhenActive, linesWhenInactive;
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

    public enum SpawnType {
        FORCE, FIRST, NONE
    }

    @AllArgsConstructor
    @Getter
    public static class SafeTeleportConfig {
        private final boolean enabled, canDamagePlayers;
        private final int time;
    }

    @AllArgsConstructor
    @Getter
    public static class TicketConfig {
        private final boolean enabled;
        private final String dateFormat, name;
        private final Material material;
        private final boolean teleportInstantly;
    }
}
