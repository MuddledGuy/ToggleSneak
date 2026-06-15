ToggleSneak
===========

ToggleSneak is a Minecraft Forge client mod for Minecraft 26.1.2 that lets
players toggle sneaking and sprinting behavior without holding extra movement
keys down.

Features:

- Dedicated keybinds for enabling or disabling the sneak and sprint functions.
- Toggle Sneak on or off with the normal Sneak key while the sneak function is
  enabled.
- Automatically sprint while moving forward when the sprint function is enabled.
- Optional setting to untoggle Sneak by pressing the Sprint key.
- Optional HUD status display with configurable position and display style.
- Optional creative-mode fly boost.
- Forge mod config screen support.

## Keybinds

Default keybinds:

- `G`: Sneak function enable/disable
- `V`: Sprint function enable/disable

These can be changed from Minecraft's Controls screen under the `ToggleSneak`
category.

## Configuration

Open the ToggleSneak config screen from Minecraft's Mods menu.

Available settings include:

- Sneak function enabled on startup
- Sprint function enabled on startup
- Sprint key untoggles sneak
- Fly boost function enabled
- HUD enabled
- HUD display style
- HUD horizontal and vertical position

## Building

This project uses ForgeGradle and the included Gradle wrapper.

On Windows:

```sh
gradlew.bat build
```

On non-Windows systems:

```sh
./gradlew build
```

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
