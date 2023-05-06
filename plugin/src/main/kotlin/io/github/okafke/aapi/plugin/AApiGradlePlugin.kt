package io.github.okafke.aapi.plugin

import com.android.build.api.instrumentation.FramesComputationMode
import com.android.build.api.instrumentation.InstrumentationScope
import com.android.build.api.variant.AndroidComponentsExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

@Suppress("unused")
class AApiGradlePlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val extension = project.extensions.create("aapi", AApiExtension::class.java)
        val dir = extension.getDir(project)
        val cacheDir = extension.getCacheDir(project)
        dir.mkdirs()
        cacheDir.mkdirs()
        val context = InstrumentationContext(dir, cacheDir)
        project.tasks.register("generateKeyboardTree", GenerateKeyboardTreeTask::class.java, context)
        project.tasks.register("generateKeyboardTreeBetter", BetterGenerateKeyboardTreeTask::class.java, context)
        project.pluginManager.withPlugin("com.android.application") {
            val androidComponentsExtension =
                project.extensions.getByType(AndroidComponentsExtension::class.java)
            println("AndroidComponentsExtension found successfuly")
            androidComponentsExtension.onVariants { variant ->
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