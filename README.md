# Server List Zoom (Fabric 1.21.x)

Forces GUI Scale to **2** only while the "Play Multiplayer" server list
screen is open. The instant you leave that screen — back to the title
screen, into Direct Connect / Add Server, or joining a world — your
normal GUI Scale (whatever it was, e.g. 4) comes right back. Nothing
else is touched: inventory, chat, options, everything else keeps using
your regular scale the whole time.

All the actual logic is one small file:
`src/main/java/com/serverlistzoom/ServerListZoomMod.java`

## How it works (short version)

It doesn't hack the rendering of one screen — it just flips the
game's real `guiScale` option to `2` right after the server list
screen finishes opening, and flips it back the moment that screen
closes. That's the same mechanism the vanilla Video Settings slider
uses, so mouse clicks, scrollbars, and text all stay perfectly in
sync — there's no custom scaling math that could get mouse
coordinates out of alignment.

## Easiest path: let GitHub build the jar for you (no Java/Gradle needed)

1. Go to github.com, sign up/log in (free), and create a new repository
   (public or private, doesn't matter).
2. On the repo page, use "Add file" → "Upload files" and drag in
   *everything* from this zip (keep the folder structure — including the
   hidden `.github` folder; if your OS hides it, use "show hidden files"
   or a file manager/archive tool that shows it).
3. Commit the upload. This automatically triggers a build (there's a
   workflow file already included).
4. Click the "Actions" tab at the top of your repo, click into the run
   that just started (a few minutes), and once it's green, scroll down
   to "Artifacts" and download `server-list-zoom` — that's your built
   jar, ready to use.

From there, skip to "Installing" below.

## Building it yourself instead

This project is set up for **Minecraft 1.21.11**. I've filled in
`gradle.properties` with dependency versions I confirmed are valid for
1.21.11 as of writing (Sept 2026):

- Yarn mappings: `1.21.11+build.6`
- Fabric Loader: `0.19.5`
- Fabric API: `0.141.6+1.21.11`

Worth knowing: **1.21.11 is the last Minecraft version Yarn mappings
support.** Anything from 26.1 onward uses Mojang's own mappings
instead, which changes the `mappings` line in `build.gradle` (you'd use
`loom.officialMojangMappings()` instead of a `yarn_mappings` string) —
not relevant for you now on 1.21.11, but worth knowing if you ever
update Minecraft past this version.

If a newer Yarn build for 1.21.11 has since come out, or you're on a
different 1.21.x release, the exact numbers above may need a bump.
Easiest fix — two options:

1. **Use the official generator** at
   https://fabricmc.net/develop/template/ — pick your exact Minecraft
   version, tick "Fabric API," download it, then just copy
   `ServerListZoomMod.java` and the `entrypoints` block from
   `fabric.mod.json` (below) into that generated project. This is the
   safest option since it always has current, correct version numbers.
2. Or just edit `minecraft_version` in `gradle.properties` to your
   version and update `yarn_mappings` / `fabric_version` to match (check
   https://fabricmc.net/develop/ or the Fabric API page on Modrinth/
   CurseForge for the right build number for your version).

To build (once versions match your setup) — this project doesn't include
the Gradle wrapper, so use a locally installed Gradle:

```
gradle build
```

(If you don't have Gradle installed, get it from gradle.org, or use the
official generator mentioned above, which includes its own wrapper —
then you'd use `./gradlew build` instead.)

The mod jar lands in `build/libs/server-list-zoom-1.0.0.jar`.

## Installing

You need, in your `.minecraft/mods` folder:
1. **Fabric API** (matching your Minecraft version) — download separately.
2. This mod's jar.

Fabric Loader itself needs to be installed via the Fabric installer
(fabricmc.net) as your Minecraft profile.

## Customizing

Open `ServerListZoomMod.java` and change this line:

```java
private static final int SERVER_LIST_SCALE = 2;
```

Valid values match vanilla's GUI Scale option: `0` = Auto, or `1`, `2`,
`3`, `4`...

If you want a *different* screen zoomed instead of/in addition to the
server list, change the `instanceof MultiplayerScreen` check to
whatever screen class you want (e.g. `TitleScreen`, `SelectWorldScreen`).
