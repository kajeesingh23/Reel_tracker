package com.kajeesingh.reelguard

import android.app.Activity
import android.app.AppOpsManager
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.widget.*
import java.util.Calendar

class MainActivity : Activity() {
 private val prefs by lazy { getSharedPreferences("reel_guard", Context.MODE_PRIVATE) }
 private lateinit var countView: TextView
 private lateinit var meterView: TextView
 private lateinit var bar: ProgressBar
 private lateinit var usageView: TextView
 private lateinit var limitInput: EditText
 private fun day() = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).format(java.util.Date())
 private fun count(): Int { if (prefs.getString("day", "") != day()) prefs.edit().putString("day", day()).putInt("count", 0).apply(); return prefs.getInt("count", 0) }
 private fun limit() = UsageLogic.clampLimit(prefs.getInt("limit", 30))
 private fun dp(n: Int) = (n * resources.displayMetrics.density).toInt()
 private fun bg(color: Int, radius: Int) = GradientDrawable().apply { setColor(color); cornerRadius = dp(radius).toFloat() }
 private fun label(s: String, size: Float, color: Int, bold: Boolean = false) = TextView(this).apply { text=s; textSize=size; setTextColor(color); if (bold) setTypeface(null, android.graphics.Typeface.BOLD) }
 override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState); render() }
 override fun onResume() { super.onResume(); if (::countView.isInitialized) refresh() }
 private fun render() {
  val black=Color.rgb(18,18,20); val green=Color.rgb(37,212,134); val muted=Color.rgb(150,150,155)
  val root=LinearLayout(this).apply { orientation=1; setPadding(dp(18),dp(28),dp(18),dp(20)); setBackgroundColor(Color.rgb(242,242,242)) }
  root.addView(label("REEL GUARD",22f,black,true))
  root.addView(label("A little more control over your scroll.",14f,Color.DKGRAY).apply { setPadding(0,dp(4),0,dp(22)) })
  val pill=LinearLayout(this).apply { orientation=0; gravity=Gravity.CENTER_VERTICAL; setPadding(dp(16),dp(16),dp(16),dp(16)); background=bg(black,48) }
  pill.addView(label("🔥",28f,Color.WHITE),LinearLayout.LayoutParams(dp(44),dp(48)))
  val left=LinearLayout(this).apply { orientation=1 }
  left.addView(label("STREAK",13f,muted,true))
  countView=label("0 SESSIONS",20f,Color.WHITE,true); left.addView(countView); left.addView(label("TODAY'S REELS / SHORTS",10f,muted))
  pill.addView(left,LinearLayout.LayoutParams(0,-2,1f))
  val right=LinearLayout(this).apply { orientation=1; setPadding(dp(10),0,0,0) }
  right.addView(label("DAILY LIMIT",12f,muted))
  meterView=label("0%",22f,Color.WHITE,true); right.addView(meterView)
  bar=ProgressBar(this,null,android.R.attr.progressBarStyleHorizontal).apply { max=100; progressTintList=android.content.res.ColorStateList.valueOf(green); progressBackgroundTintList=android.content.res.ColorStateList.valueOf(Color.rgb(39,66,54)) }
  right.addView(bar,LinearLayout.LayoutParams(dp(100),dp(8)).apply { topMargin=dp(8) }); pill.addView(right); root.addView(pill)
  root.addView(label("SESSION COUNTER",13f,black,true).apply { setPadding(0,dp(20),0,dp(8)) })
  val actions=LinearLayout(this).apply { orientation=0; gravity=Gravity.CENTER }
  actions.addView(button("−",Color.LTGRAY,black){ update(-1) },LinearLayout.LayoutParams(0,dp(48),1f).apply { marginEnd=dp(5) })
  actions.addView(button("+ Watched one",green,black){ update(1) },LinearLayout.LayoutParams(0,dp(48),2f).apply { marginHorizontalCompat(dp(5)) })
  actions.addView(button("+5",Color.LTGRAY,black){ update(5) },LinearLayout.LayoutParams(0,dp(48),1f).apply { marginStart=dp(5) })
  root.addView(actions)
  root.addView(label("APP USAGE TODAY",13f,black,true).apply { setPadding(0,dp(20),0,dp(8)) })
  usageView=label("Instagram: grant Usage Access\nYouTube: grant Usage Access",14f,Color.DKGRAY).apply { setPadding(dp(14),dp(14),dp(14),dp(14)); background=bg(Color.WHITE,16) }
  root.addView(usageView,LinearLayout.LayoutParams(-1,-2))
  root.addView(button("Grant Usage Access",black,Color.WHITE){ startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)) },LinearLayout.LayoutParams(-1,dp(48)).apply { topMargin=dp(12) })
  root.addView(label("SET DAILY SESSION LIMIT",13f,black,true).apply { setPadding(0,dp(20),0,dp(8)) })
  val row=LinearLayout(this).apply { orientation=0; gravity=Gravity.CENTER_VERTICAL }
  limitInput=EditText(this).apply { inputType=2; setSingleLine(true); setText(limit().toString()); hint="1–1440" }
  row.addView(limitInput,LinearLayout.LayoutParams(0,dp(50),1f))
  row.addView(button("Save limit",green,black){ val n=limitInput.text.toString().toIntOrNull(); if(n==null || n !in 1..1440) limitInput.error="Enter 1–1440" else { prefs.edit().putInt("limit",n).apply(); refresh(); Toast.makeText(this,"Limit saved",Toast.LENGTH_SHORT).show() } },LinearLayout.LayoutParams(dp(120),dp(48)))
  root.addView(row)
  root.addView(label("Count is manual. Android reports app time, not individual reel swipes. Data stays on this device.",12f,Color.DKGRAY).apply { setPadding(0,dp(18),0,0) })
  setContentView(ScrollView(this).apply { addView(root) }); refresh()
 }
 private fun LinearLayout.LayoutParams.marginHorizontalCompat(value: Int) { marginStart=value; marginEnd=value }
 private fun button(title:String,color:Int,fg:Int,action:()->Unit)=Button(this).apply { text=title; textSize=13f; setTextColor(fg); background=bg(color,24); isAllCaps=false; setOnClickListener { action() } }
 private fun update(delta:Int) { val old=count(); val next=if(delta<0) (old+delta).coerceAtLeast(0) else UsageLogic.safeIncrement(old,delta); prefs.edit().putString("day",day()).putInt("count",next).apply(); refresh() }
 private fun hasAccess():Boolean { val ops=getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager; @Suppress("DEPRECATION") val mode=ops.checkOpNoThrow("android:get_usage_stats",android.os.Process.myUid(),packageName); return mode==AppOpsManager.MODE_ALLOWED }
 private fun minutes(pkg:String):Long? { if(!hasAccess()) return null; return try { val c=Calendar.getInstance(); c.set(Calendar.HOUR_OF_DAY,0); c.set(Calendar.MINUTE,0); c.set(Calendar.SECOND,0); c.set(Calendar.MILLISECOND,0); val stats=(getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager).queryUsageStats(UsageStatsManager.INTERVAL_DAILY,c.timeInMillis,System.currentTimeMillis()); stats.filter{it.packageName==pkg}.sumOf{it.totalTimeInForeground}/60000L } catch (_:Exception) { null } }
 private fun fmt(v:Long?)=if(v==null) "Grant Usage Access" else "$v min"
 private fun refresh() { if(!::countView.isInitialized) return; val n=count(); val l=limit(); val p=UsageLogic.percent(n,l); countView.text="$n SESSIONS"; meterView.text="$p%"; bar.progress=p; val status=when { n>l -> "⚠ Limit exceeded by ${n-l}. Take a break."; p>=80 -> "Almost at your daily limit."; else -> "You're within your daily limit." }; title = "Reel Guard"; usageView.text="Instagram: ${fmt(minutes("com.instagram.android"))}\nYouTube: ${fmt(minutes("com.google.android.youtube"))}"; meterView.contentDescription=status }
}
