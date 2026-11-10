# Do a Barrel Roll for NeoForge 1.20.1 (unofficial port)

> **This is not the original mod.** It is an unofficial, native port of
> [Do a Barrel Roll](https://github.com/enjarai/do-a-barrel-roll) **3.8.3** by **enjarai**
> to **NeoForge for Minecraft 1.20.1**, maintained by rossxrio.
> Please report problems with this port [here](https://github.com/rossxrio/do-a-barrel-roll-neoforgeport/issues),
> not to the original author.
>
> If this port is useful to you, you can support the maintainer on
> [Buy Me a Coffee](https://buymeacoffee.com/rossxrio):
>
> [![Buy Me a Coffee](https://img.shields.io/badge/Buy%20Me%20a%20Coffee-rossxrio-FFDD00?logo=buymeacoffee&logoColor=black)](https://buymeacoffee.com/rossxrio)

<img style="text-align:center" src="https://raw.githubusercontent.com/enjarai/do-a-barrel-roll/stonecutter/1.21.11/img/banner.png">

Do a Barrel Roll is a lightweight, fully clientside mod that changes
elytra flight to be more fun and semi-realistic.
It achieves this by redesigning movement with a
completely unlocked camera orientation in mind,
allowing for full pitch, yaw and roll control in flight.

Some subtle and less subtle modifiers are also applied to the camera,
including but not limited to smoothing and banking.

![](https://raw.githubusercontent.com/enjarai/do-a-barrel-roll/stonecutter/1.21.11/img/ravine.gif)

## All versions of this port

Each Minecraft version lives on its own branch with its own release:

| Minecraft | NeoForge | Java | Branch | Download |
|---|---|---|---|---|
| 26.3 | 26.3.0.102 or newer | 25 | [`neoforge/26.3`](https://github.com/rossxrio/do-a-barrel-roll-neoforgeport/tree/neoforge/26.3) | [3.8.3+26.3](https://github.com/rossxrio/do-a-barrel-roll-neoforgeport/releases/tag/v3.8.3%2B26.3-neoforge) |
| 1.21.1 | 21.1.x | 21 | [`neoforge/1.21.1`](https://github.com/rossxrio/do-a-barrel-roll-neoforgeport/tree/neoforge/1.21.1) | [3.8.3+1.21.1](https://github.com/rossxrio/do-a-barrel-roll-neoforgeport/releases/tag/v3.8.3%2B1.21.1-neoforge) |
| **1.20.1** (this branch) | 47.1.x | 17 | [`neoforge/1.20.1`](https://github.com/rossxrio/do-a-barrel-roll-neoforgeport/tree/neoforge/1.20.1) | [3.8.3+1.20.1](https://github.com/rossxrio/do-a-barrel-roll-neoforgeport/releases/tag/v3.8.3%2B1.20.1-neoforge) |

## Requirements

| Requirement | Version |
|---|---|
| Minecraft | 1.20.1 |
| [NeoForge](https://neoforged.net/) | 47.1.x (the 1.20.1 NeoForge, which still uses the `forge` mod id) |
| Java | 17 or newer |
| [YACL (Yet Another Config Lib)](https://modrinth.com/mod/yacl/versions?g=1.20.1&l=forge) | Forge 1.20.1 build, 3.6.6 recommended (optional, only needed for the in-game config screen) |

No other library is needed. MixinExtras is bundled inside the jar.

## Installation

1. Install NeoForge 47.1.x for Minecraft 1.20.1 using the
   [NeoForge installer](https://maven.neoforged.net/releases/net/neoforged/forge/) (`forge-1.20.1-47.1.x-installer.jar`).
2. Download `do_a_barrel_roll-neoforge-3.8.3+1.20.1.jar` from the
   [releases page](https://github.com/rossxrio/do-a-barrel-roll-neoforgeport/releases) and put it in your `mods` folder.
3. Optional: put the Forge 1.20.1 build of [YACL](https://modrinth.com/mod/yacl/versions?g=1.20.1&l=forge)
   in the `mods` folder too, so you can open the config screen.
4. Optional, for servers: put the same jar in the server's `mods` folder to enable the server-side features below.
   YACL is not needed on the server.

Do not install this together with the official Forge build of Do a Barrel Roll, they share the same mod id.

## Controls

The default controls are as follows, but can be modified:

- Mouse x axis to roll
- Mouse y axis to pitch
- strafe keys (normally A and D) to yaw

The flight keys only respond while you are flying, so they don't show as conflicting with the vanilla movement keys
they share defaults with.

## Configuration

The mod can be configured in-game with [YACL](https://modrinth.com/mod/yacl/versions?g=1.20.1&l=forge) installed.
Open the mods list, select Do a Barrel Roll and press "Config", or bind the "Open config" key.

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

The server and client must both run this NeoForge 1.20.1 port for syncing to work.
It does not talk to Fabric servers or clients, or to the official Forge 1.20.1 builds of the mod.

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

To allow players to use it on your server, install the mod on the server and, while in the world,
use the "Server" tab on the config screen to configure it (recommended).
- This tab is only available to level 3 server operators and players with the `do_a_barrel_roll.configure` permission.
- You can also set values manually in `config/do_a_barrel_roll-server.json` to configure the mod without a client.
  The file starts out as `{}` (every option at its default); add the keys below to change them, for example
  `{"allowThrusting": true, "kineticDamage": "HIGH_SPEED"}`, then restart the server.

### Other features

The server config includes a few other features to configure server-side behaviour, including:

- `allowThrusting`: Allows players to use the thrusting feature.
- `forceEnabled`: Forces the mod to be enabled for all players, regardless of their client configuration.
- `forceInstalled`: Rejects any player trying to join without having the mod installed on their client.
- `installedTimeout`: The amount of time (in ticks) to wait for a client to respond to the config sync packet.
  - If the client does not respond in time, and `forceInstalled` is set to `true`, they will be kicked from the server.
  - You may want to increase this value if players with bad connections are getting kicked despite having the mod installed.
- `kineticDamage`: How much damage flying into a wall does. One of `VANILLA`, `HIGH_SPEED`, `NONE` or `INSTANT_KILL`.

### Permissions

The mod registers two NeoForge permission nodes, so permission mods that hook into the NeoForge/Forge permission API
can grant them:

- `do_a_barrel_roll.configure` (defaults to operator level 3): can edit the server config from the in-game config screen.
- `do_a_barrel_roll.ignore_config` (defaults to operator level 2): bypasses `allowThrusting` and `forceEnabled`,
  so the player can always use thrusting and turn the mod off.

## Credits

All credit for the mod itself goes to **[enjarai](https://github.com/enjarai)**, the original author of
[Do a Barrel Roll](https://github.com/enjarai/do-a-barrel-roll). Please support the original mod:

[![Modrinth](https://img.shields.io/modrinth/dt/do-a-barrel-roll)](https://modrinth.com/mod/do-a-barrel-roll)
[![CurseForge](https://cf.way2muchnoise.eu/full_663658_downloads.svg)](https://curseforge.com/minecraft/mc-mods/do-a-barrel-roll)
[<img alt="kofi-singular" height="40" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/donate/kofi-singular_vector.svg">](https://ko-fi.com/enjarai)

Based on the [Cool Elytra Roll](https://github.com/Jorbon/cool_elytra) mod by [Jorbon](https://github.com/Jorbon),
specifically its "realistic mode".

Originally ported to Forge by [MehVahdJukaar](https://github.com/MehVahdJukaar).

Mod icon by Mizeno.

NeoForge 1.20.1 port by [rossxrio](https://github.com/rossxrio).

## Building

```sh
./gradlew build
```

Gradle must run on Java 21 (Architectury Loom requires it); the mod itself is compiled for Java 17 through a Gradle
toolchain. The jar ends up in `build/libs/do_a_barrel_roll-neoforge-3.8.3+1.20.1.jar`.

## License

GPL-3.0, like the original mod. See [LICENSE](LICENSE).
