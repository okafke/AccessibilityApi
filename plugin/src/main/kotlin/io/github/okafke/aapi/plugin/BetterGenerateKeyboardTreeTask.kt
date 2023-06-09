package io.github.okafke.aapi.plugin

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.util.function.BiConsumer
import java.util.function.Consumer
import javax.inject.Inject

// TODO: we need an even BetterGenerateKeyboardTreeTask!!!!
open class BetterGenerateKeyboardTreeTask @Inject constructor(private val ctx: InstrumentationContext): DefaultTask() {
    companion object {
        // TODO: if we could include the subproject we could the actual class
        const val KEY_BOARD_CLASS = "io.github.okafke.aapi.client.keyboard.Keyboard"
        const val KEYS = "abcdefghijklmnopqrstuvwxyz"
        const val NUMBERS = "1234567890"

        fun runStatic(ctx: InstrumentationContext) {
            for (degree in 2..8) {
                val keyCategories: MutableList<Node> = ArrayList()
                val numberCategories: MutableList<Node> = ArrayList()
                collectCategories(degree, KEYS, keyCategories)
                collectCategories(degree, NUMBERS, numberCategories)

                val keyCategoryTree = processUntilDone(degree, keyCategories)
                val keyCategory = Category("abc", emptyArray(), LinkedHashSet())
                keyCategoryTree.forEach { keyCategory.found[it.name] = it }

                val numCategoryTree = processUntilDone(degree, numberCategories)
                val numCategory = Category("123", emptyArray(), LinkedHashSet())
                numCategoryTree.forEach { numCategory.found[it.name] = it }

                val moreCategory = moreTree(true)
                val tree = buildTree("lowercase", degree, keyCategory, numCategory, moreCategory)

                ctx.writeTreeLegacy(tree, degree, rearrange = false)

                val moreCategoryUpper = moreTree(false)
                keyCategoryTree.forEach { node -> makeUpperCase(node) }
                val upperTree = buildTree("uppercase", degree, keyCategory, numCategory, moreCategoryUpper)

                ctx.writeTreeLegacy(upperTree, degree, rearrange = false)
            }
        }

        private fun buildTree(name: String, degree: Int, keyCategory: Category, numCategory: Category, moreCategory: Category): Tree {
            val tree = Tree(name, LinkedHashSet())
            when (degree) {
                2 -> {
                    val moreAndNumCategory = Category("123...", "keyboard_more", LinkedHashSet())
                    moreAndNumCategory.found["123"] = numCategory
                    moreAndNumCategory.found["More"] = moreCategory
                    tree.found["ABC"] = keyCategory
                    tree.found["More"] = moreAndNumCategory
                }
                3 -> {
                    tree.found["ABC"] = keyCategory
                    tree.found["More"] = moreCategory
                    tree.found["123"] = numCategory
                }
                else -> {
                    // TODO: make this better
                    // tree.found["ABC"] = keyCategory
                    if (keyCategory.found.size > degree - 2) {
                        println("keyCategory to big: $keyCategory")
                    } else {
                        for (found in keyCategory.found.values) {
                            tree.found[found.name] = found
                        }
                    }

                    tree.found["More"] = moreCategory
                    tree.found["123"] = numCategory

                    assert(tree.found.size <= degree)
                }
            }

            return tree
        }

        private fun moreTree(lower: Boolean): Category {
            val shift = if (lower) {
                Adapter("shift_to_upper", KEY_BOARD_CLASS, "loadUpperCaseTree")
            } else {
                Adapter("shift_to_lower", KEY_BOARD_CLASS, "loadLowerCaseTree")
            }

            val nodes = ArrayList<Node>()
            nodes.add(Action("Shift", "shift_key", shift))
            nodes.add(Action("Stop", "stop_typing", Adapter("back", KEY_BOARD_CLASS, "returnToPreviousTree")))
            nodes.add(Action("Delete", "delete", Adapter("delete", KEY_BOARD_CLASS, "delete")))
            nodes.add(Action("Space", emptyArray(), Adapter("space", KEY_BOARD_CLASS, "typeSpace")))

            val result = Category("More", "keyboard_more", LinkedHashSet())
            nodes.forEach { result.found[it.name] = it }
            return result
        }

        private fun makeUpperCase(node: Node) {
            node.name = node.name.uppercase()
            if (node is Category) {
                node.found.values.forEach { makeUpperCase(it) }
            } else if (node is Action) {
                node.adapter.uid = node.adapter.uid.uppercase()
            }
        }

        private fun processUntilDone(degree: Int, categories: MutableList<Node>): MutableList<Node> {
            var list: MutableList<Node> = ArrayList(categories)
            print("Starting process $degree")
            while (list.size > degree) {
                list = processCategories(degree, list)
            }

            return list
        }

        private fun processCategories(degree: Int, categories: MutableList<Node>): MutableList<Node> {
            val result = ArrayList<Node>()
            process(degree,
                {
                    categories.forEachIndexed { index, ch -> it.accept(index, ch) }
                },
                { currentCategory, node ->
                    currentCategory.name = "${currentCategory.name}${node.name}"
                    currentCategory.found[node.name] = node
                },
                result)
            return result
        }

        private fun collectCategories(degree: Int, string: String, categories: MutableList<Node>) {
            process(degree,
                { string.forEachIndexed { index, ch -> it.accept(index, ch) } },
                { currentCategory, c ->
                    val ch = "$c"
                    currentCategory.name = "${currentCategory.name}$ch"
                    val adapter = Adapter(ch, KEY_BOARD_CLASS, "typeKey")
                    currentCategory.found[ch] = Action(ch, emptyArray(), adapter)
                },
                categories)
        }

        private fun <T> process(degree: Int, indexedIterator: Consumer<BiConsumer<Int, T>>, action: BiConsumer<Category, T>, categories: MutableList<Node>) {
            var currentCategory: Category? = null
            indexedIterator.accept { index, ch ->
                if (currentCategory == null) {
                    currentCategory = Category("", emptyArray(), LinkedHashSet())
                } else if (index % degree == 0) {
                    categories.add(currentCategory!!)
                    currentCategory = Category("", emptyArray(), LinkedHashSet())
                }

                action.accept(currentCategory!!, ch)
            }

            if (currentCategory != null) {
                if (currentCategory!!.name.length == 1
                    && currentCategory!!.found.values.size == 1
                    && currentCategory!!.found.values.stream().allMatch { it is Action }) {
                    val ch = currentCategory!!.name
                    val adapter = Adapter(ch, KEY_BOARD_CLASS, "typeKey")
                    categories.add(Action(ch, emptyArray(), adapter))
                } else {
                    categories.add(currentCategory!!)
                }
            }
        }
    }

    @TaskAction
    fun run() {
        runStatic(ctx)
    }

}