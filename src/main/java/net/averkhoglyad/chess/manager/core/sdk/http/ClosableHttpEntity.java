package net.averkhoglyad.chess.manager.core.sdk.http;

import lombok.experimental.Delegate;
import net.averkhoglyad.chess.manager.core.helper.IOStreamHelper;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ClosableHttpEntity implements HttpEntity, Closeable {

    @Delegate
    private final HttpEntity httpEntity;

    public ClosableHttpEntity(HttpEntity httpEntity) {
        this.httpEntity = httpEntity != null ? httpEntity : new EmptyHttpEntity();
    }

    @Override
    public void close() throws IOException {
        EntityUtils.consume(httpEntity);
    }

    private static final class EmptyHttpEntity implements HttpEntity {
        @Override
        public boolean isRepeatable() {
            return false;
        }

        @Override
        public boolean isChunked() {
            return false;
        }

        @Override
        public long getContentLength() {
            return 0;
        }

        @Override
        public Header getContentType() {
            return null;
        }

        @Override
        public Header getContentEncoding() {
            return null;
        }

        @Override
        public InputStream getContent() throws IOException, UnsupportedOperationException {
            return IOStreamHelper.emptyInput();
        }

        @Override
        public void writeTo(OutputStream outstream) throws IOException {

        }

        @Override
        public boolean isStreaming() {
            return false;
        }

        @Override
        @Deprecated
        public void consumeContent() throws IOException {

        }
    }

}
