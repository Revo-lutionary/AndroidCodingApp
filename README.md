# CodeLearn

A free, ad-free, offline-first Android app for learning to code in **Python**,
**Lua/Luau**, and **C++**. No ads, no in-app purchases, no tracking. All code
execution happens on-device — no backend server required.

## Status

This is an early-stage scaffold (Phase 0). Navigation, DI wiring, curriculum
loading, and a placeholder code editor are in place. Live code execution for
each language is not implemented yet — see the roadmap below.

## Module map

```
:app                — Compose UI, navigation, Hilt DI wiring, theming
:core-model          — Pure Kotlin domain models (Lesson, Track, Language, ExecutionResult)
:core-curriculum     — Loads lesson content from git-tracked JSON assets
:core-data           — Room database for user progress/settings (not lesson content)
:core-editor         — Sora-Editor wrapper composable for the code editor UI
:core-execution      — Shared ExecutionEngine interface implemented by each language engine
```

Future modules (not yet present): `:engine-python` (Chaquopy), `:engine-lua`
(NDK/JNI Luau VM), `:engine-cpp` (bundled Clang toolchain), `:sync-firebase`
(optional opt-in progress sync).

## Curriculum content

Lessons live as JSON files under `core-curriculum/src/main/assets/curriculum/<language>/<track>/<lesson>.json`,
indexed by `manifest.json`. This keeps lesson content diffable and reviewable
in git, with no database migrations required as content grows.

## Building

Requires JDK 21 and the Android SDK (compileSdk/targetSdk 37, minSdk 26, AGP 9.1.1,
Gradle 9.7.1). AGP 9.x's built-in Kotlin support is used instead of the classic
`org.jetbrains.kotlin.android` plugin (which is no longer compatible with AGP 9's
DSL); KSP 2.3+ is required for Room/Hilt annotation processing to work under it.

```bash
./gradlew assembleDebug
./gradlew testDebugUnitTest
./gradlew lintDebug
```

## Roadmap

1. Curriculum content authoring (fundamentals track for all 3 languages)
2. Sora-Editor syntax highlighting wired into the Playground screen
3. Python execution via Chaquopy
4. Lua/Luau execution via an embedded Luau VM (NDK/JNI)
5. C++ execution via a bundled Clang toolchain (subprocess compile + run)
6. Progress tracking UI
7. Optional opt-in device sync (Firebase free tier)
8. Polish, accessibility, Play Store listing

## Licensing note

This project is intended to be released under an OSI-approved open-source
license. This is required because the planned Python engine (Chaquopy) is
free to use only in open-source-licensed apps — a commercial license is
otherwise required. The code editor (Sora-Editor) is LGPL-2.1; used here as
an unmodified dependency, which requires only an in-app "Open Source
Licenses" notice, not open-sourcing the app itself. The Luau interpreter,
used for the Lua/Luau engine, is MIT licensed.
