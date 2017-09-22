package net.averkhoglyad.chess.manager.core.sdk.http;


import org.apache.http.client.methods.*;

import java.util.function.Supplier;

public enum Verb implements Supplier<HttpRequestBase> {

    GET {
        @Override
        public HttpGet get() {
            return new HttpGet();
        }
    },
    HEAD {
        @Override
        public HttpHead get() {
            return new HttpHead();
        }
    },
    POST {
        @Override
        public HttpPost get() {
            return new HttpPost();
        }
    },
    PUT {
        @Override
        public HttpPut get() {
            return new HttpPut();
        }
    },
    PATCH {
        @Override
        public HttpPatch get() {
            return new HttpPatch();
        }
    },
    DELETE {
        @Override
        public HttpDelete get() {
            return new HttpDelete();
        }
    },
    OPTIONS {
        @Override
        public HttpOptions get() {
            return new HttpOptions();
        }
    },
    TRACE {
        @Override
        public HttpTrace get() {
            return new HttpTrace();
        }
    }

}
