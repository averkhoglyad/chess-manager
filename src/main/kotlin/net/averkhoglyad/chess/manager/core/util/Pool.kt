package net.averkhoglyad.chess.manager.core.util

interface Pool<E> {

    fun provide(): E
    fun release(entry: E)

}
