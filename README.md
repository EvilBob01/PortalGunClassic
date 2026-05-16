# PortalGunClassic

A classic Portal Gun mod for Minecraft 1.21.1 on NeoForge.

Shoot blue and orange portals into walls, floors, and ceilings — step through one and emerge from the other. Classic iChun-style Portal Gun mechanics, ported and modernized for NeoForge 1.21.1.

---

## Features

- **Blue & Orange Portal Guns** — craft and wield both types
- **Portal Projectiles** — fire portal shots that travel through the air and embed in surfaces
- **Linked Portals** — step through a portal and teleport to its pair, preserving momentum
- **HUD Overlay** — shows portal status (active/inactive) for each colour while holding a gun
- **Key Bindings** — swap gun type (`G`) and reset portals (`R`)
- **Persistence** — portal positions saved per-dimension across server restarts
- **Multiplayer** — full server-side portal tracking with per-player status sync

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
2. Drop it into your `mods/` folder
3. Launch Minecraft with NeoForge 1.21.1

---

## Crafting

### Portal Core
Craft from iron ingots and ender pearls (see in-game recipe viewer).

### Portal Gun (Blue / Orange)
Craft from Portal Cores and additional materials (see in-game recipe viewer).

---

## Key Bindings

| Key | Action |
|---|---|
| `G` | Swap between Blue and Orange gun |
| `R` | Reset portals (hold Shift to reset only the gun you're holding) |

---

## Building from Source

Requires Java 21 and Git.

```bash
git clone https://github.com/EvilBob01/PortalGunClassic.git
cd PortalGunClassic
./gradlew build
# Output: build/libs/PortalGunClassic-1.21.1-1.0.0.jar
```

First build will take 15–20 minutes to download the NeoForge toolchain. Subsequent builds are fast.

---

## Changelog

See [CHANGELOG.md](CHANGELOG.md) for full version history.

---

## License

Licensed under the [GNU Lesser General Public License v3.0](COPYING.LESSER).

---

## Credits

Original concept and code by [iChun](https://github.com/iChun).  
NeoForge 1.21.1 port maintained by EvilBob01.
