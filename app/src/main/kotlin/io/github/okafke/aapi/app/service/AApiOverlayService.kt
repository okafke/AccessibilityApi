package io.github.okafke.aapi.app.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.InputMethod
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import android.view.KeyEvent.ACTION_DOWN
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import androidx.preference.PreferenceManager
import io.github.okafke.aapi.aidl.Node
import io.github.okafke.aapi.app.AppManager
import io.github.okafke.aapi.app.R
import io.github.okafke.aapi.app.aidl.NavigationTreeService
import io.github.okafke.aapi.app.input.InputService
import io.github.okafke.aapi.app.input.InputServiceImpl
import io.github.okafke.aapi.app.overlay.ButtonService
import io.github.okafke.aapi.app.overlay.Overlay
import io.github.okafke.aapi.app.overlay.OverlayUpdateService
import io.github.okafke.aapi.app.overlay.TreeListener
import io.github.okafke.aapi.app.tree.DefaultTreeMapper
import io.github.okafke.aapi.app.tree.TreeHolder
import io.github.okafke.aapi.app.tree.TreeMapperStrict
import io.github.okafke.aapi.app.tree.TreeMapperWIthStrictBack
import io.github.okafke.aapi.app.tree.permissions.AllowNode
import io.github.okafke.aapi.app.tree.permissions.DenyNode
import io.github.okafke.aapi.app.util.AApiUtil
import io.github.okafke.aapi.app.util.isEnabled


class AApiOverlayService : AccessibilityService(), TreeListener {
    val threadHandler = Handler(Looper.getMainLooper())
    val treeHolder = TreeHolder(DefaultTreeMapper())
    val buttonService = ButtonService()
    val overlay = Overlay.Holder()

    lateinit var overlayUpdateService: OverlayUpdateService
    lateinit var inputService: InputService

    private var inAllowDeny = false

    init {
        instance = this
    }

    companion object {
        var instance: AApiOverlayService? = null
        val PERMISSION_IDS = mapOf(
            Pair("com.android.permissioncontroller:id/permission_allow_button",
                "com.android.permissioncontroller:id/permission_deny_button")
        )
    }

    override fun onCreate() {
        println("AAPI onCreate() called!")
        overlayUpdateService = OverlayUpdateService(applicationContext, threadHandler, overlay)
        inputService = InputServiceImpl(applicationContext, treeHolder, overlayUpdateService)
        NavigationTreeService.inputAmount = AApiUtil.getInputs(applicationContext)
        setMapper()
    }

    override fun onServiceConnected() {
        println("AAPI onServiceConnected() called!")
        val intent = Intent(AApiConnectionService::class.java.name)
        intent.action = "accessibilityapi.tree"
        intent.setPackage("io.github.okafke.aapi.app")
        startService(intent)

        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        preferences.registerOnSharedPreferenceChangeListener { _, _ ->
            NavigationTreeService.inputAmount = AApiUtil.getInputs(applicationContext)
            inputService = InputServiceImpl(applicationContext, treeHolder, overlayUpdateService)
            setMapper()
            if (isEnabled()) {
                addView()
                onNewTree(treeHolder.currentTree)
            }
        }

        addView()

        AppManager.init(applicationContext)
        onNewTree(AppManager.getTree(inputService, DefaultTreeMapper()))
        /*val node = Node(
            "Music Player",
            arrayOf(R.drawable.baseline_music_note_24),
            arrayOf(applicationContext.packageName),
            "dummy",
            Node.INVALID_ID,
            emptyArray())
        node.callbackInApi = Runnable {
            val musicIntent = packageManager.getLaunchIntentForPackage("code.name.monkey.retromusic.debug")
            if (musicIntent != null) {
                startActivity(musicIntent)
            }
        }*/
        // onNewTree(arrayOf(node))

        if (!NavigationTreeService.hasListener(this)) {
            NavigationTreeService.addLocalListener(this)
        }
    }

    override fun onNewTree(tree: Array<Node>) {
        println("Received new tree ${tree.contentToString()}")

        val pref = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val addAApiSettings = pref.getBoolean(applicationContext.getString(R.string.add_aapi_settings_key), false)
        if (addAApiSettings) {
            val newTree = ArrayList(tree.asList())
            if (newTree.size == inputService.inputs.size) {

            } else {

            }
        }


        treeHolder.setTree(tree, inputService.inputs)
        overlayUpdateService.update(treeHolder.currentNode)
    }

