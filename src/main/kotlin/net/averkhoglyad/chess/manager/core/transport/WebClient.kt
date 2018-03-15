package net.averkhoglyad.chess.manager.core.transport

import java.io.IOException

interface WebClient {

    @Throws(ErrorResponseException::class, EmptyResponseException::class, IOException::class)
    fun <R: Any> send(request: Request): R?

}
