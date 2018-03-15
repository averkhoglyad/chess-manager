package net.averkhoglyad.chess.manager.core.transport

abstract class HttpStatusAwareException(val statusCode: Int, val reasonPhrase: String) : Exception("Status: $statusCode $reasonPhrase")

class ErrorResponseException(statusCode: Int, reasonPhrase: String, val meta: Map<String, Any> = emptyMap())
    : HttpStatusAwareException(statusCode, reasonPhrase)

class EmptyResponseException(statusCode: Int, reasonPhrase: String) : HttpStatusAwareException(statusCode, reasonPhrase) {
    override val message: String
        get () = "Unexpected empty response. ${super.message}"
}
