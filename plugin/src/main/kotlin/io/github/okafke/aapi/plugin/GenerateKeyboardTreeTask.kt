package io.github.okafke.aapi.plugin

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.File

open class GenerateKeyboardTreeTask: DefaultTask() {
    companion object {
        const val KEY_BOARD_CLASS = "io.github.okafke.aapi.keyboard.Keyboard"
        const val KEYS = "abcdefghijklmnopqrstuvwxyz1234567890"
    }

    @TaskAction
    fun run() {
        val lowerTree = Tree("lowercase", LinkedHashSet())
        val upperTree = Tree("uppercase", LinkedHashSet())

        val shiftU = Adapter("shift_to_upper", KEY_BOARD_CLASS, "loadUpperCaseTree")
        lowerTree.found["Shift"] = Action("Shift", "shift_key", shiftU)

        val shiftL = Adapter("shift_to_lower", KEY_BOARD_CLASS, "loadLowerCaseTree")
        upperTree.found["Shift"] = Action("Shift", "shift_key", shiftL)

        val goBack = Action("Stop", "stop_typing", Adapter("back", KEY_BOARD_CLASS, "returnToPreviousTree"))
        lowerTree.found["Stop"] = goBack
        upperTree.found["Stop"] = goBack

        val delete = Action("Delete", "delete", Adapter("delete", KEY_BOARD_CLASS, "delete"))
        lowerTree.found["Delete"] = delete
        upperTree.found["Delete"] = delete

        val space = Action("Space", emptyArray(), Adapter("space", KEY_BOARD_CLASS, "typeSpace"))
        lowerTree.found["Space"] = space
        upperTree.found["Space"] = space

        for (char in KEYS) {
            val upper = char.uppercaseChar()
            if (upper != char) {
                val adapter = Adapter("$upper", KEY_BOARD_CLASS, "typeKey")
                val upperNode = Action("$upper", emptyArray(), adapter)
                upperTree.found["$upper"] = upperNode
            }

            val adapter = Adapter("$char", KEY_BOARD_CLASS, "typeKey")
            val action = Action("$char", emptyArray(), adapter)
            lowerTree.found["$char"] = action
        }

        val extension = project.extensions.create("aapi", AApiExtension::class.java)
        val ctx = InstrumentationContext(extension.getDir(project), extension.getCacheDir(project))
        ctx.writeTree(lowerTree)
        ctx.writeTree(upperTree)
    }

}