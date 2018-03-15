package net.averkhoglyad.chess.manager.core.transport

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import net.averkhoglyad.chess.manager.core.util.Slf4j
import net.averkhoglyad.chess.manager.core.util.orThrow
import org.apache.http.Header
import org.apache.http.HttpEntity
import org.apache.http.HttpHeaders
import org.apache.http.StatusLine
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase
import org.apache.http.client.methods.HttpRequestBase
import org.apache.http.entity.ByteArrayEntity
import org.apache.http.entity.ContentType
import org.apache.http.impl.client.HttpClients
import org.apache.http.message.BasicHeader
import org.apache.http.util.EntityUtils
import java.io.IOException
import java.io.InputStream
import java.nio.charset.Charset
import java.util.stream.Stream
import kotlin.reflect.KClass

class JsonWebClientImpl @JvmOverloads constructor(
        private val mapper: ObjectMapper = ObjectMapper()) : WebClient {

    private val log by Slf4j()

    @Throws(ErrorResponseException::class, EmptyResponseException::class, IOException::class)
    override fun <R: Any> send(request: Request): R? {
        val httpRequest = createHttpRequest(request)
        HttpClients.createDefault().use { httpClient ->
            httpClient.execute(httpRequest).use { httpResponse ->
                log.debug("WebClient response status: {} {}", httpResponse.statusLine.statusCode, httpResponse.statusLine.reasonPhrase)
                if (httpResponse.statusLine.statusCode >= 400) {
                    throw createErrorResponse(httpResponse)
                }
                ClosableHttpEntity(httpResponse.entity ?: EmptyHttpEntity).use { httpEntity ->
                    return parseResponse(request, httpResponse.statusLine, httpEntity)
                }
            }
        }
    }

    @Throws(JsonProcessingException::class)
    private fun createHttpRequest(request: Request): HttpRequestBase {
        val operation = request.operation
        val httpRequest = operation.verb.get()
        httpRequest.uri = request.url
        applyRequestBody(request, operation, httpRequest)
        applyHeaders(httpRequest, request.headers)
        return httpRequest
    }

    @Throws(JsonProcessingException::class)
    private fun applyRequestBody(request: Request, operation: Operation, httpRequest: HttpRequestBase) {
        if (operation.requestClass == Nothing::class) return
        operation.requestClass.isInstance(request.body) orThrow {
            "Unsupported body type. ${operation.requestClass} was expected but ${request.body?.javaClass} was found."
        }
        (httpRequest as HttpEntityEnclosingRequestBase).entity = ByteArrayEntity(mapper.writeValueAsBytes(request.body))
    }

    private fun applyHeaders(httpRequest: HttpRequestBase, headers: Map<String, List<String>>) {
        headers.entries.stream()
                .flatMap { it -> createMultiHeaderStream(it.key, it.value) }
                .forEach { httpRequest.addHeader(it) }
        httpRequest.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.mimeType)
        httpRequest.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.mimeType)
    }

    private fun createMultiHeaderStream(key: String, values: Collection<String>): Stream<Header> {
        return values.stream().map { value -> BasicHeader(key, value) }
    }

    @Throws(EmptyResponseException::class, IOException::class)
    private fun <R : Any> parseResponse(request: Request, statusLine: StatusLine, httpEntity: HttpEntity): R? {
        val operation = request.operation
        if (operation.responseClass == Unit::class) return Unit as R
        if (operation.responseClass == Nothing::class) return null
        if (httpEntity.contentLength == 0L) {
            throw EmptyResponseException(statusLine.statusCode, statusLine.reasonPhrase)
        }
        if (log.isDebugEnabled) {
            val res = EntityUtils.toString(httpEntity)
            log.debug("WebClient response:\n{}", res)
            return deserialize(operation.responseClass, res)
        } else {
            return deserialize(operation.responseClass, httpEntity.content)
        }
    }

    private fun <R: Any> deserialize(targetClass: KClass<*>, input: InputStream): R {
        if (targetClass == String::class) { // TODO: Remove from WebClient. Find a better way to get response as String, byte[] or InputStream. Probably need some kinds of WebClient (json or xml based, raw, etc) or more low-level component for this.
            return (input.reader(Charset.defaultCharset())).use { it.readText() } as R
        } else {
            return mapper.readValue<R>(input, (targetClass as KClass<R>).java)
        }
    }

    private fun <R: Any> deserialize(targetClass: KClass<*>, input: String): R {
        if (targetClass == String::class) { // TODO: Remove from WebClient. Find a better way to get response as String, byte[] or InputStream. Probably need some kinds of WebClient (json or xml based, raw, etc) or more low-level component for this.
            return input as R
        } else {
            return mapper.readValue<R>(input, (targetClass as KClass<R>).java)
        }
    }

    private fun createErrorResponse(httpResponse: CloseableHttpResponse): ErrorResponseException {
        return ErrorResponseException(
                httpResponse.statusLine.statusCode,
                httpResponse.statusLine.reasonPhrase)
    }

}
