package net.averkhoglyad.chess.manager.core.transport

import org.apache.http.Header
import org.apache.http.HttpEntity
import org.apache.http.util.EntityUtils
import java.io.*

class ClosableHttpEntity(private val httpEntity: HttpEntity) : HttpEntity by httpEntity, Closeable {

    @Throws(IOException::class)
    override fun close() {
        EntityUtils.consume(httpEntity)
    }

}

object EmptyHttpEntity : HttpEntity {
    override fun isRepeatable(): Boolean {
        return false
    }

    override fun isChunked(): Boolean {
        return false
    }

    override fun getContentLength(): Long {
        return 0
    }

    override fun getContentType(): Header? {
        return null
    }

    override fun getContentEncoding(): Header? {
        return null
    }

    @Throws(IOException::class, UnsupportedOperationException::class)
    override fun getContent(): InputStream {
        return ByteArrayInputStream(byteArrayOf())
    }

    @Throws(IOException::class)
    override fun writeTo(outstream: OutputStream) {

    }

    override fun isStreaming(): Boolean {
        return false
    }

    @Deprecated("")
    @Throws(IOException::class)
    override fun consumeContent() {

    }
}
