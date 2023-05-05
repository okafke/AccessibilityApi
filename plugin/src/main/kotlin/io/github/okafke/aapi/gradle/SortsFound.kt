package io.github.okafke.aapi.gradle

interface SortsFound: JsonableWithFound {
    val children: MutableSet<String>

    /**
     * Children might get found in an arbitrary order.
     */
    fun sortFound() {
        if (found.size == children.size && found.keys.stream().allMatch { children.contains(it) }) {
            val newFound = LinkedHashMap<String, Node>()
            for (child in children) {
                newFound[child] = found[child]!!
            }

            this.found = newFound
        }
    }

}