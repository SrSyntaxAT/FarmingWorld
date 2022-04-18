# FarmingWorld
FarmingWorld is a world management plugin for Spigot based servers to manage simplified farm worlds.
You can create multiple farm worlds that will be automatically deleted and generate a new world.

---

## Useful links

* [Download](https://www.spigotmc.org/resources/farmingworld.100640/)
* [Discord](http://discord.gg/tvEFd4j)
* [Issues](https://github.com/SrSyntaxAT/FarmingWorld/issues)

---

## Commands

| Command              | Aliases                                 | Permission                                   | Syntax                     | Usage                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               |
|----------------------|:----------------------------------------|:---------------------------------------------|:---------------------------|:--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| farming              | farm, farmworld, farmingworld, farmwelt | Permissive is specified in the config itself | /farming [farmworld]       | Teleports a player to a farmworld. If no farmworld is specified, the player is teleported to the default farmworld.                                                                                                                                                                                                                                                                                                                                                                                                 |
| teleportfarmingworld | fpfw                                    | **farmingworld.teleport.other**              | /tpfw %player% [farmworld] | With this command the console or a player can teleport another player to a farmworld. If no farmworld is specified, the player is teleported to the default farmworld. This player can only be teleported to the farmworld if he also has this permission to the farmworld. If the command sender has the permission **farmingworld.teleport.other.ignore.check** he can append "**-dtc**" or "**-disabletargetcheck**" to the command, and the player can be teleported even without permission for the farmworld. |
| farmingworldinfo     | fwi                                     | **farmingworld.info**                        | /fwi %farmworld%           | Show current information of the farmworld                                                                                                                                                                                                                                                                                                                                                                                                                                                                           |
| farmingworldreset    | fwr                                     | **farmingworld.reset**                       | /fwr %farmworld%           | Reset a farmworld                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   |


---

## Config

| Key                 | Explain                                                                                                                                                                                                                                               |
|---------------------|:------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| fallbackWorld       | The world where the player should be teleported to if there is a problem teleporting the player in a new farmworld.                                                                                                                                   |
| displayPosition     | Is the range from 0 in which a player is teleported to the farm world.                                                                                                                                                                                |
| displayPosition     | Specifies where to display when the farm world is deleted. Possible specifications: **BOSS_BAR, ACTION_BAR, NOT**                                                                                                                                     |
| displayType         | Specifies in which mode the display is updated. <br> **REMAINING** updates the display every minute. <br> **DATE** never updates the display, because it is not necessary. The **dateRefresh** specification can also be used to update this display. |
| barColor            | Specifies the color of the bossbar that can be activated at displayPosition. You can find a list of colors on [Spigot's Java Docs page](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/boss/BarColor.html).                                      |
| dateRefresh         | Specifies the time in seconds at which the DATE display type should be updated.                                                                                                                                                                       |
| spawnBlockBlacklist | A list of blocks that the player should not teleport to.                                                                                                                                                                                              |
| defaultFarmingWorld | Specifies the default farmworld to which you will be teleported if you use the farming command without arguments. If the value is **"null"**, you need to specify a specific farmworld.                                                               |
| farmingWorlds       | These are all current farmworlds. Read more at "FarmingWorld" explanation.                                                                                                                                                                            |
| message             | There you can change all messages that the plugin sends.                                                                                                                                                                                              |

### FarmingWorld

| Key              | Explain                                                                                                                                                                        |
|------------------|:-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| name             | Name of the world.                                                                                                                                                             |
| permission       | The permission a player needs to teleport to this world. If the value is "null", no permission is needed.                                                                      |
| currentWorldName | The current world name. You should never change this value, otherwise the current world can no longer be found.                                                                |
| nextWorldName    | The next world to which a player will be teleported when the current world is deleted. You should never change this value, otherwise the current world can no longer be found. |
| created          | This is the time when the current world was created. This value should never be changed.                                                                                       |
| timer            | This is the time in minutes when the world should be deleted.                                                                                                                  |
| rtpArenaSize     | The area in which the player will be teleported. This must be smaller than the World Border.                                                                                   |
| borderSize       | The size of the World Border of the world. This feature only works in the Nether and the Overworld. The size of the Border should be larger than that of the RTP area.         |
| environment      | The world type with which the world is generated. Available types: **NORMAL, NETHER, THE_END**                                                                                 |
| generator        | Generate a farmworld via a world generator                                                                                                                                     |

**When you add a new farm world, you should not specify created, currentWorldName and nextWorldName.** <br>
[Here is an example](https://gist.github.com/SrSyntaxAT/591d75bc8c80317053cd7b24eb283f52) of creating multiple worlds in the config.

---

## License
This project is licensed under the **MIT License**. Read more in the LICENSE file.
