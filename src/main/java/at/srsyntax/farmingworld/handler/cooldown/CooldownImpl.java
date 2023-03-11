package at.srsyntax.farmingworld.handler.cooldown;

import at.srsyntax.farmingworld.FarmingWorldPlugin;
import at.srsyntax.farmingworld.api.farmworld.FarmWorld;
import at.srsyntax.farmingworld.api.handler.HandleException;
import at.srsyntax.farmingworld.api.handler.cooldown.Cooldown;
import at.srsyntax.farmingworld.api.handler.cooldown.CooldownException;
import at.srsyntax.farmingworld.config.MessageConfig;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

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
public class CooldownImpl implements Cooldown {

    private static final String METADATA_KEY = "fwcd";

    private final FarmingWorldPlugin plugin;
    private final MessageConfig.CooldownMessages messages;

    private final Player player;
    private final FarmWorld farmWorld;

    public CooldownImpl(FarmingWorldPlugin plugin, MessageConfig.CooldownMessages messages, Player player, FarmWorld farmWorld) {
        this.plugin = plugin;
        this.messages = messages;
        this.player = player;
        this.farmWorld = farmWorld;
    }

    @Override
    public boolean canBypass() {
        return player.hasPermission("farmingworld.bypass.*") || player.hasPermission("farmingworld.bypass.cooldown");
    }

    @Override
    public void handle() throws HandleException {
        if (canBypass()) return;
        if (hasCooldown()) throw new CooldownException(messages.getHasCooldown(), this);
        final long end = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(farmWorld.getCooldown());
        player.setMetadata(METADATA_KEY, new FixedMetadataValue(plugin, end));
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public boolean hasCooldown() {
        if (getRemaining() > 0) return true;
        remove();
        return false;
    }

    @Override
    public void remove() {
        if (!player.hasMetadata(METADATA_KEY)) return;
        player.removeMetadata(METADATA_KEY, plugin);
    }

    @Override
    public long getRemaining() {
        final long end = getEnd();
        return end <= System.currentTimeMillis() ? 0 : end - System.currentTimeMillis();
    }

    @Override
    public long getEnd() {
        if (!player.hasMetadata(METADATA_KEY)) return 0L;
        return player.getMetadata(METADATA_KEY).get(0).asLong();
    }
}
