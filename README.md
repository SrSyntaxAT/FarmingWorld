# FarmingWorld
FarmingWorld is a world management plugin for Spigot based servers to manage simplified farm worlds.
You can create multiple farm worlds that will be automatically deleted and generate a new world.

---

## Useful links

* [Download](https://www.spigotmc.org/resources/farmingworld.100640/)
* [Discord](http://discord.gg/tvEFd4j)
* [Issues](https://github.com/SrSyntaxAT/FarmingWorld/issues)

---

## Config

| Key                 | Explain                                                                                                                                                                                                                                               |
|---------------------|:------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| fallback            | The coordinates where the player should be teleported to if there is a problem teleporting the player in a new farm world.                                                                                                                            |
| displayPosition     | Is the range from 0 in which a player is teleported to the farm world.                                                                                                                                                                                |
| displayPosition     | Specifies where to display when the farm world is deleted. Possible specifications: **BOSS_BAR, ACTION_BAR, NOT**                                                                                                                                     |
| displayType         | Specifies in which mode the display is updated. <br> **REMAINING** updates the display every minute. <br> **DATE** never updates the display, because it is not necessary. The **dateRefresh** specification can also be used to update this display. |
| barColor            | Specifies the color of the bossbar that can be activated at displayPosition. You can find a list of colors on [Spigot's Java Docs page](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/boss/BarColor.html).                                      |
| dateRefresh         | Specifies the time in seconds at which the DATE display type should be updated.                                                                                                                                                                       |
| defaultFarmingWorld | Specifies the default farm world to which you will be teleported if you use the farming command without arguments. If the value is **"null"**, you need to specify a specific farm world.                                                             |
| farmingWorlds       | These are all current farm worlds. Read more at "Farming World" explanation.                                                                                                                                                                          |
| message             | There you can change all messages that the plugin sends.                                                                                                                                                                                              |

### Farming World

| Key              | Explain                                                                                                                                                                        |
|------------------|:-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| name             | Name of the world.                                                                                                                                                             |
| permission       | The permission a player needs to teleport to this world. If the value is "null", no permission is needed.                                                                      |
| currentWorldName | The current world name. You should never change this value, otherwise the current world can no longer be found.                                                                |
| nextWorldName    | The next world to which a player will be teleported when the current world is deleted. You should never change this value, otherwise the current world can no longer be found. |
| created          | This is the time when the current world was created. This value should never be changed.                                                                                       |
| timer            | This is the time in minutes when the world should be deleted.                                                                                                                  |
| environment      | The world type with which the world is generated. Available types: **NORMAL, NETHER, THE_END**                                                                                     |

**When you add a new farm world, you should not specify created, currentWorldName and nextWorldName.**

---

## License
This project is licensed under the **MIT License**. Read more in the LICENSE file.