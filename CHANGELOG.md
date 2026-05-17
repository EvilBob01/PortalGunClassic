# Changelog

All notable changes to PortalGunClassic will be documented here.

---

## [Unreleased] - 1.21.1-2.0.0

### Added
- **Multi-color portal guns** — single `portalgun` item with color stored as NBT (0-15, DyeColor ordinals)
- **Dye recoloring** — sneak + right-click with a dye in offhand to recolor the gun
- **Slot A / Slot B** — left-click fires portal A, right-click fires portal B for each color
- **Per-player portal isolation** — portals keyed by `(ownerUUID, colorIndex, slot)`; different players' portals never interact
- **Multiple portal pairs** — each color is an independent pair; one player can have cyan A↔B, red A↔B, green A↔B all active simultaneously
- **Color-tinted rendering** — portal balls, portal blocks, and HUD icons all tinted using DyeColor RGB values; slot B rendered at 65% brightness to distinguish from slot A
- **Reset portals** — sneak + right-click with no dye removes both portals for the held gun's color
- Tooltip on portal gun shows current color, left/right-click instructions, and dye hint

### Fixed
- `DyeColor.getTextureDiffuseColors()` (float[]) removed in 1.21.1; replaced with `getTextureDiffuseColor()` (ARGB int) via `FastColor.ARGB32` helpers
- `ItemStack.hasTag()` / `getTag()` / `getOrCreateTag()` replaced with 1.21.1 Data Components API (`DataComponents.CUSTOM_DATA` + `CustomData.update()`)
- Missing `CompoundTag` import in `ItemPortalGun`

- `ItemPortalGun` redesigned: `isOrange` boolean removed, replaced by `colorIndex` NBT; single registered item replaces `portalgun` + `portalgun_orange`
- `EntityPortalProjectile`: carries `ownerUUID`, `colorIndex`, `slot` instead of `isOrange`
- `PortalInfo`: stores `ownerUUID`, `colorIndex`, `slot` per portal; `makeKey()` / `pairKey()` helpers
- `PortalSavedData`: flat `HashMap<String, PortalInfo>` keyed by `owner:color:slot`; `sendStatusToPlayer()` sends per-color bitmask to owning player only
- `TileEntityPortal`: stores `ownerUUID`, `colorIndex`, `slot`; pair lookup via `PortalSavedData.getPair()`
- `BlockPortal.canPlace()`: checks owner+color+slot instead of `isOrange`
- `PacketPortalStatus`: now carries `UUID` + `Map<colorIndex, bitmask>` instead of two booleans
- `ClientState`: replaced `PortalStatus` object with per-color bitmask map; `getPortalBits(colorIndex)` helper
- `EventHandlerServer`: left-click events (`LeftClickEmpty`, `LeftClickBlock`) fire slot A; right-click (`use()`) fires slot B; sneak+right-click resets or recolors
- `EventHandlerClientGame` HUD: draws slot A and slot B icons tinted with current gun's dye color
- `TileRendererPortal` / `RenderPortalProjectile`: use single base texture tinted at render time

### Removed
- `PacketSwapType` — gun swap is no longer needed (each color is its own independent pair)
- `PortalStatus` client class — replaced by `ClientState` bitmask map
- `portalgun_orange` item registration — single `portalgun` item covers all colors via NBT
- Key bindings for swap/reset — reset is now sneak+right-click on the gun

---

## [1.0.0] - 2026-05-16

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
- `PacketEntityLocation`: split 9-field `StreamCodec.composite()` into two 6-field composites
- `PacketSwapType`: renamed record field `type` → `portalType`
- `EntityPortalProjectile`: removed stale `@Override` on `shouldRender()`
- `PortalSavedData`: updated `save()`/`load()` signatures for 1.21.1
- Removed legacy proxy classes, old model/recipe JSON files, `mcmod.info`
