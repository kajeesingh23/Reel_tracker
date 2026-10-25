package com.kajeesingh.reelguard

import org.junit.Assert.assertEquals
import org.junit.Test

class UsageLogicTest {
    @Test fun percentIsBounded() {
        assertEquals(0, UsageLogic.percent(-1, 100))
        assertEquals(50, UsageLogic.percent(50, 100))
        assertEquals(100, UsageLogic.percent(150, 100))
        assertEquals(100, UsageLogic.percent(1, 0))
    }
    @Test fun limitClamps() {
        assertEquals(1, UsageLogic.clampLimit(0))
        assertEquals(1440, UsageLogic.clampLimit(5000))
    }
    @Test fun incrementNeverGoesNegativeOrOverflows() {
        assertEquals(5, UsageLogic.safeIncrement(5, -2))
        assertEquals(Int.MAX_VALUE, UsageLogic.safeIncrement(Int.MAX_VALUE, 1))
    }
    @Test fun zonesHaveExactBoundaries() {
        assertEquals(ReelZone.GREEN, UsageLogic.zone(0))
        assertEquals(ReelZone.GREEN, UsageLogic.zone(200))
        assertEquals(ReelZone.AMBER, UsageLogic.zone(201))
        assertEquals(ReelZone.AMBER, UsageLogic.zone(400))
        assertEquals(ReelZone.EXTREME, UsageLogic.zone(401))
        assertEquals(ReelZone.EXTREME, UsageLogic.zone(700))
        assertEquals(ReelZone.VERY_HIGH, UsageLogic.zone(701))
    }
}