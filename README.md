# Do a Barrel Roll for NeoForge 26.3 (unofficial port)

> **This is not the original mod.** It is an unofficial, native NeoForge port of
> [Do a Barrel Roll](https://modrinth.com/mod/do-a-barrel-roll) by **enjarai**, maintained by
> [rossxrio](https://github.com/rossxrio). Please report bugs in this port
> [here](https://github.com/rossxrio/do-a-barrel-roll-neoforgeport/issues), not to enjarai.
> The original project lives at [codeberg.org/enjarai/do-a-barrel-roll](https://codeberg.org/enjarai/do-a-barrel-roll).

<img style="text-align:center" src="https://raw.githubusercontent.com/enjarai/do-a-barrel-roll/stonecutter/1.21.11/img/banner.png">

[![Buy Me a Coffee](https://img.shields.io/badge/Buy%20Me%20a%20Coffee-support%20the%20port-FFDD00?logo=buymeacoffee&logoColor=black)](https://buymeacoffee.com/rossxrio)

Do a Barrel Roll is a lightweight, fully clientside mod that changes
elytra flight to be more fun and semi-realistic.
It achieves this by redesigning movement with a
completely unlocked camera orientation in mind,
allowing for full pitch, yaw and roll control in flight.

Some subtle and less subtle modifiers are also applied to the camera,
including but not limited to smoothing and banking.

![](https://raw.githubusercontent.com/enjarai/do-a-barrel-roll/stonecutter/1.21.11/img/ravine.gif)

## This branch: NeoForge 26.3

This branch (`neoforge/26.3`) is a native NeoForge build of Do a Barrel Roll 3.8.3 for Minecraft 26.3.
It does **not** need Forgified Fabric API, Sinytra Connector, Cicada or any other Fabric compatibility layer.
A NeoForge 1.21.1 build lives on the `neoforge/1.21.1` branch.

### Requirements

| | Version |
|---|---|
| Minecraft | 26.3 |
| NeoForge | 26.3.0.102 or newer (26.3.x) |
| Java | 25 (the version Minecraft 26.3 ships with) |
| [YACL](https://modrinth.com/mod/yacl) | **Optional**, 3.9.7+26.3-neoforge, only for the in-game config screen |

Without YACL the mod works normally: your settings are read from `config/do_a_barrel_roll-client.json`
and the server settings from `config/do_a_barrel_roll-server.json`. Opening the config screen
(mods list or the "Open Config" key) shows a prompt to install YACL instead of crashing.

### Install

1. Install Minecraft 26.3 with the NeoForge installer for 26.3.0.102 or newer
   ([neoforged.net](https://neoforged.net/)).
2. Download `do_a_barrel_roll-neoforge-3.8.3+26.3.jar` from the
   [releases page](https://github.com/rossxrio/do-a-barrel-roll-neoforgeport/releases).
3. Put the jar in the `mods` folder of your game directory (and of your server, if you want the
   server-side features below).
4. Optional: put YACL for NeoForge 26.3 in `mods` as well to get the in-game config screen.
5. Start the game.

### Building from source

```sh
./gradlew build
```

The mod jar is written to `build/libs/do_a_barrel_roll-neoforge-3.8.3+26.3.jar`. Gradle downloads the
Java 25 toolchain automatically if it isn't installed. `./gradlew runClient` and `./gradlew runServer`
start a development client or server (`-PnoYacl` runs the client without YACL).

### Differences from the Fabric version

- The Fabric-only integrations (Mod Menu, Controlify, Cicada) are not included. The config screen
  is available from NeoForge's mods list instead.
- Flight keybinds use a NeoForge key conflict context: they only react while you are flying and
  are not shown as conflicting with the movement keys they share by default.
- Server permissions use NeoForge permission nodes (see [For server admins](#for-server-admins)).

## Controls

The default controls are as follows, but can be modified:

- Mouse x axis to roll
- Mouse y axis to pitch
- strafe keys (normally A and D) to yaw

## Configuration

The mod can be configured in-game with [YACL](https://modrinth.com/mod/yacl) installed:
open the NeoForge mods list, select Do a Barrel Roll and press "Config",
or bind the "Open Config" key in the controls menu.

A wide range of options are available, including custom mouse behavior, elytra activation restrictions and
changing values of modifiers like banking, sensitivity and more.

## Server-side features

Visual aspects of the mod (playermodel roll in particular) can be synced between
clients by installing it on the server-side as well.
Everything is still fully compatible with both vanilla clients and servers.
All the following configurations are valid:

- Server with mod, client with mod: visuals are synced
- Server with mod, client without mod: client can join and play without issues, but can't see visuals
- Server without mod, client with mod: client can join and play without issues, but can only see their own visuals
- Server with mod, client 1 with mod, client 2 with mod, client 3 without mod: client 1 and 2 can see each other's visuals, client 3 cannot
- Server without mod, client 1 with mod, client 2 with mod, client 3 without mod: client 1 and 2 can only see their own visuals

![](https://raw.githubusercontent.com/enjarai/do-a-barrel-roll/stonecutter/1.21.11/img/do-a-barrel-roll.gif)

## Disclaimers

This mod does not actually modify the flight physics themselves,
so in most cases it shouldn't trigger anticheat.
However, doing things like loop de loops looks to the server like rapid camera movement
and may get you flagged.
**Use at your own risk.**

## For server admins

### Thrusting

The mod includes a "thrusting" feature that is probably considered cheating by most servers,
as it lets users accelerate without using fireworks.

**This feature is disabled by default on servers.**

If you want to allow players to use this feature on your server, install the mod on the server and,
while in the world, use the "Server" tab on the config screen to configure it (recommended).

- This tab is only available to level 3 server operators and players with the `do_a_barrel_roll.configure` permission.
- You can also set values manually in `config/do_a_barrel_roll-server.json`
  to configure the mod without a client.

On platforms without the mod, a custom plugin or mod can send a packet to the client at login on
the `do_a_barrel_roll:handshake` channel with the following layout:

| Type    | Value                      |
|---------|----------------------------|
| integer | `1`                        |
| string  | `{"allowThrusting": true}` |

Any client that has DABR installed will respond on the same channel with a packet like this:

| Type    | Value              |
|---------|--------------------|
| integer | [protocol version] |
| boolean | [success]          |

### Other features

The server config includes a few other features to configure server-side behaviour, including:

- `forceEnabled`: Forces the mod to be enabled for all players, regardless of their client configuration.
- `forceInstalled`: Rejects any player trying to join without having the mod installed on their client.
- `installedTimeout`: The amount of time (in ticks) to wait for a client to respond to the `do_a_barrel_roll:config_sync` packet.
  - If the client does not respond in time, and `forceInstalled` is set to `true`, they will be kicked from the server.
  - You may want to increase this value if players with bad connections are getting kicked despite having the mod installed.

These options can all be configured via the same methods as the `allowThrusting` option.

### Permissions

Players can bypass the configured restrictions with level 2 operator status or the
`do_a_barrel_roll.ignore_config` permission, and edit the server config with level 3 operator status or the
`do_a_barrel_roll.configure` permission. Both are registered as NeoForge permission nodes, so permission
mods that hook into NeoForge's permission API (for example LuckPerms) can grant them; without one, the
operator levels above apply.

## Credits

- **[enjarai](https://github.com/enjarai)**: original author of Do a Barrel Roll. All the flight
  mechanics, rendering and design are theirs. Support the original:
  [Ko-fi](https://ko-fi.com/enjarai), [Discord](https://discord.gg/WcYsDDQtyR).
- Based on the [Cool Elytra Roll](https://github.com/Jorbon/cool_elytra) mod by [Jorbon](https://github.com/Jorbon),
  specifically its "realistic mode".
- Originally ported to Forge by [MehVahdJukaar](https://github.com/MehVahdJukaar).
- Mod icon by Mizeno.
- NeoForge 26.3 port by [rossxrio](https://github.com/rossxrio), based on enjarai's upstream 26.3 source.

## Support

If this port is useful to you, you can support its maintenance here:

[![Buy Me a Coffee](https://img.shields.io/badge/Buy%20Me%20a%20Coffee-rossxrio-FFDD00?logo=buymeacoffee&logoColor=black)](https://buymeacoffee.com/rossxrio)

https://buymeacoffee.com/rossxrio

## License

GPL-3.0, like the original mod. See [LICENSE](LICENSE).
