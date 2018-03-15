package net.averkhoglyad.chess.manager.core.sdk.lichess.data

open class PageResult<T> {
    open var currentPage: Int = 0
    open var previousPage: Int = 0
    open var nextPage: Int = 0
    open var maxPerPage: Int = 0
    open var nbPages: Int = 0
    open var nbResults: Int = 0
    open var currentPageResults: List<T> = listOf()
}

class GamePageResult : PageResult<Game>() {
    override var currentPage: Int = 0
    override var previousPage: Int = 0
    override var nextPage: Int = 0
    override var maxPerPage: Int = 0
    override var nbPages: Int = 0
    override var nbResults: Int = 0
    override var currentPageResults: List<Game> = listOf()
}
