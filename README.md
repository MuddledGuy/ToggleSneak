ToggleSneak
===========

ToggleSneak is a Minecraft Forge/ModLoader client mod for Minecraft 1.2.5 that lets
players toggle sneak behavior and automatically sprint while moving forward.

Features:

- Dedicated keybinds for enabling or disabling the sneak and sprint functions.
- Toggle Sneak on or off with the normal Sneak key while the sneak function is
  enabled.
- Automatically sprint while moving forward when the sprint function is enabled.
- Optional setting to untoggle Sneak by pressing the Sprint key.
- Minimal HUD status display with configurable visibility, position, and style.
- Avoid sneak toggling while dismounting or flying.

## Keybinds

Default keybinds:

- `G`: Sneak function enable/disable
- `V`: Sprint function enable/disable

These can be changed from Minecraft's Controls screen under the
`Toggle Sneak&Sprint` category.

## Configuration

Minecraft Forge 1.2.5 does not provide the newer Mods menu config screen for
this mod. Edit the config file directly instead:

```text
config/togglesneak.cfg
```

Available settings include:

- Sneak function enabled on startup
- Sprint function enabled on startup
- Sprint key untoggles sneak
- HUD enabled
- HUD display style
- HUD horizontal and vertical position

## Building

This branch builds against the Minecraft 1.2.5 and Forge 3.4.9.171 jars
installed by PrismLauncher.

To build:

```sh
gradlew build
```

On non-Windows systems, use `./gradlew` instead of `gradlew`. If PrismLauncher
is installed somewhere other than the default AppData path, set `PRISM_ROOT` to
the PrismLauncher directory before building.

Built jars are created under `build/libs`.

## Project Lineage

This project is based on
[BlueAnanas/ZebrasToggleSneak](https://github.com/BlueAnanas/ZebrasToggleSneak),
which was released under the GNU General Public License version 2.

This repository contains later changes and maintenance updates. See `NOTICE`
for attribution details.

## License

This project is licensed under the GNU General Public License version 2. See
`LICENSE`.
