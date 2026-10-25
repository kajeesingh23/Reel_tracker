package com.kajeesingh.reelguard

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.SystemClock
import android.provider.Settings
import android.view.Gravity
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Best-effort inference from accessibility events; screen text is never persisted or transmitted. */
class ReelAccessibilityService : AccessibilityService() {
    private var overlay: TextView? = null
    private var lastCountAt = 0L
    private var lastSignature = 0
    private val prefs by lazy { getSharedPreferences("reel_guard", Context.MODE_PRIVATE) }
    private val wm by lazy { getSystemService(WINDOW_SERVICE) as WindowManager }
    private fun day() = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
    private fun count(): Int {
        if (prefs.getString("day", "") != day()) prefs.edit().putString("day", day()).putInt("count", 0).apply()
        return prefs.getInt("count", 0)
    }
    override fun onServiceConnected() { super.onServiceConnected(); showOverlay() }
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        event ?: return
        val pkg = event.packageName?.toString() ?: return
        if (pkg != "com.instagram.android" && pkg != "com.google.android.youtube") { hideOverlay(); return }
        showOverlay()
        if (event.eventType != AccessibilityEvent.TYPE_VIEW_SCROLLED &&
            event.eventType != AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) return
        val now = SystemClock.elapsedRealtime()
        val signature = 31 * pkg.hashCode() + (event.className?.hashCode() ?: 0) +
            (event.contentDescription?.hashCode() ?: 0) + event.eventType
        if (now - lastCountAt < 1200L) return
        if (event.eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED && signature == lastSignature) return
        lastSignature = signature
        lastCountAt = now
        val n = UsageLogic.safeIncrement(count(), 1)
        prefs.edit().putString("day", day()).putInt("count", n).apply()
        updateOverlay()
    }
    override fun onInterrupt() { hideOverlay() }
    override fun onDestroy() { hideOverlay(); super.onDestroy() }
    private fun showOverlay() {
        if (!Settings.canDrawOverlays(this) || overlay != null) return
        val v = TextView(this).apply {
            textSize = 13f; setTextColor(Color.WHITE); typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER; setPadding(24, 10, 24, 10)
            background = GradientDrawable().apply { setColor(Color.rgb(20,20,22)); cornerRadius = 48f; setStroke(2, Color.rgb(80,80,85)) }
            contentDescription = "Reel Guard daily reel counter. Tap to open controls."
            setOnClickListener {
                startActivity(Intent(this@ReelAccessibilityService, MainActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP))
            }
        }
        val params = WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            PixelFormat.TRANSLUCENT).apply { gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL; y = dp(8) }
        try { wm.addView(v, params); overlay = v; updateOverlay() } catch (_: Exception) { overlay = null }
    }
    private fun hideOverlay() {
        overlay?.let { try { wm.removeView(it) } catch (_: Exception) {} }
        overlay = null
    }
    private fun updateOverlay() {
        val n = count()
        val color = when { n <= 200 -> Color.rgb(35,190,115); n <= 400 -> Color.rgb(245,174,45); else -> Color.rgb(235,75,75) }
        overlay?.apply {
            text = "🔥  " + n
            (background as? GradientDrawable)?.setStroke(2, color)
            contentDescription = "Reels today: " + n + ". " + when { n <= 200 -> "Green zone"; n <= 400 -> "Moderate zone"; n <= 700 -> "Extreme zone"; else -> "Very high zone" } + ". Tap to open controls."
        }
    }
    private fun dp(v: Int) = (v * resources.displayMetrics.density).toInt()
}