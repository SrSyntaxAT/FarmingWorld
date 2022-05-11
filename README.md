# FarmingWorld
FarmingWorld is a world management plugin for Spigot based servers to manage simplified farm worlds.
You can create multiple farm worlds that will be automatically deleted and generate a new world.

---

## Contents

- [Useful links](#useful-links)
- [Commands](#commands)
- [Config](#config)
  - [FarmingWorld](#farmingworld)
  - [Multiple worlds](#multiple-worlds)
- [Cooldown](#cooldown)
- [License](#license)

---

## Useful links

* [Download](https://www.spigotmc.org/resources/farmingworld.100640/)
* [Discord](http://discord.gg/tvEFd4j)
* [Issues](https://github.com/SrSyntaxAT/FarmingWorld/issues)

---

## Commands

| Command              | Aliases      | Permission                                                                                                                                                                                           | Syntax                                                                       | Usage                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   |
|----------------------|:-------------|:-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|:-----------------------------------------------------------------------------|:--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| farming              | configurable | Permissive is specified in the config itself                                                                                                                                                         | /farming [farmworld]                                                         | Teleports a player to a farmworld. If no farmworld is specified, the player is teleported to the default farmworld.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     |
| teleportfarmingworld | tpfw         | farmingworld.teleport.other                                                                                                                                                                          | /tpfw %player% [farmworld]                                                   | With this command the console or a player can teleport another player to a farmworld. If no farmworld is specified, the player is teleported to the default farmworld. This player can only be teleported to the farmworld if he also has this permission to the farmworld. If the command sender has the permission **farmingworld.teleport.other.ignore.check** he can append "**-dtc**" or "**-disabletargetcheck**" to the command, and the player can be teleported even without permission for the farmworld.                                                                                     |
| farmingworldadmin    | fwa, fwadmin | farmingworld.admin.* or<br>farmingworld.admin.reload <br>farmingworld.admin.info <br>farmingworld.admin.delete <br>farmingworld.admin.reset <br>farmingworld.admin.list <br>farmingworld.admin.activ | /fwa <reload/ info / delete / reset / list / enable / disable> [%farmworld%] | An easy way to manage the farming worlds and the plugin ingame. <br>**reload <farmingworld.admin.reload>:** Reload the config of the plugin. This should not delete any farmworld! <br>**info <farmingworld.admin.info>:** Display current information and statistics about the farmworld. <br>**delete <farmingworld.admin.delete>:** Delete a farmworld and its worlds. <br>**reset <farmworld.admin.reset>:** Reset the worlds of the farmworld. <br>**list <farmingworld.admin.list>:** List all farmworlds. <br>**enable/disable <farmingworld.admin.activ>:** Activate or deactivate a farmworld. |


---

## Config

| Key                 | Explain                                                                                                                                                                                                                                               |
|---------------------|:------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| version             | The current version of the Config. **Should not be changed.**                                                                                                                                                                                         |
| fallbackWorld       | The world where the player should be teleported to if there is a problem teleporting the player in a new farmworld.                                                                                                                                   |
| aliases             | Aliases for the "farming" command.                                                                                                                                                                                                                    |
| displayPosition     | Specifies where to display when the farm world is deleted. Possible specifications: **BOSS_BAR, ACTION_BAR, NOT**                                                                                                                                     |
| displayType         | Specifies in which mode the display is updated. <br> **REMAINING** updates the display every minute. <br> **DATE** never updates the display, because it is not necessary. The **dateRefresh** specification can also be used to update this display. |
| dateRefresh         | Can be optionally specified if you want the display to update when displayType has been set to DATE.                                                                                                                                                  |
| barColor            | Specifies the color of the bossbar that can be activated at displayPosition. You can find a list of colors on [Spigot's Java Docs page](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/boss/BarColor.html).                                      |
| dateRefresh         | Specifies the time in seconds at which the DATE display type should be updated.                                                                                                                                                                       |
| spawnBlockBlacklist | A list of blocks that the player should not teleport to.                                                                                                                                                                                              |
| defaultFarmingWorld | Specifies the default farmworld to which you will be teleported if you use the farming command without arguments. If the value is **"null"**, you need to specify a specific farmworld.                                                               |
| farmingWorlds       | These are all current farmworlds. Read more at "FarmingWorld" explanation.                                                                                                                                                                            |
| message             | There you can change all messages that the plugin sends.                                                                                                                                                                                              |

### FarmingWorld

| Key          | Explain                                                                                                                                                                |
|--------------|:-----------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| name         | Name of the world.                                                                                                                                                     |
| permission   | The permission a player needs to teleport to this world. If the value is "null", no permission is needed.                                                              |
| activ        | This can be used to activate or deactivate a farmworld.                                                                                                                |
| timer        | This is the time in minutes when the world should be deleted.                                                                                                          |
| rtpArenaSize | The area in which the player will be teleported. This must be smaller than the World Border.                                                                           |
| borderSize   | The size of the World Border of the world. This feature only works in the Nether and the Overworld. The size of the Border should be larger than that of the RTP area. |
| environment  | The world type with which the world is generated. Available types: **NORMAL, NETHER, THE_END**                                                                         |
| generator    | Generate a farmworld via a world generator                                                                                                                             |
| cooldown     | A cooldown in seconds. More under [Cooldown](#cooldown).                                                                                                               |

**You should not delete a farm world by removing it from the configuration. This will not delete the data in the database or the worlds. Instead, you should use the "farmingworldadmin delete" command as a console or player in the game.** <br><br>

### Multiple worlds
[Here is an example](https://gist.github.com/SrSyntaxAT/591d75bc8c80317053cd7b24eb283f52) of creating multiple worlds in the config.

---

## Cooldown

<p>
To save the server, a cooldown can be specified in seconds.
This limits the number of times a player can teleport to a farmworld.
This cooldown can also be bypassed. To do so, a player needs the permission 'farmingworld.cooldown.bypass.*' to bypass it for every world or 'farmingworld.cooldown.bypass.<farmingworld>' to bypass it for a specific world only.
To teleport another player without cooldown, you need the permission 'farmingworld.cooldown.bypass.other.*' or 'farmingworld.cooldown.bypass.other.<farmingworld>'.
</p>

---

## License
This project is licensed under the **MIT License**. Read more in the LICENSE file.