    private fun getSettingsNode(): Node {
        val off = Node(
            "Off",
            arrayOf(R.drawable.power),
            arrayOf(applicationContext.packageName),
            "dummy",
            Node.INVALID_ID,
            emptyArray())

        off.callbackInApi = Runnable {
            disableSelf()
        }

        val apps = Node(
            "Apps",
            arrayOf(R.drawable.show_apps),
            arrayOf(applicationContext.packageName),
            "dummy",
            Node.INVALID_ID,
            emptyArray())

        apps.callbackInApi = Runnable {
            val startMain = Intent(Intent.ACTION_MAIN)
            startMain.addCategory(Intent.CATEGORY_HOME)
            startMain.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            applicationContext.startActivity(startMain)

            AppManager.init(applicationContext.applicationContext)
            onNewTree(AppManager.getTree(inputService, DefaultTreeMapper()))
        }

        val inputs = Node(
            "Inputs",
            arrayOf(R.drawable.gamepad),
            arrayOf(applicationContext.packageName),
            "dummy",
            Node.INVALID_ID,
            emptyArray())

        inputs.callbackInApi = Runnable {
            threadHandler.post {
                overlay.overlay?.overlayElements?.forEach { element ->
                    OverlayUpdateService.update(applicationContext, element, element.input.getAsNode(), false)
                }

                threadHandler.postDelayed({
                    overlay.overlay?.overlayElements?.forEach { element ->
                        OverlayUpdateService.update(applicationContext, element, element.node, false)
                    }
                }, 5_000)
            }
        }

        val children: Array<Node> = if (inputService.inputs.size == 2) {
            val nestedCategory = Node("Off, Inputs",
                arrayOf(R.drawable.aapi_settings),
                arrayOf(applicationContext.packageName),
                "dummy",
                Node.INVALID_ID,
                arrayOf(off, inputs))

            arrayOf(nestedCategory, apps)
        } else {
            arrayOf(apps, inputs, off)
        }


        return Node("AApi",
            arrayOf(R.drawable.aapi_settings),
            arrayOf(applicationContext.packageName),
            "dummy",
            Node.INVALID_ID,
            children)
    }

    private fun setMapper() {
        val pref = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val backAction = pref.getBoolean(applicationContext.getString(R.string.back_action_key), false)
        if (backAction) {
            // treeHolder.mapper = TreeMapperWithBackAction()
            treeHolder.mapper = TreeMapperWIthStrictBack()
        } else {
            // treeHolder.mapper = DefaultTreeMapper()
            treeHolder.mapper = TreeMapperStrict()
        }

        /*val offAction = pref.getBoolean(applicationContext.getString(R.string.off_action_key), false)
        println("OffAction $offAction")
        if (offAction) {
            treeHolder.mapper = TreeMapperWithOffAction(this, treeHolder)
        }*/
    }

    private fun addView() {
        println("Scheduling addView...")
        threadHandler.post {
            println("addView called!")
            val wm = getSystemService(WINDOW_SERVICE) as WindowManager
            if (overlay.overlay != null) {
                println("View did exist, removing old view")
                wm.removeView(overlay.overlay)
            }

            overlay.overlay = Overlay(this)
            buttonService.addOffButton(this, overlay.overlay!!)

            buttonService.addViewInputsButton(this, overlay.overlay!!)

            buttonService.addAppsButton(this, overlay.overlay!!)

            buttonService.addButtons(this, inputService, overlay.overlay!!)
        }
    }

    override fun onKeyEvent(event: KeyEvent): Boolean {
        if (event.action == ACTION_DOWN) {
            overlay.overlay?.overlayElements?.forEach {
                if (it.keyCode == event.keyCode) {
                    inputService.onInput(it.input)
                }
            }

            return true
        }

        return false
    }

    override fun onCreateInputMethod(): InputMethod {
        return super.onCreateInputMethod()
    }

    // AccessibilityService methods

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        // TODO: timeout between permissions!!!!!
        if ("com.google.android.permissioncontroller" != event.packageName) {
            inAllowDeny = false
            return
        }

        if (inAllowDeny) {
            return
        }

        if (AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED == event.eventType
            || AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED == event.eventType) {
            println(event.source)
            val nodeInfo = event.source ?:let {
                println("source null $rootInActiveWindow")
                rootInActiveWindow
            } ?: return

            for (id in PERMISSION_IDS) {
                val accept = nodeInfo.findAccessibilityNodeInfosByViewId(id.key)
                println("found accept $accept")
                if (accept.isNotEmpty()) {
                    val deny = nodeInfo.findAccessibilityNodeInfosByViewId(id.value)
                    println("found deny $deny")
                    if (deny.isNotEmpty()) {
                        inAllowDeny = true
                        onNewTree(arrayOf(
                            AllowNode(treeHolder.currentTree, accept, this),
                            DenyNode(treeHolder.currentTree, deny, this)
                        ))

                        break
                    }
                }
            }
        }

        //TODO("Not yet implemented")
    }

    override fun onInterrupt() {
        //TODO("Not yet implemented")
    }

}