![Icon](./src/main/resources/assets/animated-chunks/icon.png)

# Animated Chunks

[![Mod Loader](https://img.shields.io/badge/Mod%20Loader-Fabric-lightyellow)](https://fabricmc.net)
[![Modrinth](https://img.shields.io/modrinth/dt/animated-chunks?logo=modrinth)](https://modrinth.com/mod/animated-chunks)
[![CurseForge](https://cf.way2muchnoise.eu/full_678609_downloads.svg)](https://curseforge.com/minecraft/mc-mods/animated-chunks)

An enhanced fork of [cadenkriese's mod](https://github.com/cadenkriese/smooth-chunks) for the latest versions of the game, with a completely different code base.

**NOTE: This mod WILL NOT support Sodium, since Sodium doesn't render separate chunks, but whole regions, hence separate chunk animations are rendered impossible. Anyone interested in making sodium compatibility, be my guest**

## What is this?

Animated Chunks is a Fabric mod that adds animations of currently loading chunks. This makes chunk loading generally seem much more pleasant than them appearing out of thin air. There are multiple built-in animations and ease types, and if this isnt't enough, there's an API which will allow you to add your own animations and eases<br/>(with ease :trollface:)

Generally, this is what you'd call an "eye-candy" mod.

### Currently supported animations:

Rise:

![rise](gifs/rise.gif)

Fall:

![fall](gifs/fall.gif)

Scale:

![sale](gifs/scale.gif)

Fly in:

![fly-in](gifs/fly-in.gif)

### Currently supported interpolation types:

- Linear
- Sine
- Quadratic
- Elastic

## Future plans

In the future, I plan to extend the scope of this mod to not only smoothen the chunk loading, but entity spawning/despawning/dying, liquid flowing, crops growing etc.

## License

This mod is under the [MIT License](./LICENSE).

