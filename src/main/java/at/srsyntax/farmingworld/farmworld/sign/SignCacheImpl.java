package at.srsyntax.farmingworld.farmworld.sign;

import at.srsyntax.farmingworld.APIImpl;
import at.srsyntax.farmingworld.FarmingWorldPlugin;
import at.srsyntax.farmingworld.api.farmworld.FarmWorld;
import at.srsyntax.farmingworld.api.farmworld.LocationCache;
import at.srsyntax.farmingworld.api.farmworld.sign.SignCache;
import at.srsyntax.farmingworld.api.message.Message;
import at.srsyntax.farmingworld.config.PluginConfig;
import at.srsyntax.farmingworld.util.TimeUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.block.Sign;

import java.util.HashMap;
import java.util.Map;
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
@Getter
public class SignCacheImpl implements SignCache {

    private final Sign sign;
    private final LocationCache location;
    private final FarmWorld farmWorld;

    @Override
    public void update() {
        final var plugin = ((APIImpl) FarmingWorldPlugin.getApi()).getPlugin();
        final var config = plugin.getPluginConfig();
        final var lines = getLines(config.getSign());

        if (lines == null || lines.length == 0) return;
        final Map<String, String> replaces = createReplaces(plugin, config.getSign());

        for (byte i = 0; i < Math.min(lines.length, 4); i++) {
            final var rawLine = lines[i];
            if (rawLine == null) continue;
            final var line = new Message(rawLine, ChatMessageType.CHAT).replaces(replaces).toString();
            sign.setLine(i, line);
        }
        sign.update();
    }

    private String[] getLines(PluginConfig.SignConfig config) {
        return farmWorld.isActive() ? config.getLinesWhenActive() : config.getLinesWhenInactive();
    }

    private Map<String, String> createReplaces(FarmingWorldPlugin plugin, PluginConfig.SignConfig config) {
        final Map<String, String> replaces = new HashMap<>();

        replaces.put("%{farm_world}", farmWorld.getName());
        final var world = farmWorld.getWorld();
        final var reset = farmWorld.getResetDate();
        replaces.put("%{players}", String.valueOf(world == null ? 0 : world.getPlayers().size()));
        replaces.put("%{date}", TimeUtil.getDate(getDateFormat(config, reset), reset));
        replaces.put("%{remaining}", TimeUtil.getRemainingTime(plugin, reset, true));

        return replaces;
    }

    private String getDateFormat(PluginConfig.SignConfig config, long reset) {
        if (reset - System.currentTimeMillis() < TimeUnit.DAYS.toMillis(1))
            return config.getHoursFormat();
        return config.getDaysFormat();
    }
}
