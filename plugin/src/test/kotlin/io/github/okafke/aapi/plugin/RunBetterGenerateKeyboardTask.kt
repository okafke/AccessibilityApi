package io.github.okafke.aapi.plugin

import org.junit.jupiter.api.Test
import java.io.File

class RunBetterGenerateKeyboardTask {
    @Test
    fun testBetterGenerateKeyboardTask() {
        BetterGenerateKeyboardTreeTask.runStatic(InstrumentationContext(File("build/testBetterGenerateKeyboardTask"), File("build/testBetterGenerateKeyboardTask")))
    }
}