package net.averkhoglyad.chess.manager.core.sdk.http;

import java.util.Collections;
import java.util.Map;

public class ErrorResponseException extends Exception {

    private final int statusCode;
    private final Map<String, Object> meta;

    public ErrorResponseException(int statusCode, String reasonPhrase, Map<String, Object> meta) {
        super(reasonPhrase);
        this.statusCode = statusCode;
        this.meta = Collections.unmodifiableMap(meta);
    }

    public int getStatusCode() {
        return statusCode;
    }

    public Map<String, Object> getMeta() {
        return meta;
    }

}
