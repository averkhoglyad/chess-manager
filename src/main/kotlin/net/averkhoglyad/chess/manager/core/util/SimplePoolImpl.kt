package net.averkhoglyad.chess.manager.core.util

import java.util.*
import java.util.function.Supplier

class SimplePoolImpl<E>(private val factory: Supplier<E>, initial: Int = 0) : Pool<E> {

    private val entries: MutableSet<E>

    init {
        entries = HashSet(initial)
        for (i in 0 until initial) {
            entries.add(factory.get())
        }
    }

    override fun provide(): E {
        val entry = entries.first() ?: factory.get()
        entries.remove(entry)
        return entry
    }

    override fun release(entry: E) {
        entries.add(entry)
    }

}
