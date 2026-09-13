# Iron Man Voice Mask — Fabric 1.21.11

A voice-controlled animated Iron Man helmet built around the supplied
`Iron man mask.bbmodel`.

## What is included

- Minecraft Java **1.21.11**
- Fabric Loader / Fabric API
- GeckoLib 5 armor renderer
- Simple Voice Chat API listener (`ClientSoundEvent`)
- Offline CMU Sphinx4 speech recognition (no cloud key)
- `JARVIS OPEN MASK` and `JARVIS CLOSE MASK`
- Server-authoritative state validation and multiplayer synchronization
- Smooth 0.8 second faceplate transition
- Persistent OPEN state
- `/ironman mask open` and `/ironman mask close` test commands
- Config file generated at `config/ironman-voice-mask.properties`
- Original `.bbmodel` kept in `model_source/`

## Model conversion

The supplied Blockbench file contains 16 elements and no animation/bone data.
The conversion preserves its original geometry/UV sources:

- original elements **0–9** -> `faceplate`
- original elements **10–15** -> `helmet_shell`
- parent head bone -> `armorHead`

The embedded 64x64 main texture is kept unchanged in the top half of the
runtime atlas. The original 16x16 face-detail texture is nearest-neighbor
scaled into the bottom half and its UVs are remapped without repainting it.

## Required runtime mods

Install on both client and server:

1. Fabric Loader for 1.21.11
2. Fabric API compatible with 1.21.11
3. GeckoLib 5.4.5+ for 1.21.11
4. Simple Voice Chat 2.6.x for 1.21.11
5. This mod

Simple Voice Chat still needs its normal UDP/server configuration.

## Building

Java 21 is required.

Preferred:

```bash
./gradlew build
```

Windows:

```bat
gradlew.bat build
```

If `gradle/wrapper/gradle-wrapper.jar` is not present in this source archive,
install Gradle 9.2.1 once and run:

```bash
gradle wrapper --gradle-version 9.2.1
gradlew.bat build
```

The resulting mod JAR will be under `build/libs/`.

## Test

Give yourself the helmet:

```mcfunction
/give @s ironman:iron_man_helmet
```

Equip it in the head slot.

While your microphone is being captured by Simple Voice Chat, say:

- `JARVIS OPEN MASK`
- `JARVIS CLOSE MASK`

You can test the exact same server-side state machine without speech:

```mcfunction
/ironman mask open
/ironman mask close
```

## Voice behavior

Simple Voice Chat microphone PCM is copied from `ClientSoundEvent`.
The event is never cancelled and the raw audio is never replaced.

The local worker:

1. receives 48 kHz signed PCM16 samples;
2. down-samples them to 16 kHz;
3. feeds a persistent Sphinx4 recognizer off the game/render thread;
4. normalizes the recognized phrase;
5. sends only the OPEN/CLOSE action to the server;
6. the server verifies the player really has `ironman:iron_man_helmet` in HEAD.

## Config

First client launch creates:

`config/ironman-voice-mask.properties`

Defaults:

```properties
enableVoiceCommands=true
openPhrase=jarvis open mask
closePhrase=jarvis close mask
recognitionThreshold=0.82
voiceCommandCooldownMs=1750
debugVoiceRecognition=false
enableConfirmationSound=false
```

When `enableConfirmationSound=true`, a short vanilla UI click is played locally
at different pitches for opening and closing. No movie/JARVIS audio is bundled.

## Notes

- The animation asset JSON is included and mirrors the runtime pose.
- Runtime multiplayer interpolation is intentionally driven from the synced
  state machine rather than a one-shot animation trigger. That is why a player
  who joins after the mask is already open immediately sees the OPEN pose.
- If the helmet is removed, the server broadcasts CLOSED and clears the state.
- Re-equipping defaults to CLOSED.
