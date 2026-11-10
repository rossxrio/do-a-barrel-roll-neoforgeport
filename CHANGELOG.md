## 3.8.3+1.20.1 (NeoForge 1.20.1 port)

- Native NeoForge 1.20.1 (47.1.x) port of Do a Barrel Roll 3.8.3, based on the NeoForge 1.21.1 port of this
  repository, with the version-specific code taken from upstream's 1.20.1 sources.
- Networking now uses a single optional Forge `SimpleChannel` (`do_a_barrel_roll:main`) with `FriendlyByteBuf`
  encoding, since 1.20.1 has no `CustomPacketPayload`. Handshake, config sync, server config updates and roll sync
  work as in 3.8.3 (protocol version 4). Vanilla clients and servers are still accepted.
- Uses Forge's camera angle event for the world roll and a Forge GUI overlay for the Star Fox easter egg, because
  Forge replaces the vanilla HUD render method.
- MixinExtras is bundled as a jar-in-jar, because Forge 47 does not ship it.
- Fixed the kinetic damage server option doing nothing on Forge: Forge's patch to `LivingEntity#travel` shifts the
  local variable the option modifies, so the injection silently failed (this also affected upstream's Forge 1.20.1 builds).
- Fixed the `do_a_barrel_roll.ignore_config` permission (and op level 2) not bypassing the server's `allowThrusting`
  and `forceEnabled` settings, as documented in the README.
- Carried over from the NeoForge 1.21.1 port:
  - Fixed thrust speed calculation ignoring player and thrust direction. (Thanks @Octol1ttle)
  - Fixed NeoForge permission nodes being ignored when resolving `ignore_config` / `configure`.
  - Flight keys use a native key conflict context, so they only respond while flying and don't show as
    conflicting with the vanilla movement keys.
