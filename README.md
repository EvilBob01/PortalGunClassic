# PortalGunClassic

A multi-color Portal Gun mod for Minecraft 1.21.1 on NeoForge.

Shoot portals into walls, floors, and ceilings — step through one and emerge from the other. Each gun supports two independent portal endpoints (A and B), is dyeable to any of Minecraft's 16 colors, and is fully isolated per player so multiple players on a server never interfere with each other's portals.

---

## Features

- **Single Portal Gun item** — color stored as NBT, defaults to cyan
- **Left-click fires portal A, right-click fires portal B** — each gun manages its own linked pair
- **16 dye colors** — sneak + right-click with a dye in your offhand to recolor
- **Multiple portal pairs per player** — one independent A↔B pair per color; carry a cyan gun and a red gun and have two active pairs simultaneously
- **Per-player isolation** — portals are keyed by `(playerUUID, colorIndex, slot)`; different players' portals never interact
- **Color-tinted rendering** — portal balls, portal blocks, and HUD icons all tinted with the gun's dye color at render time; slot B rendered at 65% brightness to distinguish from slot A
- **Reset portals** — sneak + right-click with no dye removes both portals for that color
- **Tooltips** — gun shows current color and control hints in the item tooltip
- **Persistence** — portal positions saved across server restarts
- **Multiplayer** — full server-side portal tracking with per-player status sync

---

## Controls

| Action | Result |
|---|---|
| Left-click | Fire portal **A** |
| Right-click | Fire portal **B** |
| Walk through either portal | Teleport to its pair, momentum preserved |
| Sneak + Right-click (dye in offhand) | Recolor the gun |
| Sneak + Right-click (no dye) | Reset both portals for this color |

---

## Requirements

| Dependency | Version |
|---|---|
| Minecraft | 1.21.1 |
| NeoForge | 21.1.172+ |
| Java | 21 |

---

## Installation

1. Download `PortalGunClassic-1.21.1-1.0.0.jar` from [Releases](https://github.com/EvilBob01/PortalGunClassic/releases)
2. Drop it into your `mods/` folder alongside NeoForge 1.21.1
3. Launch Minecraft

---

## Crafting

### Portal Core
Crafted from iron ingots and ender pearls — check the in-game recipe viewer for the exact recipe.

### Portal Gun
Crafted from Portal Cores and additional materials — check the in-game recipe viewer.

### Recoloring
Hold the Portal Gun in your main hand, put any dye in your offhand, then **sneak + right-click** to apply the color. One dye is consumed per recolor (unless in Creative mode).

---

## Building from Source

Requires Java 21 and Git.

```bash
git clone https://github.com/EvilBob01/PortalGunClassic.git
cd PortalGunClassic
./gradlew build
# Output: build/libs/PortalGunClassic-1.21.1-1.0.0.jar
```

First build downloads the NeoForge toolchain and takes 15–20 minutes. Subsequent builds use the Gradle cache and complete in under a minute.

---

## Changelog

See [CHANGELOG.md](CHANGELOG.md) for full version history.

---

## License

This project is dual-licensed:

- The mod code is licensed under the **GNU Lesser General Public License v3.0** — see [COPYING.LESSER](https://github.com/EvilBob01/PortalGunClassic/blob/master/COPYING.LESSER) for the full text.
- The GNU General Public License v3.0 is also included as a dependency of the LGPL — see [COPYING](https://github.com/EvilBob01/PortalGunClassic/blob/master/COPYING) for the full text.

In short: you are free to use, modify, and distribute this mod, including in modpacks, provided that any modifications to the mod code itself are released under the same LGPL license.

---

## Credits

Original concept and code by [iChun](https://github.com/iChun).
NeoForge 1.21.1 port and multi-color redesign by EvilBob01.
