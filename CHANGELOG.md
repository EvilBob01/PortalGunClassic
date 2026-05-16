# Changelog

All notable changes to PortalGunClassic will be documented here.

---

## [Unreleased] - 1.21.1-1.0.0

### Added
- Initial port to NeoForge 1.21.1 (NeoForge 21.1.172)
- `ClientState` class for client-side key mapping and teleport state
- `EventHandlerClientGame` for client tick and GUI rendering
- `EventHandlerCommon` for payload registration on MOD bus
- `ModRegistries` for centralized deferred registration
- NeoForge `neoforge.mods.toml` mod metadata
- Updated recipe data (`data/portalgunclassic/recipe/`)
- Updated lang file to `en_us.json` format

### Changed
- Ported from legacy Forge to NeoForge 21.1.x API
- `RegisterPayloadsEvent` → `RegisterPayloadHandlersEvent` + `PayloadRegistrar`
- `RegisterBlockEntityRenderers` event merged into `RegisterRenderers`
- `PacketEntityLocation`: split 9-field `StreamCodec.composite()` into two
  6-field composites via `StreamCodec.of()` (NeoForge max is 6 fields)
- `PacketSwapType`: renamed record field `type` → `portalType` to avoid clash
  with `CustomPacketPayload.type()` interface method
- `EntityPortalProjectile`: removed stale `@Override` on `shouldRender()`
- `PortalSavedData`: updated `save()`/`load()` signatures to include
  `HolderLookup.Provider` parameter (1.21.1 requirement)
- `PortalSavedData`: explicit `Factory<PortalSavedData>` type parameter
- Removed stale `ClientGamePacketReceivedEvent` and `RegisterPayloadsEvent`
  imports from client event handlers
- Removed legacy `ProxyClient`, `ProxyCommon`, and old model/recipe JSON files

### Removed
- `mcmod.info` (replaced by `neoforge.mods.toml`)
- Legacy item model JSONs (`pg_blue.json`, `pg_orange.json`, `pg_core.json`)
- Legacy recipe JSONs (`portal_core.json`, `portalgun.json` in old location)
- `en_us.lang` (replaced by `en_us.json`)
