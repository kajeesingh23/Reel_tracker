package com.kajeesingh.reelguard

enum class ReelZone { GREEN, AMBER, EXTREME, VERY_HIGH }

object UsageLogic {
    fun percent(used: Int, limit: Int): Int {
        if (limit <= 0) return if (used > 0) 100 else 0
        return ((used.coerceAtLeast(0).toLong() * 100L) / limit).coerceIn(0L, 100L).toInt()
    }
    fun isOverLimit(used: Int, limit: Int) = limit >= 0 && used > limit
    fun clampLimit(value: Int) = value.coerceIn(1, 1440)
    fun safeIncrement(value: Int, delta: Int) = (value.toLong() + delta.coerceAtLeast(0)).coerceIn(0L, Int.MAX_VALUE.toLong()).toInt()
    fun zone(count: Int): ReelZone = when {
        count <= 200 -> ReelZone.GREEN
        count <= 400 -> ReelZone.AMBER
        count <= 700 -> ReelZone.EXTREME
        else -> ReelZone.VERY_HIGH
    }
}