package net.averkhoglyad.chess.manager.core.data

data class Paging(val offset: Int, val limit:Int) {
    val page: Int
        get() = (offset + limit) / limit
}

data class Page<out E>(val items: List<E>, val total: Int)
