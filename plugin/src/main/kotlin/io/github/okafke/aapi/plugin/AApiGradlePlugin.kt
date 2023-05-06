package io.github.okafke.aapi.plugin

import com.android.build.api.instrumentation.FramesComputationMode
import com.android.build.api.instrumentation.InstrumentationScope
import com.android.build.api.variant.AndroidComponentsExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File

@Suppress("unused")
class AApiGradlePlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val extension = project.extensions.create("aapi", AApiExtension::class.java)
        val dir = extension.getDir().map { d -> d.asFile }.orElse(File(project.rootProject.rootDir, "build/tree")).get()
        val cacheDir = extension.getCacheDir().map { d -> d.asFile }.orElse(File(project.rootProject.rootDir, "build/tree")).get()
        dir.mkdirs()
        cacheDir.mkdirs()
        project.tasks.register("generateKeyboardTree", GenerateKeyboardTreeTask::class.java)
        project.tasks.register("generateKeyboardTreeBetter", BetterGenerateKeyboardTreeTask::class.java)
        project.pluginManager.withPlugin("com.android.application") {
            val androidComponentsExtension =
                project.extensions.getByType(AndroidComponentsExtension::class.java)
            println("AndroidComponentsExtension found successfuly")
            androidComponentsExtension.onVariants { variant ->
                val context = InstrumentationContext(dir, cacheDir)
                variant.instrumentation.setAsmFramesComputationMode(FramesComputationMode.COMPUTE_FRAMES_FOR_INSTRUMENTED_METHODS)
                variant.instrumentation.transformClassesWith(
                    AnnotationProcessingClassVisitorFactory::class.java,
                    InstrumentationScope.ALL // for now, ALL would be better
                ) { params ->
                    params.dir.set(dir)
                    params.cacheDir.set(dir)
                    params.context.set(context)
                }
            }
        }
    }

}