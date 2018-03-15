package net.averkhoglyad.chess.manager.gui.data

sealed class AsyncResult {
    class Success(val data: Any) : AsyncResult()
    class Error(val ex: Exception) : AsyncResult()
    object Empty: AsyncResult()
}
