# SundayTools

A productivity utility for hosts/moderators of the mobile game Sunday City. It
runs as a Messenger-style floating bubble, captures a single screenshot on tap,
runs on-device OCR to pull out the visible player tag, and lets you save it to
a searchable daily list with clipboard copy, duplicate detection, history, and
CSV/TXT export.

The app only reads pixels already visible on the user's own screen when they
explicitly tap the bubble. It never captures continuously, does not touch or
modify the game process, and stores everything locally on-device.

## Opening the project

1. Open this folder in Android Studio (Koala or newer recommended).
2. Let Gradle sync — it will pull Compose, Room, Hilt, ML Kit Text Recognition,
   and WorkManager from Google/Maven Central.
3. Run on a device or emulator with **API 26+**.

## First run flow

1. Launch the app and tap **Start Floating Bubble**.
2. Grant the "Draw over other apps" permission when prompted (this opens the
   system Settings screen, since it can't be granted via a normal dialog).
3. Open Sunday City (or any app) — the bubble now floats on top.
4. Tap the bubble once. The first tap will also prompt the one-time
   "Cast/record your screen" (MediaProjection) permission; grant it.
5. The bubble captures a single frame, runs OCR, and shows the
   Player Tag / Save / Copy Again / Cancel popup.
6. Saved tags appear instantly on the Home screen's "Today's Tags" list.

## Architecture

- **MVVM + Repository pattern**, Hilt for DI, Room for storage, Kotlin
  Coroutines/Flow throughout.
- `overlay/` — the floating bubble and save-dialog, both hosted directly in
  `WindowManager` from a foreground `Service` (`OverlayBubbleService`), so they
  work independently of the Activity lifecycle, just like Messenger's Chat
  Heads.
- `capture/` — a single-frame `MediaProjection` capture (`ScreenCaptureManager`)
  plus the transparent `ScreenCapturePermissionActivity` needed to request
  that permission (a Service alone cannot show that system dialog).
- `ocr/` — wraps Google ML Kit's on-device text recognizer and picks the most
  tag-shaped text block out of everything visible in the capture.
- `database/` — a single `player_tags` Room table, grouped by a `dayKey`
  ("yyyy-MM-dd") so "Today" and "History" both read from one table without a
  separate migration step at midnight.
- `worker/` — `ArchiveWorker` + `ArchiveScheduler` run at the user-configured
  Archive Time (default 11:00 PM) as an extension point for future
  archive-time behavior; the Today/History split itself is derived from the
  `dayKey`, so it updates automatically at midnight even if the device is
  briefly offline or asleep at exactly 11 PM.
- `export/` — native CSV/TXT writers; swap in an .xlsx writer later without
  changing the call site.
- `ui/` — Jetpack Compose + Material 3 screens (Home, History, History Detail,
  Settings) and reusable components (search bar, tag row).

## Extending

The package layout leaves room for the roadmap features mentioned in the
spec (Blacklist Manager, Thrower Manager, Firebase Backup/Cloud Sync,
multi-language, Statistics Dashboard):

- Add new Room entities/DAOs next to `PlayerTagEntity`/`PlayerTagDao`.
- Add new repositories in `repository/`, following `PlayerTagRepository`.
- Add new Compose screens in `ui/screen/` and wire them into `MainActivity`'s
  `NavHost`.
- For cloud sync, add a `sync/` package and inject it the same way
  `PlayerTagRepository` is injected today — the Room DAO stays the local
  source of truth.
