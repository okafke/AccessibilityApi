package io.github.okafke.aapi.app.overlay

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.view.KeyEvent
import android.view.View
import android.view.ViewTreeObserver
import android.view.WindowManager
import android.widget.Button
import androidx.core.content.ContextCompat.startActivity
import androidx.preference.PreferenceManager
import io.github.okafke.aapi.aidl.Node
import io.github.okafke.aapi.app.AppManager
import io.github.okafke.aapi.app.R
import io.github.okafke.aapi.app.input.InputService
import io.github.okafke.aapi.app.service.AApiOverlayService
import io.github.okafke.aapi.app.tree.DefaultTreeMapper
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference


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
            button.layoutParams = lp
            button.isAllCaps = false
            button.text = "$input"

            val overlayElement = OverlayElement(button, input)
            elements.add(overlayElement)

            if (viewTreeObserver.isAlive) {
                viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        if (viewTreeObserver.isAlive) {
                            viewTreeObserver.removeOnGlobalLayoutListener(this)
                        }

                        positionButton(index, button, overlay, overlayElement)
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

    fun addAppsButton(context: AApiOverlayService, overlay: Overlay) {
        addButton(context, overlay, "Apps", 0xFFFFFF00, 5, R.drawable.show_apps) {
            val startMain = Intent(Intent.ACTION_MAIN)
            startMain.addCategory(Intent.CATEGORY_HOME)
            startMain.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(startMain)

            AppManager.init(context.applicationContext)
            context.onNewTree(AppManager.getTree(context.inputService, DefaultTreeMapper()))
        }
    }

    fun addViewInputsButton(context: AApiOverlayService, overlay: Overlay) {
        val isShowingInputs = AtomicBoolean()
        addButton(context, overlay, "Inputs", 0xFF00FFFF, 6, R.drawable.gamepad) {
            isShowingInputs.set(!isShowingInputs.get())
            if (isShowingInputs.get()) {
                overlay.overlayElements.forEach { element ->
                    OverlayUpdateService.update(context, element, element.input.getAsNode(), false)
                }
            } else {
                overlay.overlayElements.forEach { element ->
                    OverlayUpdateService.update(context, element, element.node, false)
                }
            }
        }
    }

    fun addOffButton(context: AApiOverlayService, overlay: Overlay) {
        addButton(context, overlay, "Off", 0xFFFF0000, 7, R.drawable.power) {
            println("Off button clicked")
            context.disableSelf()
        }
    }

    fun addButton(context: AApiOverlayService, overlay: Overlay, name: String, color: Long, position: Int, drawable: Int, listener: View.OnClickListener) {
        val button = Button(context)
        val lp = WindowManager.LayoutParams()
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        button.layoutParams = lp
        button.isAllCaps = false
        button.text = name
        //button.background.colorFilter = LightingColorFilter(0xFFFFFFFF.toInt(), 0xFFAA0000.toInt())
        button.background.colorFilter = PorterDuffColorFilter(color.toInt(), PorterDuff.Mode.MULTIPLY)
        button.setCompoundDrawablesWithIntrinsicBounds(0, drawable, 0, 0)

        val viewTreeObserver = overlay.viewTreeObserver
        if (viewTreeObserver.isAlive) {
            viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    if (viewTreeObserver.isAlive) {
                        viewTreeObserver.removeOnGlobalLayoutListener(this)
                    }

                    positionButton(position, button, overlay, null)
                }
            })
        }


        button.setOnClickListener(listener)
        overlay.addView(button)
    }

    fun positionButton(index: Int, button: View, overlay: View, overlayElement: OverlayElement?) {
        if (!RANGE.contains(index)) {
            throw IndexOutOfBoundsException("$index not in 0-7")
        }

        when (index) {
            0 -> { // middle top
                button.x = overlay.width / 2.0f - button.width / 2.0f

                val preference = PreferenceManager.getDefaultSharedPreferences(overlay.context)
                val y = preference.getInt(overlay.context.getString(R.string.camera_y_key), 50)
                button.y = y.toFloat()
                overlayElement?.keyCode = KeyEvent.KEYCODE_W
            }
            1 -> { // middle left
                button.x = 0.0f
                button.y = overlay.height / 2.0f - button.height / 2.0f
                overlayElement?.keyCode = KeyEvent.KEYCODE_A
            }
            2 -> { // middle bottom
                button.x = overlay.width / 2.0f - button.width / 2.0f
                button.y = overlay.height - button.height.toFloat()
                overlayElement?.keyCode = KeyEvent.KEYCODE_S
            }
            3 -> { // middle right
                button.x = overlay.width.toFloat() - button.width
                button.y = overlay.height / 2.0f - button.height / 2.0f
                overlayElement?.keyCode = KeyEvent.KEYCODE_D
            }
            4 -> { // top left
                button.x = 0.0f
                button.y = 0.0f
                overlayElement?.keyCode = KeyEvent.KEYCODE_Q
            }
            5 -> { // bottom left
                button.x = 0.0f
                button.y = overlay.height.toFloat() - button.height
                overlayElement?.keyCode = KeyEvent.KEYCODE_Y
            }
            6 -> { // bottom right
                button.x = overlay.width - button.width.toFloat()
                button.y = overlay.height.toFloat() - button.height
                overlayElement?.keyCode = KeyEvent.KEYCODE_X
            }
            7 -> { // top right
                button.x = overlay.width.toFloat() - button.width.toFloat()
                button.y = 0.0f
                overlayElement?.keyCode = KeyEvent.KEYCODE_E
            }
        }
    }

}