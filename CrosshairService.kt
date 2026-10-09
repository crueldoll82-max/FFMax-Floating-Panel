package com.example.ffpanel

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.core.app.NotificationCompat

class CrosshairService : Service() {

    private lateinit var wm: WindowManager
    private lateinit var view: View

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        startForeground(1, buildNotification())

        wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager

        val size = 60
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val c = Canvas(bitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#00FF00")
            style = Paint.Style.STROKE
            strokeWidth = 4f
        }
        c.drawCircle(size / 2f, size / 2f, size / 4f, paint)
        paint.style = Paint.Style.FILL
        c.drawCircle(size / 2f, size / 2f, 4f, paint)

        val iv = android.widget.ImageView(this)
        iv.setImageBitmap(bitmap)
        view = iv

        val type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        else WindowManager.LayoutParams.TYPE_PHONE

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            type,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                    or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        )
        params.gravity = Gravity.CENTER
        wm.addView(view, params)
    }

    private fun buildNotification(): Notification {
        val chId = "crosshair_ch"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val ch = NotificationChannel(chId, "Crosshair", NotificationManager.IMPORTANCE_LOW)
            (getSystemService(NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(ch)
        }
        return NotificationCompat.Builder(this, chId)
            .setContentTitle("FF Panel")
            .setContentText("Crosshair active")
            .setSmallIcon(android.R.drawable.ic_menu_view)
            .build()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::view.isInitialized && ::wm.isInitialized) {
            try { wm.removeView(view) } catch (_: Exception) {}
        }
    }
}
