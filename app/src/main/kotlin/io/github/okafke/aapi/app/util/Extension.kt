package io.github.okafke.aapi.app.util

fun <E> Set<E>.index(index: Int): E {
    val itr = this.iterator()
    for (i in 0 until index) {
        itr.next()
    }

    return itr.next()
}

fun <E> LinkedHashSet<E>.addFirst(e: E) {

}

fun <E> Iterator<E>.last(): E? {
    var result: E? = null
    while (this.hasNext()) {
        result = next()
    }

    return result
}
