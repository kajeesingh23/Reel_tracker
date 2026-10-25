package com.kajeesingh.reelguard

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.widget.*

class MainActivity : Activity() {
    private val prefs by lazy { getSharedPreferences("reel_guard", Context.MODE_PRIVATE) }
    private lateinit var countView: TextView
    private lateinit var zoneView: TextView
    private lateinit var limitInput: EditText
    private fun day() = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).format(java.util.Date())
    private fun count(): Int {
        if (prefs.getString("day", "") != day()) prefs.edit().putString("day", day()).putInt("count", 0).apply()
        return prefs.getInt("count", 0)
    }
    private fun limit() = prefs.getInt("limit", 200).coerceIn(1, 10000)
    private fun dp(n: Int) = (n * resources.displayMetrics.density).toInt()
    private fun bg(color: Int, radius: Int) = GradientDrawable().apply { setColor(color); cornerRadius = dp(radius).toFloat() }
    private fun label(s: String, size: Float, color: Int, bold: Boolean = false) = TextView(this).apply {
        text=s; textSize=size; setTextColor(color); if (bold) setTypeface(null, android.graphics.Typeface.BOLD)
    }
    override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState); render() }
    override fun onResume() { super.onResume(); if (::countView.isInitialized) refresh() }
    private fun render() {
        val black=Color.rgb(18,18,20); val green=Color.rgb(35,190,115); val muted=Color.rgb(110,110,116)
        val root=LinearLayout(this).apply { orientation=1; setPadding(dp(20),dp(28),dp(20),dp(24)); setBackgroundColor(Color.rgb(246,246,247)) }
        root.addView(label("REEL GUARD",24f,black,true))
        root.addView(label("Your scroll, made visible.",15f,muted).apply { setPadding(0,dp(4),0,dp(20)) })
        val card=LinearLayout(this).apply { orientation=1; setPadding(dp(20),dp(18),dp(20),dp(18)); background=bg(black,24) }
        card.addView(label("TODAY'S REELS & SHORTS",12f,Color.LTGRAY,true))
        countView=label("0",48f,Color.WHITE,true); card.addView(countView)
        zoneView=label("GREEN ZONE · STEADY",14f,green,true); card.addView(zoneView)
        val bar=ProgressBar(this,null,android.R.attr.progressBarStyleHorizontal).apply { max=700; progressTintList=android.content.res.ColorStateList.valueOf(green); progressBackgroundTintList=android.content.res.ColorStateList.valueOf(Color.DKGRAY) }
        card.addView(bar,LinearLayout.LayoutParams(-1,dp(8)).apply { topMargin=dp(14) })
        root.addView(card)
        root.addView(label("DAILY ZONES",14f,black,true).apply { setPadding(0,dp(22),0,dp(8)) })
        root.addView(zoneRow("0–200 reels","Green · comfortable",Color.rgb(35,190,115)))
        root.addView(zoneRow("201–400 reels","Amber · pause and check in",Color.rgb(245,174,45)))
        root.addView(zoneRow("401–700 reels","Red · extreme scrolling",Color.rgb(235,75,75)))
        root.addView(zoneRow("700+ reels","Very high · strongly consider stopping",Color.rgb(180,40,45)))
        root.addView(label("AUTOMATIC COUNTER",14f,black,true).apply { setPadding(0,dp(24),0,dp(6)) })
        root.addView(label("Enable Reel Guard in Android Accessibility settings, then allow it to display over other apps. A small pill stays at the top centre while Instagram or YouTube is open. Tap it any time to return here.",14f,Color.DKGRAY))
        root.addView(button("Enable automatic counting",black,Color.WHITE) { startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)) },LinearLayout.LayoutParams(-1,dp(50)).apply { topMargin=dp(12) })
        root.addView(button("Allow top counter overlay",green,black) {
            if (!Settings.canDrawOverlays(this)) startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + packageName)))
            else Toast.makeText(this,"Overlay permission is already enabled",Toast.LENGTH_SHORT).show()
        },LinearLayout.LayoutParams(-1,dp(50)).apply { topMargin=dp(8) })
        root.addView(label("DAILY TARGET",14f,black,true).apply { setPadding(0,dp(22),0,dp(6)) })
        limitInput=EditText(this).apply { inputType=2; setSingleLine(true); setText(limit().toString()); hint="Target reels per day" }
        root.addView(limitInput,LinearLayout.LayoutParams(-1,dp(52)))
        root.addView(button("Save target",green,black) {
            val n=limitInput.text.toString().toIntOrNull()
            if (n == null || n !in 1..10000) limitInput.error="Enter a target from 1 to 10,000"
            else { prefs.edit().putInt("limit",n).apply(); Toast.makeText(this,"Daily target saved",Toast.LENGTH_SHORT).show(); refresh() }
        },LinearLayout.LayoutParams(-1,dp(48)).apply { topMargin=dp(6) })
        root.addView(label("Accuracy note: Android does not provide a direct per-reel counter. Reel Guard estimates reel changes from accessibility scroll/content events, so it can miss or occasionally double-count items depending on app version. It does not save screen text or send data off your phone.",12f,muted).apply { setPadding(0,dp(20),0,0) })
        setContentView(ScrollView(this).apply { addView(root) }); refresh()
    }
    private fun zoneRow(title: String, subtitle: String, color: Int) = LinearLayout(this).apply {
        orientation=0; gravity=Gravity.CENTER_VERTICAL; setPadding(dp(12),dp(12),dp(12),dp(12)); background=bg(Color.WHITE,14)
        addView(TextView(this@MainActivity).apply { text="●"; textSize=18f; setTextColor(color) },LinearLayout.LayoutParams(dp(28),-2))
        val col=LinearLayout(this@MainActivity).apply { orientation=1; addView(label(title,14f,Color.BLACK,true)); addView(label(subtitle,12f,Color.DKGRAY)) }
        addView(col,LinearLayout.LayoutParams(0,-2,1f))
    }
    private fun button(title:String,color:Int,fg:Int,action:()->Unit)=Button(this).apply {
        text=title; textSize=14f; setTextColor(fg); background=bg(color,20); isAllCaps=false; setOnClickListener { action() }
    }
    private fun refresh() {
        if (!::countView.isInitialized) return
        val n=count()
        countView.text=n.toString()
        val color=when { n<=200 -> Color.rgb(35,190,115); n<=400 -> Color.rgb(245,174,45); else -> Color.rgb(235,75,75) }
        zoneView.setTextColor(color)
        zoneView.text=when { n<=200 -> "GREEN ZONE · STEADY"; n<=400 -> "AMBER ZONE · TAKE A PAUSE"; n<=700 -> "EXTREME · TIME TO STOP"; else -> "VERY HIGH · PUT THE PHONE DOWN" }
    }
}