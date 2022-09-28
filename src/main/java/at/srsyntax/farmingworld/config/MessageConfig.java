package at.srsyntax.farmingworld.config;

import lombok.AllArgsConstructor;
import lombok.Getter;

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
@Getter
public class MessageConfig {

  private final String farmingWorldList;
  private final String noPermission, worldNotFound, usage;
  private final String targetNotFound, targetNoPermission, targetTeleported;
  private final String reset, delete, reload;
  private final String remaining;

  private final String second, seconds;
  private final String minute, minutes;
  private final String hour, hours;
  private final String day, days;
  
  private final String noWorlds, dateFormat;

  private final String worldDeleted, nothingToConfirm, confirmExpired, confirm;
  private final String disabled, enabled, alreadyEnabled, alreadyDisabled, isDisabled;

  private final String cooldownError, cooldownOtherError;
  private final String countdown, countdownActivError, countdownCanceledMoved;

  private final String spawnDisabledError, onlyPlayers, spawnTeleported;
  private final String spawnDisabled, spawnEnabled, spawnSet;

  public MessageConfig() {
    this(
        "&eFarmingWorlds&8: <list>",
        "&cYou have no rights to do that!",
        "&cFarming world not found!",
        "&cUsage&8:&f /<usage>",
        "&cPlayer not found!",
        "&cThe player does not have the rights to be teleported to the farmworld!",
        "&e<player> &awas teleported to farmworld &e<farmingworld>&a.",
        "&4The world is reset.",
        "&cThe farmworld was deleted",
        "&cThe plugin has been reloaded",
        "&4Reset in &e<remaining>",
        "second", "seconds",
        "minute", "minutes",
        "hour", "hours",
        "day", "days",
        "&cNo worlds found!",
        "dd.MM.yyyy - HH:mm:ss",
        "&aFarming world has been reset.",
        "&cYou didn't want to reset a world, so you can't confirm anything.",
        "&cThe time to confirm has expired.",
        "&fConfirm your intention in the next &a10 seconds&f with the command \"&a/fwa confirm&f\".",
        "&cFarmingworld was disabled!",
        "&aFarmingworld was enabled!",
        "&cThe farmingworld is already enabled!",
        "&cThe farmingworld is already disabled!",
        "&cYou cannot teleport to this farmworld because it has been disabled.",
        "&cYou may not teleport to this farmworld until <date>.",
        "&cThis player will not be allowed to teleport to this farmworld until <date>.",
        "&cYou will be teleported in &e<time> &cseconds.",
        "&cYou are already teleported to a farmworld!",
        "&cThe countdown was canceled because you moved.",
        "&cSpawn command is currently disabled.",
        "&cOnly players can use this command.",
        "&aYou have been teleported to the spawn.",
        "&cThe spawn has been disabled.",
        "&aThe spawn has been enabled.",
        "&aThe spawn has been set."
    );
  }
}
