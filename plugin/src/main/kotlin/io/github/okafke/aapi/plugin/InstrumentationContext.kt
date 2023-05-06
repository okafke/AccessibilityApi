package io.github.okafke.aapi.plugin

import com.google.gson.JsonParser
import java.io.*
import java.nio.file.Files
import java.util.concurrent.ConcurrentHashMap

class InstrumentationContext(private val dir: File, private val cacheDir: File): Serializable {
    val actions = HashMap<String, Action>()
    private val categories = HashMap<String, Category>()
    private val trees = ConcurrentHashMap<String, Tree>()

    init {
        dir.mkdirs()
        cacheDir.mkdirs()
    }

    fun addAction(action: Action) {
        actions[action.name] = action
    }

    fun addCategory(category: Category) {
        categories[category.name] = category
    }

    fun addTree(tree: Tree) {
        trees[tree.name] = tree
    }

    fun update() {
        updateCache()
        for (tree in trees.values) {
            var done = true
            for (categoryName in tree.children) {
                val category = categories[categoryName]
                if (!done || category == null) {
                    val action = actions[categoryName]
                    if (action != null) {
                        println("Tree ${tree.name} uses action $categoryName")
                        tree.found[categoryName] = action
                        continue
                    }

                    println("Tree ${tree.name} is still missing $categoryName")
                    done = false
                    break
                }

                tree.found[categoryName] = category
                if (!recursivelyResolveCategory(category)) {
                    done = false
                    break
                }
            }

            if (done && tree.children.size == tree.found.size) {
                writeTree(tree)
            }
        }
    }

    fun writeTree(tree: Tree) {
        println("Resolving Tree $tree")
        val array = tree.toJson()
        Files.newBufferedWriter(dir.toPath().resolve("${tree.name}.json")).use { br ->
            Constants.GSON.toJson(array, br)
        }

        for (i in 2..8) {
            writeTree(tree, i)
        }

        trees.remove(tree.name, tree)
    }

    fun writeTree(tree: Tree, degree: Int) {
        Files.newBufferedWriter(dir.toPath().resolve("${tree.name}_$degree.json")).use { br ->
            Constants.GSON.toJson(tree.toJson(degree), br)
        }
    }

    private fun recursivelyResolveCategory(category: Category): Boolean {
        for (actionName in category.children) {
            var child: Node? = actions[actionName]
            if (child == null) {
                child = categories[actionName]
                if (child == null) {
                    println("Category ${category.name} is still missing $actionName")
                    return false
                }

                recursivelyResolveCategory(child)
            }

            category.found[actionName] = child
        }

        return true
    }

    /**
     * We need this because this Context will not be shared between multiple projects that use this
     * plugin. So e.g. the AudioTree in clientcommons is not available when compiling
     * RetroMusicPlayer.
     */
    // TODO: allow the user to specify other caches which he can use when he imports another library
    // TODO: this could be problematic if multiple processes try to access the cache?
    private fun updateCache() {
        val cache = File(cacheDir, "cache.json")
        if (!cache.exists()) {
            cache.createNewFile()
            FileOutputStream(cache).use {
                it.write("""
                    {
                        "categories": {},
                        "actions": {}
                    }
                """.trimIndent().toByteArray())
            }
        }

        val json = InputStreamReader(FileInputStream(cache)).use { isr ->
            return@use JsonParser.parseReader(isr).asJsonObject
        }

        val categoryJson = json.get("categories").asJsonObject
        categoryJson.entrySet().forEach { entry ->
            if (!categories.containsKey(entry.key)) {
                categories[entry.key] = Constants.GSON.fromJson(entry.value, Category::class.java)
            }
        }

        this.categories.values.forEach { category ->
            if (!categoryJson.has(category.name)) {
                categoryJson.add(category.name, Constants.GSON.toJsonTree(category))
            }
        }

        val actionsJson = json.get("actions").asJsonObject
        actionsJson.entrySet().forEach { entry ->
            if (!actions.containsKey(entry.key)) {
                actions[entry.key] = Constants.GSON.fromJson(entry.value, Action::class.java)
            }
        }

        this.actions.values.forEach { action ->
            if (!actionsJson.has(action.name)) {
                actionsJson.add(action.name, Constants.GSON.toJsonTree(action))
            }
        }

        Files.newBufferedWriter(cache.toPath()).use { fos ->
            Constants.GSON.toJson(json, fos)
        }
    }

}
