package net.averkhoglyad.chess.manager.core.sdk.http;

import org.apache.http.HttpHeaders;

import java.util.*;
import java.util.stream.Collectors;

public class Request {

    private final Operation operation;
    private final Object body;
    private final Map<String, String> pathVariables;
    private final Map<String, List<String>> queryParams;
    private final Map<String, List<String>> headers;

    private Request(Operation operation, Object body,
                    Map<String, String> pathVariables,
                    Map<String, List<String>> queryParams,
                    Map<String, List<String>> headers) {
        this.operation = operation;
        this.body = body;
        this.pathVariables = pathVariables;
        this.queryParams = queryParams;
        this.headers = headers;
    }

    public Operation getOperation() {
        return operation;
    }

    public Object getBody() {
        return body;
    }

    public Map<String, String> getPathVariables() {
        return pathVariables;
    }

    public Map<String, List<String>> getQueryParams() {
        return queryParams;
    }

    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    public static Builder operation(Operation operation) {
        return new Builder(operation);
    }

    public static class Builder {

        private Operation operation;
        private String authorization;
        private Object body;
        private Map<String, String> pathVariables = new HashMap<>();
        private Map<String, List<String>> queryParams = new HashMap();
        private Map<String, List<String>> headers = new HashMap();

        public Builder(Operation operation) {
            this.operation = operation;
        }

        public Request build() {
            if (authorization != null && authorization.length() > 0) {
                header(HttpHeaders.AUTHORIZATION, authorization);
            }
            return new Request(operation, body, pathVariables, queryParams, headers);
        }

        public Builder authorization(String authorization, String type) {
            this.authorization = type + " " + authorization;
            return this;
        }

        public Builder authorization(String authorization) {
            return authorization(authorization, "Bearer");
        }

        public Builder body(Object body) {
            this.body = body;
            return this;
        }

        public Builder pathVariable(String key, Object raw) {
            if (key == null || key.isEmpty()) throw new IllegalArgumentException();
            String str = paramToString(raw);
            pathVariables.put(key, str);
            return this;
        }

        public Builder header(String key, Object... raw) {
            if (key == null || key.isEmpty()) throw new IllegalArgumentException();
            List<String> values = Arrays.stream(raw)
                .map(this::paramToString)
                .collect(Collectors.toList());
            headers.put(key, values);
            return this;
        }

        public Builder addHeader(String key, Object raw) {
            if (key == null || key.isEmpty()) throw new IllegalArgumentException();
            headers.putIfAbsent(key, new ArrayList<>());
            headers.get(key).add(paramToString(raw));
            return this;
        }

        public Builder queryParam(String key, Object... raw) {
            if (key == null || key.isEmpty()) throw new IllegalArgumentException();
            List<String> values = Arrays.stream(raw)
                .map(this::paramToString)
                .collect(Collectors.toList());
            queryParams.put(key, values);
            return this;
        }

        public Builder addQueryParam(String key, Object raw) {
            if (key == null || key.isEmpty()) throw new IllegalArgumentException();
            queryParams.putIfAbsent(key, new ArrayList<>());
            queryParams.get(key).add(paramToString(raw));
            return this;
        }

        private String paramToString(Object raw) {
            return Optional.ofNullable(raw)
                .map(Object::toString)
                .orElse("");
        }

    }

}
