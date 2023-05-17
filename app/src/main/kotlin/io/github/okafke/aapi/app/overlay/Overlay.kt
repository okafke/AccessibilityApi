package io.github.okafke.aapi.app.overlay

import android.content.Context
import android.graphics.PixelFormat
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.WindowManager
import android.widget.RelativeLayout
import androidx.preference.PreferenceManager
import io.github.okafke.aapi.app.R

class Overlay(ctx: Context): RelativeLayout(ctx) {
    var overlayElements : List<OverlayElement> = emptyList()

    init {
        val pref = PreferenceManager.getDefaultSharedPreferences(ctx)
        val passthrough = pref.getBoolean(ctx.getString(R.string.overlay_passthrough_key), true)
        println("Making overlay passthrough $passthrough")

        val lp = WindowManager.LayoutParams()
        lp.title = "Overlay"
        lp.format = PixelFormat.TRANSLUCENT
        lp.type = WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY
        lp.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                (if (passthrough) WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE else 0)
        // or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.MATCH_PARENT

        (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).addView(this, lp)
    }

    override fun onGenericMotionEvent(event: MotionEvent?): Boolean {
        //Toast.makeText(context, "onGenericMotionEvent: $event", Toast.LENGTH_SHORT).show()
        return super.onGenericMotionEvent(event)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        //Toast.makeText(context, "onKeyDown: $keyCode $event", Toast.LENGTH_SHORT).show()
        return super.onKeyDown(keyCode, event)
    }

    class Holder {
        var overlay: Overlay? = null
    }
}
