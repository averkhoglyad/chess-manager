package net.averkhoglyad.chess.manager.core.sdk.http;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ClosableHttpEntity implements HttpEntity, Closeable {

    private final HttpEntity httpEntity;

    public ClosableHttpEntity(HttpEntity httpEntity) {
        this.httpEntity = httpEntity;
    }

    @Override
    public void close() throws IOException {
        EntityUtils.consume(httpEntity);
    }

    // Delegated methods
    @Override
    public boolean isRepeatable() {
        return httpEntity.isRepeatable();
    }

    @Override
    public boolean isChunked() {
        return httpEntity.isChunked();
    }

    @Override
    public long getContentLength() {
        return httpEntity.getContentLength();
    }

    @Override
    public Header getContentType() {
        return httpEntity.getContentType();
    }

    @Override
    public Header getContentEncoding() {
        return httpEntity.getContentEncoding();
    }

    @Override
    public InputStream getContent() throws IOException, UnsupportedOperationException {
        return httpEntity.getContent();
    }

    @Override
    public void writeTo(OutputStream outstream) throws IOException {
        httpEntity.writeTo(outstream);
    }

    @Override
    public boolean isStreaming() {
        return httpEntity.isStreaming();
    }

    @Override
    @Deprecated
    public void consumeContent() throws IOException {
        httpEntity.consumeContent();
    }
}
