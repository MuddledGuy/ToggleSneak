ToggleSneak
===========

ToggleSneak is a Minecraft Forge client mod for Minecraft 1.12.2 that lets
players toggle sneaking and sprinting instead of holding the movement keys down.

Features:

- Toggle sneak and sprint with normal movement key presses.
- Enable or disable toggle behavior with configurable in-game keybinds.
- Configure key hold timing.
- Avoid sneak toggling while dismounting or flying.
- Boost creative flying speed with the sprint key.

## Building

This project uses ForgeGradle and the included Gradle wrapper.

```sh
gradlew setupDecompWorkspace
gradlew build
```

On non-Windows systems, use `./gradlew` instead of `gradlew`.

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
