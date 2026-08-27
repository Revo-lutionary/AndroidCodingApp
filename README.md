# CodeLearn

A free, ad-free, offline-first Android app for learning to code in **Python**,
**Lua/Luau**, and **C++**. No ads, no in-app purchases, no tracking. All code
execution happens on-device — no backend server required.

## Status

Navigation, DI wiring, curriculum loading, and progress tracking are in
place, with a Duolingo-style flow: pick a language → a roadmap of lesson/quiz
nodes → a tabbed lesson (Reference / Challenge / Code / Solution) with a
symbol toolbar and a Run button, or a multiple-choice quiz. **Lua code
actually executes on-device** via LuaJ (a pure-JVM Lua interpreter — no NDK
needed); Python and C++ execution are not wired up yet.

## Module map

```
:app                — Compose UI, navigation, Hilt DI wiring, theming
:core-model          — Pure Kotlin domain models (Lesson, QuizQuestion, RoadmapNode, Language, ExecutionResult)
:core-curriculum     — Loads lesson/quiz content from git-tracked JSON assets
:core-data           — Room database for user progress/settings (not lesson content)
:core-editor         — Sora-Editor wrapper composable for the code editor UI
:core-execution      — Shared ExecutionEngine interface implemented by each language engine
:engine-lua          — Real Lua execution via LuaJ (pure JVM, standard Lua 5.1 semantics)
```

Future modules (not yet present): `:engine-python` (Chaquopy), `:engine-cpp`
(bundled Clang toolchain), `:sync-firebase` (optional opt-in progress sync).
`:engine-lua` currently targets standard Lua via LuaJ as an interim engine —
swapping in the real Luau VM via NDK/JNI (for exact Roblox-style semantics)
is tracked as follow-up work.

## App flow

1. **Language picker** — choose Python, Lua(u), or C++ to start its roadmap.
2. **Roadmap** — a vertical path of lesson/quiz nodes; locked until the
   previous node is completed, matching progress stored in Room.
3. **Lesson** — tabs for Reference (explanation), Challenge (the task),
   Code (editor + a symbol toolbar above the keyboard + Run + output), and
   Solution. An "Explain with AI" button on the Challenge tab copies a
   ready-made prompt to the clipboard and opens a free web AI assistant
   (ChatGPT, Copilot, or DeepSeek) of the learner's choice — no AI backend
   of our own.
4. **Quiz** — a multiple-choice question with immediate right/wrong
   feedback and an explanation.

## Curriculum content

Lessons and quizzes live as JSON files under
`core-curriculum/src/main/assets/curriculum/<language>/<track>/<node>.json`,
indexed by `manifest.json` (an ordered list of `{id, type}` nodes per
track — `lesson` or `quiz`). This keeps content diffable and reviewable in
git, with no database migrations required as content grows. Room only
stores mutable user progress, never content itself.

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

1. ~~Curriculum content authoring~~ (seeded; more lessons/quizzes needed for all 3 languages)
2. ~~Duolingo-style navigation flow, symbol toolbar, quiz UI~~
3. ~~Lua execution (LuaJ)~~
4. Python execution via Chaquopy
5. C++ execution via a bundled Clang toolchain (subprocess compile + run)
6. Real Luau VM (NDK/JNI) to replace the interim LuaJ engine
7. Sora-Editor syntax highlighting (TextMate grammars) per language
8. Optional opt-in device sync (Firebase free tier)
9. Polish, accessibility, Play Store listing

## License

This project is licensed under the **Mozilla Public License 2.0** (see
[`LICENSE`](LICENSE)). MPL 2.0 is OSI-approved, which is what makes it
possible to use Chaquopy's Python engine for free once that's wired up
(Chaquopy's free tier requires an OSI-approved open-source license, or a
paid commercial license otherwise). MPL 2.0 is file-level weak copyleft:
anyone can view, fork, and modify this repository, and if they redistribute
a modified version of a file from it, that file's modifications must stay
under MPL 2.0 — but they can still combine it with proprietary code in a
larger work. (A "view-only, no forking" license was considered, but isn't
achievable for a public GitHub repo — GitHub's own Fork button works
regardless of license — and wouldn't be an OSI-approved open-source license
either, which would have blocked free Chaquopy usage.)

Third-party licenses of note: Sora-Editor (code editor) is LGPL-2.1, used
here as an unmodified dependency, which requires only an in-app "Open
Source Licenses" notice rather than open-sourcing the app itself. LuaJ is
MPL 2.0. The Luau interpreter planned for later is MIT licensed.
