# Reel Guard v2 — UAT and Break-Test Plan

## Automated unit tests
- Percentage bounds, invalid limit and integer overflow safety.
- Daily zone boundaries: 200/201, 400/401 and 700/701.

## Setup UAT on Android / Galaxy S23
1. Install APK and open app; verify count starts at zero.
2. Enable Reel Guard in Android Accessibility settings.
3. Grant display-over-other-apps permission.
4. Open Instagram and enter Reels; verify the pill appears at top centre.
5. Swipe through 20–30 reels at different speeds; compare estimate with actual swipes and record misses/double-counts.
6. Repeat on YouTube Shorts.
7. Tap pill from each app; verify Reel Guard controls open and target can be edited.
8. Check colors and labels at 200, 201, 400, 401, 700 and 701 counts (use test builds or controlled counter data).
9. Disable overlay permission; app must not crash. Re-enable and reopen service.
10. Disable accessibility service; counter should stop counting. Re-enable and verify resumes.
11. Force-stop/relaunch and reboot; verify count/target persistence and reset at local midnight.
12. Test screen rotation, split screen, app updates, dark mode, rapid swipes, notifications, and switching between Instagram/YouTube.
13. Check accessibility disclosures and verify no screen text is persisted or network requests occur.

## Known limitation
This is event-based inference, not guaranteed exact tracking. Android and the social apps do not provide an official public per-item count. Device-level testing is required to evaluate how well scroll/content events map to actual reels.
