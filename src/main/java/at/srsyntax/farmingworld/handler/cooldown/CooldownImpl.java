package at.srsyntax.farmingworld.handler.cooldown;

import at.srsyntax.farmingworld.FarmingWorldPlugin;
import at.srsyntax.farmingworld.api.farmworld.FarmWorld;
import at.srsyntax.farmingworld.api.handler.HandleException;
import at.srsyntax.farmingworld.api.handler.cooldown.Cooldown;
import at.srsyntax.farmingworld.api.handler.cooldown.CooldownException;
import at.srsyntax.farmingworld.config.MessageConfig;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import java.util.concurrent.TimeUnit;

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
        if (!player.hasMetadata(METADATA_KEY)) return false;
        final MetadataValue value = player.getMetadata(METADATA_KEY).get(0);
        if (value.asLong() >= System.currentTimeMillis()) return true;
        remove();
        return false;
    }

    @Override
    public void remove() {
        if (!player.hasMetadata(METADATA_KEY)) return;
        player.removeMetadata(METADATA_KEY, plugin);
    }
}
