package io.github.okafke.aapi.plugin

import com.android.build.api.instrumentation.ClassContext
import com.android.build.api.instrumentation.ClassData
import io.github.okafke.aapi.plugin.test.TestClass
import org.gradle.api.Transformer
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Opcodes
import java.io.File
import java.nio.file.Paths
import java.util.function.BiFunction

fun main() {
    val context = InstrumentationContext(Paths.get("build", "test").toFile(), Paths.get("build", "test").toFile())
    val parameters = object: AnnotationProcessingClassVisitorFactory.Parameters {
        override val dir: Property<File>
            get() = DummyProperty(context.dir)
        override val cacheDir: Property<File>
            get() = DummyProperty(context.cacheDir)
        override val context: Property<InstrumentationContext>
            get() = DummyProperty(context)

    }

    val classVisitorFactory = object: AnnotationProcessingClassVisitorFactory() {
        override val instrumentationContext: com.android.build.api.instrumentation.InstrumentationContext
            get() = object: com.android.build.api.instrumentation.InstrumentationContext {
                override val apiVersion: Property<Int>
                    get() = DummyProperty(33)
            }
        override val parameters: Property<Parameters>
            get() = DummyProperty(parameters)
    }

    TestClass::class.java.classLoader.getResourceAsStream(TestClass::class.java.name.replace(".", "/") + ".class").use {
        val cr = ClassReader(it)
        val nextCv = object: ClassVisitor(Opcodes.ASM9) {}
        val classContext = object: ClassContext {
            override val currentClassData: ClassData
                get() = TODO("Not yet implemented")

            override fun loadClassData(className: String): ClassData? {
                TODO("Not yet implemented")
            }
        }

        val cv = classVisitorFactory.createClassVisitor(classContext, nextCv)
        cr.accept(cv, 0)
    }
}

class InstrumentationTest {
    fun main() {

    }
}

class DummyProperty<T>(val value: T): Property<T> {
    override fun get(): T {
        return value
    }

    override fun getOrNull(): T? {
        TODO("Not yet implemented")
    }

    override fun isPresent(): Boolean {
        TODO("Not yet implemented")
    }

    @Deprecated("Deprecated in Java")
    override fun forUseAtConfigurationTime(): Provider<T> {
        TODO("Not yet implemented")
    }

    override fun finalizeValue() {
        TODO("Not yet implemented")
    }

    override fun finalizeValueOnRead() {
        TODO("Not yet implemented")
    }

    override fun disallowChanges() {
        TODO("Not yet implemented")
    }

    override fun disallowUnsafeRead() {
        TODO("Not yet implemented")
    }

    override fun convention(p0: Provider<out T>): Property<T> {
        TODO("Not yet implemented")
    }

    override fun convention(p0: T?): Property<T> {
        TODO("Not yet implemented")
    }

    override fun value(p0: Provider<out T>): Property<T> {
        TODO("Not yet implemented")
    }

    override fun value(p0: T?): Property<T> {
        TODO("Not yet implemented")
    }

    override fun set(p0: Provider<out T>) {
        TODO("Not yet implemented")
    }

    override fun set(p0: T?) {
        TODO("Not yet implemented")
    }

    override fun <U : Any?, R : Any?> zip(
        p0: Provider<U>,
        p1: BiFunction<in T, in U, out R>
    ): Provider<R> {
        TODO("Not yet implemented")
    }

    override fun orElse(p0: Provider<out T>): Provider<T> {
        TODO("Not yet implemented")
    }

    override fun orElse(p0: T): Provider<T> {
        TODO("Not yet implemented")
    }

    override fun <S : Any?> flatMap(p0: Transformer<out Provider<out S>, in T>): Provider<S> {
        TODO("Not yet implemented")
    }

    override fun <S : Any?> map(p0: Transformer<out S, in T>): Provider<S> {
        TODO("Not yet implemented")
    }

    override fun getOrElse(p0: T): T {
        TODO("Not yet implemented")
    }
}
