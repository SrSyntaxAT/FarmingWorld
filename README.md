# FarmingWorld
FarmingWorld is a world management plugin for Spigot based servers to manage simplified farm worlds. <br>
You can create and manage multiple farm worlds that will be automatically deleted and create a new world.

---

The v2 is currently under development. <br>
The new version will include more features, performance improvements and more. <br>
Wishes or other suggestions for improvement can be submitted at any time. <br> <br>
You can find the v1 [here](https://github.com/SrSyntaxAT/FarmingWorld/tree/main).

---

## Contents

- [Configuration](#configuration)
  - [Countdown](#countdown)
- [Useful links](#useful-links)
- [License](#license)

---

## Configuration

### version
This is the version of the configuration. <br>
This allows you to see how up-to-date the current configurations are and should therefore not be changed yourself.

### refund
The percentage value that the player will be refunded upon purchase. <br>
This applies if the player leaves the server under a countdown or moves without permission.

### countdown
Settings that affect the countdown.

#### time
The length of the countdown.

#### permittedDistance
The allowed distance a player may move under a countdown without interrupting the countdown.

#### movementAllowed
Indicates whether the player is allowed to move during a countdown.

#### messageType
The position at which the message is to be sent.
<br> CHAT - The message will be sent to the chat normally.
<br> SYSTEM - The message is sent as a system message in the chat and is displayed even if the player has only system messages enabled in the game settings.
<br> ACTION_BAR - The message is displayed above the health and armor bar.

---

## Useful links

* [Download](https://www.spigotmc.org/resources/farmingworld.100640/)
* [Discord](http://discord.gg/tvEFd4j)
* [Issues](https://github.com/SrSyntaxAT/FarmingWorld/issues)

---

## License
This project is licensed under the **MIT License**. Read more in the LICENSE file.
