package io.github.okafke.aapi.app.overlay

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.view.View
import android.view.ViewTreeObserver
import android.view.WindowManager
import android.widget.Button
import androidx.preference.PreferenceManager
import io.github.okafke.aapi.app.R
import io.github.okafke.aapi.app.input.InputService
import io.github.okafke.aapi.app.service.AApiOverlayService


class ButtonService {
    companion object {
        val RANGE = 0..7
    }

    @SuppressLint("SetTextI18n")
    fun addButtons(context: Context, inputService: InputService, overlay: Overlay) {
        val elements = ArrayList<OverlayElement>(inputService.inputs.size)
        val viewTreeObserver = overlay.viewTreeObserver
        for ((index, input) in inputService.inputs.withIndex()) {
            val button = Button(context)

            val lp = WindowManager.LayoutParams()
            lp.width = WindowManager.LayoutParams.WRAP_CONTENT
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT
            button.text
            button.layoutParams = lp
            button.isAllCaps = false
            button.text = "B$index"

            val overlayElement = OverlayElement(button, input)
            elements.add(overlayElement)

            if (viewTreeObserver.isAlive) {
                viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        if (viewTreeObserver.isAlive) {
                            viewTreeObserver.removeOnGlobalLayoutListener(this)
                        }

                        positionButton(index, button, overlay)
                    }
                })
            }

            button.setOnClickListener {
                println("Button $button clicked, notifying input $input")
                inputService.onInput(input)
            }

            overlay.addView(button)
        }

        overlay.overlayElements = elements
    }

    fun addOffButton(context: AApiOverlayService, overlay: Overlay) {
        val button = Button(context)
        val lp = WindowManager.LayoutParams()
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        button.layoutParams = lp
        button.isAllCaps = false
        button.text = "Off"
        //button.background.colorFilter = LightingColorFilter(0xFFFFFFFF.toInt(), 0xFFAA0000.toInt())
        button.background.colorFilter = PorterDuffColorFilter(0xFFFF0000.toInt(), PorterDuff.Mode.MULTIPLY)
        button.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.power, 0, 0)

        val viewTreeObserver = overlay.viewTreeObserver
        if (viewTreeObserver.isAlive) {
            viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    if (viewTreeObserver.isAlive) {
                        viewTreeObserver.removeOnGlobalLayoutListener(this)
                    }

                    positionButton(7, button, overlay)
                }
            })
        }

        button.setOnClickListener {
            println("Off button clicked")
            context.disableSelf()
        }

        overlay.addView(button)
    }

    fun positionButton(index: Int, button: View, overlay: View) {
        if (!RANGE.contains(index)) {
            throw IndexOutOfBoundsException("$index not in 0-7")
        }

        when (index) {
            0 -> { // middle top
                button.x = overlay.width / 2.0f - button.width / 2.0f

                val preference = PreferenceManager.getDefaultSharedPreferences(overlay.context)
                val y = preference.getInt(overlay.context.getString(R.string.camera_y_key), 50)
                button.y = y.toFloat()
            }
            1 -> { // middle left
                button.x = 0.0f
                button.y = overlay.height / 2.0f - button.height / 2.0f
            }
            2 -> { // middle bottom
                button.x = overlay.width / 2.0f - button.width / 2.0f
                button.y = overlay.height - button.height.toFloat()
            }
            3 -> { // middle right
                button.x = overlay.width.toFloat() - button.width
                button.y = overlay.height / 2.0f - button.height / 2.0f
            }
            4 -> { // top left
                button.x = 0.0f
                button.y = 0.0f
            }
            5 -> { // bottom left
                button.x = 0.0f
                button.y = overlay.height.toFloat() - button.height
            }
            6 -> { // bottom right
                button.x = overlay.width - button.width.toFloat()
                button.y = overlay.height.toFloat() - button.height
            }
            7 -> { // top right
                button.x = overlay.width.toFloat() - button.width.toFloat()
                button.y = 0.0f
            }
        }
    }

}