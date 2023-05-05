package io.github.okafke.aapi.gradle

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.ClassNode

abstract class NodeClassVisitor(private val next: ClassVisitor): ClassVisitor(Opcodes.ASM7, ClassNode()) {
    abstract fun process(node: ClassNode)

    override fun visit(
        version: Int,
        access: Int,
        name: String?,
        signature: String?,
        superName: String?,
        interfaces: Array<out String>?
    ) {
        super.visit(version, access, name, signature, superName, interfaces)
    }

    override fun visitEnd() {
        super.visitEnd()
        val node = super.cv as ClassNode
        process(node)
        node.accept(next)
    }

}