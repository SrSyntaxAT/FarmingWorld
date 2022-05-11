package at.srsyntax.farmingworld.util;

import at.srsyntax.farmingworld.FarmingWorldPlugin;
import at.srsyntax.farmingworld.api.CooldownHandler;
import at.srsyntax.farmingworld.api.FarmingWorld;
import at.srsyntax.farmingworld.database.Database;
import at.srsyntax.farmingworld.database.data.CooldownData;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

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
public class CooldownHandlerImpl implements CooldownHandler {

  private final FarmingWorldPlugin plugin;

  private final Player player;
  private final FarmingWorld farmingWorld;

  private final Database database;

  public CooldownHandlerImpl(FarmingWorldPlugin plugin, Player player, FarmingWorld farmingWorld) {
    this.plugin = plugin;
    this.player = player;
    this.farmingWorld = farmingWorld;
    this.database = plugin.getDatabase();
  }

  @Override
  public boolean hasCooldown() {
    if (farmingWorld.getCooldown() <= 0) return false;
    if (player.hasPermission("farmingworld.cooldown.bypass.*")
        || player.hasPermission("farmingworld.cooldown.bypass." + farmingWorld.getName()))
      return false;

    final CooldownData data = getCooldownData();
    if (data == null) return false;

    if (data.hasCooldown()) return true;
    removeCooldown();
    return false;
  }

  @Override
  public CooldownData getCooldownData() {
    if (!player.hasMetadata(getKey())) return null;;
    return (CooldownData) player.getMetadata(getKey()).get(0).value();
  }

  @Override
  public void addCooldown() {
    final long end = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(farmingWorld.getCooldown());
    final CooldownData data = new CooldownData(farmingWorld.getName(), end);
    player.setMetadata(getKey(), new FixedMetadataValue(plugin, data));
    updateDatabase();
  }

  @Override
  public void removeCooldown() {
    if (!player.hasMetadata(getKey())) return;
    player.removeMetadata(getKey(), plugin);
    updateDatabase();
  }

  @Override
  public FarmingWorld getFarmingWorld() {
    return farmingWorld;
  }

  @Override
  public Player getPlayer() {
    return player;
  }

  private void updateDatabase() {
    try {
      database.updateCooldown(player);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  private String getKey() {
    return CooldownData.METADATA_KEY + farmingWorld.getName();
  }
}
