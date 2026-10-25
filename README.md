# Reel Guard (Android)

A privacy-first Android digital-wellbeing tracker with a compact black pill inspired by the supplied reference, a manual Reels/Shorts session counter, daily limit meter, and Instagram/YouTube foreground usage summaries.

## Tracking limitation
Android does not expose a reliable public per-reel/per-Short swipe count. Reel Guard reads aggregate foreground time using Android Usage Access and provides a manual counter. It does not inspect screen content or use an Accessibility Service.

## Build
Open in Android Studio with JDK 17 and Android SDK 35. Run `gradle testDebugUnitTest assembleDebug`.
APK output: `app/build/outputs/apk/debug/app-debug.apk`.
GitHub Actions runs unit tests and builds a debug APK on pushes, pull requests and manual dispatch. Download `reel-guard-debug-apk` from the Actions run artifacts.

## Permissions and privacy
Grant Usage Access in Android Settings to see app time. Without it, time is shown as unavailable but the manual counter works. Preferences are stored locally. No analytics or network calls.

See [UAT_AND_TEST_PLAN.md](UAT_AND_TEST_PLAN.md). Not affiliated with Instagram, YouTube or Google.