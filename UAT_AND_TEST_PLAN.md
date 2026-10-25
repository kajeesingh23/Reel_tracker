# UAT and Break-Test Plan

## Unit tests
- Meter percentages, over-limit clamping, negative values and zero limit.
- Daily limit clamps to 1–1440 minutes.
- Exactly-at-limit vs over-limit behaviour.
- Counter overflow and negative increment safety.

## Manual UAT on Galaxy S23
1. Fresh install/launch; verify no crash and count starts at zero.
2. Increment, decrement at zero, and rapid repeated taps.
3. Set limit to 1; confirm warning only after count exceeds 1.
4. Reject blank, nonnumeric, 0, negative and 1441 limits; accept 1 and 1440.
5. Grant/revoke Usage Access; app time should appear only with access; manual count still works.
6. Force-stop/relaunch; verify count and limit persistence.
7. Cross local midnight; count resets while limit persists.
8. Rotate, background/resume and inspect smaller screens for crashes/overflow.

## Limitations
CI/device tests have not yet run. UsageStatsManager provides aggregate foreground time, not individual reel swipes; time can update with delay. Reel count is manually maintained.
