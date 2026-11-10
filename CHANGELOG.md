# Changelog

## 3.8.3+26.3 (NeoForge)

Native NeoForge port of Do a Barrel Roll 3.8.3 for Minecraft 26.3.

- Ported to Minecraft 26.3 / NeoForge 26.3.0.102+ (Java 25), built with NeoForge ModDevGradle
  and Mojang mappings. The shared code is based on enjarai's upstream `stonecutter/26.3` source,
  with the NeoForge-specific parts of the 1.21.1 port carried over.
- HUD widgets (horizon line, momentum crosshair, Star Fox overlay) ported to the retained-mode GUI.
  The colour-inverting widget pipeline is registered through `RegisterRenderPipelinesEvent` under its
  own id instead of replacing the vanilla crosshair pipeline.
- Player model roll is driven from the entity render state; camera roll is applied through
  NeoForge's camera roll, and the roll no longer flips sign when leaving a roll in mirrored
  third-person view.
- Networking uses NeoForge payload registration (optional channels, so vanilla clients and servers
  still work). The clientbound roll sync packet is `do_a_barrel_roll:roll_sync_s2c`.
- Flight keybinds use a NeoForge key conflict context and the key category is registered through
  `RegisterKeyMappingsEvent`. The category now has a proper translated name.
- Permission checks use the NeoForge permission nodes `do_a_barrel_roll.ignore_config` and
  `do_a_barrel_roll.configure`, falling back to the 26.x operator levels.
- YACL is optional: without it the mod loads and the config file still works, and the config screen
  shows an "install YACL" prompt.
- Mod Menu, Controlify and Cicada integrations from the Fabric version are not included.

## 3.8.3+1.21.1 (NeoForge)

- Native NeoForge 1.21.1 port of 3.8.3. Forgified Fabric API is no longer required.
- Fixed thrust speed calculation ignoring player and thrust direction. (Thanks @Octol1ttle)
- Fixed NeoForge permission nodes being ignored when resolving `ignore_config` / `configure`.
