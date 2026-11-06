# Luncher — One UI 9 style, Lawnchair feature plan (Stage 1)

A clean-room Compose launcher. It does **not** contain Lawnchair code: it
re-implements Lawnchair's feature list and settings structure in stages, with a
One UI 9 (8.5 look + 9 controls) front end. The full reasoning, the researched
One UI 9 trait list, and the true-fork alternative are in
`../ONEUI9-LAWNCHAIR-SPEC.md`.

## Build (Mobile Harness)

1. Replace the project files with this folder's contents.
2. Run `gradle assembleDebug`. **Stop at the result** — installed APK or the
   exact error text — before adding anything.
3. Install, then set as default: Settings → Apps → Default apps → Home app →
   Luncher.

Toolchain is unchanged from the previous fixed project on purpose:
AGP 8.13.0, Kotlin 1.9.24, Compose compiler 1.5.14, Compose UI 1.6.11,
Material3 1.2.1, compileSdk / targetSdk 36, minSdk 29.

Verified **statically only** — there is no Android SDK in the environment that
produced this package, so the first `assembleDebug` on your phone is the first
real compile. That is stated plainly instead of claiming a build that never ran.

## What works in Stage 1

- Real app list (PackageManager launcher query, `<queries>` declared for
  Android 11+ package visibility), A–Z, icons decoded off the main thread once.
- Home: live clock header, One UI 9 home search pills (Google / Finder / both /
  off, set in Settings), app grid (4 or 5 columns), page dots, floating glass
  dock (One UI 9 Home Up style) with Phone / Messages / Browser / Camera.
- Apps screen: swipe up, vertical A–Z grid, letter bubble while scrolling,
  Finder pill at the **bottom** that filters live. Back or swipe gesture
  conventions follow stock Android.
- Long-press an app → One UI bottom sheet: Add to Home / Remove from Home,
  App info, Hide app. Hidden apps can be restored in Settings.
- Long-press empty home space → edit bar: Wallpaper and style (system picker),
  Widgets (Stage 2 notice), Settings.
- Settings in One UI style: large title, floating back button, grouped cards
  (28dp, no borders), pill switches with outlined off state, accent `#0381FE`,
  AMOLED black dark theme.

## Deliberately not in Stage 1

Widgets (AppWidgetHost is Views-based; it gets its own verified step),
drag & drop, folders / enlarged folders, notification badges, icon packs,
multiple home pages, true backdrop blur for the dock (Stage 1 uses
translucency + border + shadow; real blur needs a blur library or API 31+
render effects), QuickStep recents and Google Feed (impossible without
system privileges — see the spec).

## Smoothness rules baked in

- Drawer transition: 300ms sine easing (skill token), fade + slide.
- No icon decoding, package queries or wallpaper work on the UI thread.
- Grids use stable keys (`package/class`), so recompositions do not reshuffle.
- Press targets ≥ 48dp in settings; home grid is non-scrolling so the swipe-up
  gesture never fights a scroll container.

## After Stage 1 compiles

Run the Vibebreaker QUICK audit (passes 01, 02, 03, 04, 11, 12, 13, 19, 20)
from the `vibebreaker` package you uploaded, fix confirmed findings, then
start Stage 2 (widgets + drag/drop + folders) as separate single-write turns.
