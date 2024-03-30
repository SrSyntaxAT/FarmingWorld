# FarmingWorld
FarmingWorld is a world management plugin for Spigot based servers to manage simplified farm worlds. <br>
You can create and manage multiple farm worlds that will be automatically deleted and create a new world.

---

## Notice
The v2 is not compatible with the v1.
When upgrading, the plugin must be completely deleted beforehand.
Plugins that use the API must be rewritten to the new API version beforehand.

---

## Contents

- [Configuration](#configuration)
- [Permissions](#permissions)
- [Sign](#sign)
- [Template and Spawn](#template-and-spawn)
- [Useful links](#useful-links)
- [License](#license)

---

## Configuration

#### messageType
The position at which the message is to be sent.
<br> CHAT - The message will be sent to the chat normally.
<br> SYSTEM - The message is sent as a system message in the chat and is displayed even if the player has only system messages enabled in the game settings.
<br> ACTION_BAR - The message is displayed above the health and armor bar.

```
{
"version": "2.2",                     # This value should not be changed.
"refund": 1.0,                          # What percentage the user will be refunded 
                                        # if they purchase a teleport to a farm world, 
                                        # but it is cancelled due to movement or other 
                                        # reasons. 0 = 0% 1.0 = 100%
"countdown": {                             
  "time": 5,                            # How long the teleport countdown runs in seconds. 0 = no countdown
  "permittedDistance": 0.7,             # How many blocks the player may move before the countdown is canceled.
  "movementAllowed": false,             # Allow a player to move during the countdown.
  "messageType": "ACTION_BAR"           # Where to display the message. CHAT, SYSTEM, ACTION_BAR
},
"defaultFarmWorld": "FarmWorld",        # The default farm world, which can be reached with /farming.
"farmWorlds": [                         # A list of all farm worlds.
  {                          
    "name": "FarmWorld",                # Farm world name
    "permission": null,                 # Permission needed to teleport to the farm world.
    "cooldown": 1800,                   # How long the player has to wait in seconds before being allowed to teleport again. 
    "timer": 43200,                     # The time in minutes until the farm world is reset.
    "price": 0,                         # Money needed to teleport to the farm world.
    "environment": "NORMAL",            # World type with which the farm world should be created.
                                        # Types: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/World.Environment.html
    "generator": null,                  # Which generator to use to generate the world.
    "border": {                         
      "size": 10000,                    
      "centerX": 0,                     
      "centerZ": 0                      
    },                          
    "active": false,                    # Whether the world is enabled and players can teleport to it.
    "aliases": [                        # Aliases are commands with which players can teleport to the farm world.
      "FarmWorld"                       
    ],
    "templates": [                      # World templates that are loaded during reset.
      "world",
      "world_2"
    ],
    "spawn": {                          # Location at which a player is teleported into the farm world.
      "x": 0.0,                         # If the spawn is zero, the player is teleported to a random location.  
      "y": 0.0,                         # If there is a spawn.json in the farm world, 
      "z": 0.0,                         # the player is teleported to this location.
      "pitch": 0.0,
      "yaw": 0.0
    }
  }
],
"blacklist": ["AIR", "LAVA", "WATER"],  # On which blocks can not be teleported.
"sign": {                               # 
  "daysFormat": "dd.MM.yyyy",           # With what pattern to be used for the signs.
  "hoursFormat": "HH:mm",               # https://www.digitalocean.com/community/tutorials/java-simpledateformat-java-date-format#patterns
  "linesWhenActive": [                  # The lines of the sign when the farm world is active.
    "&7[&bFarm World&7]",               # Placeholder: %{farm_world} = farm world name
    "&6%{farm_world}",                  # %{players}    = Players who are on the farm world.
    "&cReset at:",                      # %{date}       = Reset date
    "&e%{date}"                         # %{remaining}  = Remaining time until reset
  ],
  "linesWhenInactive": [                # The lines of the sign when the farm world is inactive.
    "&7[&bFarm World&7]",               # Placeholder: %{farm_world} = farm world name
    "&6%{farm_world}",                  # %{players}    = Players, who are on the farm world.
    "&4&lDISABLED"                      
  ]
},
"locationCache": 3,                     # How many teleport locations to pre-generate.
"spawnCommandEnabled": true,            # Whether the /spawn command should be enabled.
"spawnType": "FIRST",                   # Types whether the player should be teleported to the spawn when joining.
                                        # FORCE = at every join | FIRTST = at the first join | NONE = will never be teleported
"resetDisplay": {                       # Displayed in the farm world when it is reset.
  "enabled": true,                      # Whether to display the message.
  "type": "BOSS_BAR",                   # Where to display the message. ACTION_BAR & BOSS_BAR
  "barStyle": "SEGMENTED_20",           # At a boss bar how full the progress should be.
                                        # https://hub.spigotmc.org/javadocs/spigot/org/bukkit/boss/BarStyle.html
  "barColor": "BLUE",                   # What color is the boss bar.
                                        # https://hub.spigotmc.org/javadocs/spigot/org/bukkit/boss/BarColor.html
  "changeBossBarProgress": true,        # Whether to match progress with time remaining to reset.
  "message": "&cReset:&e %{date}",      # The message to be displayed.
                                        # Placeholder: %{date} & %{remaining}
  "dateFormat": "HH:mm:ss dd.MM.yyyy"   # Which pattern should be used for the date.
},                                      # https://www.digitalocean.com/community/tutorials/java-simpledateformat-java-date-format#patterns
"spawn": {                              # The fallback or spawn location.
  "world": "world",
  "x": 0.0,
  "y": 78.0,
  "z": 0.0,
  "pitch": 0.0,
  "yaw": 0.0
},
"safeTeleport": {
  "enabled": true,                      # Whether safe teleport should be enabled.
  "canDamagePlayers": false,            # Whether a protected player can harm another player.
  "time": 30                            # The time in seconds that a player is protected.
},
"chunkDeletePeriod": 336                # Time in hours when an unused chunk should be deleted. 0 = deletion disabled
"ticket": {
    "enabled": true,                    # Whether the ticket is activated.
    "dateFormat": "HH:mm dd.MM.yyyy",   # Which time format should be displayed on the ticket.
    "name": "&6&lTeleport Ticket",      # What name the ticket item should have.
    "material": "PAPER",                # List of available materials  https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html
    "teleportInstantly": false          # When using the ticket, should the player be teleported to the farm world immediately? 
  },
  "localRTPfee": true                   # Whether the /rtp command is free.
}
```
---

## Permissions

| Permission                       | Description                                                                                        |
|----------------------------------|----------------------------------------------------------------------------------------------------|
| farmingworld.admin               | Gives the player permission for the /fwa command.                                                  |
| farmingworld.world.*             | Allow the player to teleport in all farm worlds.                                                   |
| farmingworld.world.{farmworld}   | Allows a player to teleport to the farm world if the farm world has a restriction.                 |
| farmingworld.teleport.other      | Allow a player to teleport another player with /farming <player> or /farming <farmworld> <player>. |
| farmingworld.sign                | Is needed to be able to create and destroy a sign.                                                 |
| farmingworld.bypass.*            | Allows the player to teleport without cooldown, countdown, or cost.                                |
| farmingworld.bypass.countdown    | Allows the player to teleport without countdown.                                                   |
| farmingworld.bypass.cooldown     | Allows the player to teleport without cooldown.                                                    |
| farmingworld.bypass.economy      | Allows the player to teleport without cost.                                                        |
| farmingworld.safeteleport        | The player is invulnerable for a certain time after teleporting to a farm world.                   |
| farmworld.buyticket.*            | Allow the player to buy a ticket for all farm worlds.                                              |
| farmworld.buyticket.{farmworld}  | Allow the player to buy a ticket to the farm world.                                                |

---

## Sign

You can create signs so that players are teleported to the farm world when they click on the sign.
To be allowed to create a sign you need the permission farmingworld.sign or farmingworld.admin.

<img src="img/sign_create.png" width="350" alt="example to create a sign">
<img src="img/sign_created.png" width="350" alt="example of a ready created sign">

If you don't want to have the sign anymore, you can simply destroy it in Creative Mode.

---

## Template and Spawn

A template can be used to use existing worlds and associated spawns for a farm world.
If several templates have been specified, one of them will be selected at random.

A farm world or template can contain a spawn.json file that contains the spawn location.
Players are teleported to the spawn and can then use /rtp to teleport to a random location.

### spawn.json
```json
{
  "x": 0,
  "y": 0,
  "z": 0,
  "pitch": 0,
  "yaw": 0
}
```

---

## Useful links

* [Download](https://www.spigotmc.org/resources/farmingworld.100640/)
* [Discord](http://discord.gg/tvEFd4j)
* [Issues](https://github.com/SrSyntaxAT/FarmingWorld/issues)
* [Multiple worlds example](https://gist.github.com/SrSyntaxAT/591d75bc8c80317053cd7b24eb283f52)

---

## License
This project is licensed under the **MIT License**. Read more in the LICENSE file.
