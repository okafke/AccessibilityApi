package io.github.okafke.aapi.gradle

import com.android.build.api.instrumentation.AsmClassVisitorFactory
import com.android.build.api.instrumentation.ClassContext
import com.android.build.api.instrumentation.ClassData
import com.android.build.api.instrumentation.InstrumentationParameters
import io.github.okafke.aapi.client.json.instances.Instances
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Internal
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Opcodes.*
import org.objectweb.asm.Type
import org.objectweb.asm.tree.*
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.lang.reflect.Modifier


abstract class AnnotationProcessingClassVisitorFactory :
    AsmClassVisitorFactory<AnnotationProcessingClassVisitorFactory.Parameters> {
    interface Parameters : InstrumentationParameters {
        @get:Internal
        val dir: Property<File>
        @get:Internal
        val context: Property<InstrumentationContext>
    }

    override fun createClassVisitor(
        classContext: ClassContext,
        nextClassVisitor: ClassVisitor
    ): ClassVisitor {
        println("Visiting ${classContext.currentClassData.className}")
        return object: NodeClassVisitor(nextClassVisitor) {
            override fun process(node: ClassNode) {
                var updated = false
                for (annotation in node.getAllAnnotations()) {
                    if (Constants.categoryDesc == annotation.desc) {
                        val name = annotation.values[1] as String
                        val drawableId = annotation.values[3] as String
                        @Suppress("UNCHECKED_CAST")
                        val children = annotation.values[5] as java.util.ArrayList<String>
                        val category = Category(name, drawableId, LinkedHashSet(children))
                        parameters.get().context.get().addCategory(category)
                        updated = true
                        println("Category: $category")
                    }

                    if (Constants.treeDesc == annotation.desc) {
                        val name = annotation.values[1] as String
                        @Suppress("UNCHECKED_CAST")
                        val children = annotation.values[3] as java.util.ArrayList<String>
                        val tree = Tree(name, LinkedHashSet(children))
                        parameters.get().context.get().addTree(tree)
                        updated = true
                        println("Tree: $tree")
                        injectTree(tree, node)
                    }
                }

                for (method in node.methods) {
                    for (annotation in method.getAllAnnotations()) {
                        if (Constants.actionDesc == annotation.desc) {
                            val name = annotation.values[1] as String
                            val drawableId = annotation.values[3] as String
                            val adapter = processMethodNode(name, node, method)
                            val action = Action(name, drawableId, adapter)
                            parameters.get().context.get().addAction(action)
                            updated = true
                            println("Action: $action")
                        }

                        if (Constants.contextDesc == annotation.desc) {
                            println("Found ContextProvider on ${method.name}")
                            val adapter = Adapter(
                                "context",
                                node.name.replace("/", "."),
                                method.name)

                            val file = File(parameters.get().dir.get(), "contextprovider.json")
                            OutputStreamWriter(FileOutputStream(file)).use { writer ->
                                Constants.GSON.toJson(adapter, writer)
                            }
                        }
                    }
                }

                /*for (field in node.fields) {
                    for (annotation in field.getAllAnnotations()) {
                        if (Constants.actionDesc == annotation.desc) {
                            val name = annotation.values[1] as String
                            val drawableId = annotation.values[3] as String
                            val action = Action(name, drawableId)
                            parameters.get().context.get().actions.add(action)
                            updated = true
                            println("Action: $action")
                        }
                    }
                }*/

                if (updated) {
                    parameters.get().context.get().update()
                }
            }
        }
    }

    // TODO: this should be configurable
    override fun isInstrumentable(classData: ClassData): Boolean {
        // return true
        return !classData.className.startsWith("androidx.")
                && !classData.className.startsWith("android.")
                && !classData.className.startsWith("kotlinx.")
                && !classData.className.startsWith("kotlin.")
                && !classData.className.startsWith("com.google.android.")
    }

    private fun injectTree(tree: Tree, cn: ClassNode) {
        for (method in cn.methods) {
            // Activity onCreated
            if (method.name == "onCreate" && method.desc == "(Landroid/os/Bundle;)V"
                // Fragment onViewCreated
                || method.name == "onViewCreated" && method.desc == "(Landroid/view/View;Landroid/os/Bundle;)V") {
                injectTree(tree, cn, method)
                return
            }
        }

        for (method in cn.methods) {
            // Activity onCreated
            if (method.name == "<init>" && method.desc.endsWith(")V")) {
                injectTree(tree, cn, method)
            }
        }
    }

    private fun injectTree(tree: Tree, cn: ClassNode, method: MethodNode) {
        val il = InsnList()
        il.add(LdcInsnNode(tree.name))
        il.add(MethodInsnNode(INVOKESTATIC, "me/okafke/accessibilityapi/json/ServiceHolder", "setTree", "(Ljava/lang/String;)V"))
        injectAtReturns(method.instructions, il)
        println("Injected Tree ${tree.name} into ${cn.name} ${method.name}")
    }

    private fun processMethodNode(name: String, cn: ClassNode, method: MethodNode): Adapter {
        val clazzName = cn.name.replace("/", ".")
        val uid = getUid(name)
        if (!Modifier.isStatic(method.access)) {
            println("$name method ${method.name} in ${cn.name} is not static, injecting instance call")
            for (mn in cn.methods) {
                println("Checking ${cn.name}.${mn.name}${mn.desc}!")
                if ("<init>" == mn.name && mn.desc.endsWith(")V")) {
                    println("Found constructor of ${cn.name}!")
                    val il = injectInstanceCall(uid, InsnList())
                    injectAtReturns(mn.instructions, il)
                }
            }
        }

        return Adapter(uid, clazzName, method.name)
    }

    private fun injectAtReturns(instructions: InsnList, il: InsnList) {
        var insnNode = instructions.first
        while (insnNode != null) {
            if (insnNode.opcode == RETURN) {
                instructions.insertBefore(insnNode, il)
            }

            insnNode = insnNode.next
        }
    }

    private fun injectInstanceCall(uid: String, il: InsnList): InsnList {
        il.add(FieldInsnNode(GETSTATIC, Type.getInternalName(Instances::class.java), "INSTANCE", Type.getDescriptor(Instances::class.java)))
        il.add(LdcInsnNode(uid))
        il.add(VarInsnNode(ALOAD, 0)) // load 'this'
        il.add(MethodInsnNode(INVOKEVIRTUAL, Type.getInternalName(Instances::class.java), "addInstance", "(Ljava/lang/String;Ljava/lang/Object;)V"))
        return il
    }

    private fun getUid(name: String): String {
        return name // for now we do not do conflict solving
        /*var result = name
        for (action in parameters.get().context.get().actions.values) {
            if (action.adapter.uid == name) {
                result += "1" // ahhhhhhhhhhh
            }
        }

        return result*/
    }

}
