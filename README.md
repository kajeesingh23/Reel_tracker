# Reel Guard (Android)

Reel Guard v2 adds a small top-centre floating counter while Instagram or YouTube is in use. Tap the pill to return to the controls. Counts reset at local midnight and are stored on-device.

## Daily zones
- **0–200:** Green — steady
- **201–400:** Amber — pause and check in
- **401–700:** Red — extreme scrolling
- **701+:** Very high — strongly consider stopping

## Enable automatic counting
1. Install and open Reel Guard.
2. Tap **Enable automatic counting** and enable Reel Guard in Android Accessibility settings.
3. Return to Reel Guard and tap **Allow top counter overlay**; allow display over other apps.
4. Open Instagram Reels or YouTube Shorts. The counter pill should appear at the top centre.
5. Tap the pill to open Reel Guard controls.

## Important accuracy and privacy notes
Android does not expose a reliable official API for individual Reel/Short IDs or swipe counts. The app estimates item changes from accessibility scroll/content events. Depending on app versions, it can miss items or occasionally over-count. Treat it as a useful estimate, not an exact audit. Accessibility access is powerful: enable it only if comfortable. Reel Guard does not save screen text, transmit data, or make network requests. The overlay requires the separate Android “display over other apps” permission.

## Build and test
Open in Android Studio with JDK 17 and Android SDK 35. Run `gradle testDebugUnitTest assembleDebug`. GitHub Actions runs unit tests and builds a debug APK. Download `reel-guard-debug-apk` from the successful Actions run.

See [UAT_AND_TEST_PLAN.md](UAT_AND_TEST_PLAN.md). Not affiliated with Instagram, YouTube or Google.
