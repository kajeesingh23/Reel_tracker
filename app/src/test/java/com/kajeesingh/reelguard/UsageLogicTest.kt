package com.kajeesingh.reelguard
import org.junit.Assert.*
import org.junit.Test
class UsageLogicTest { @Test fun percentageEdges(){ assertEquals(50,UsageLogic.percent(5,10)); assertEquals(100,UsageLogic.percent(20,10)); assertEquals(0,UsageLogic.percent(-2,10)); assertEquals(0,UsageLogic.percent(0,0)); assertEquals(100,UsageLogic.percent(1,0)) }; @Test fun limits(){ assertEquals(1,UsageLogic.clampLimit(Int.MIN_VALUE)); assertEquals(1440,UsageLogic.clampLimit(Int.MAX_VALUE)); assertEquals(30,UsageLogic.clampLimit(30)) }; @Test fun threshold(){ assertFalse(UsageLogic.isOverLimit(5,5)); assertTrue(UsageLogic.isOverLimit(6,5)) }; @Test fun incrementSafe(){ assertEquals(0,UsageLogic.safeIncrement(0,-1)); assertEquals(Int.MAX_VALUE,UsageLogic.safeIncrement(Int.MAX_VALUE,2)) } }
