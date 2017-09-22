package net.averkhoglyad.chess.manager.core.sdk.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import net.averkhoglyad.chess.manager.core.helper.ExceptionHelper;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import sun.misc.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Slf4j
public class WebClientImpl implements WebClient {

    private final ObjectMapper mapper;
    private final String endpointUrl;

    public WebClientImpl(String endpointUrl) {
        this(endpointUrl, new ObjectMapper());
    }

    public WebClientImpl(String endpointUrl, ObjectMapper mapper) {
        if (endpointUrl == null || endpointUrl.isEmpty()) {
            throw new IllegalArgumentException("Argument `endpointUrl` is required and can't be null or empty String");
        }
        this.endpointUrl = endpointUrl;
        this.mapper = mapper;
    }

    @Override
    public <R> R send(Request request) throws ErrorResponseException {
        try {
            return doSend(request);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private <R> R doSend(Request request) throws ErrorResponseException, IOException {
        HttpRequestBase httpRequest = createHttpRequest(request);
        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse httpResponse = httpClient.execute(httpRequest);
             ClosableHttpEntity httpEntity = new ClosableHttpEntity(httpResponse.getEntity())) {
            log.debug("WebClient response status: {} {}", httpResponse.getStatusLine().getStatusCode(), httpResponse.getStatusLine().getReasonPhrase());
            if (httpResponse.getStatusLine().getStatusCode() >= 400) {
                throw createErrorResponse(httpResponse);
            }
            Operation operation = request.getOperation();
            if (log.isDebugEnabled()) {
                String res = EntityUtils.toString(httpEntity);
                log.debug("WebClient response:\n{}", res);
                return (R) deserialize(operation.getResponseClass(), res);
            } else {
                return (R) deserialize(operation.getResponseClass(), httpEntity.getContent());
            }
        }
    }

    private <R> R deserialize(Class<R> targetClass, InputStream in) {
        if (targetClass == String.class) {
            return (R) new String(ExceptionHelper.doStrict(() -> IOUtils.readFully(in, -1, false)));
        } else {
            return ExceptionHelper.doStrict(() -> mapper.readValue(in, targetClass));
        }
    }

    private <R> R deserialize(Class<R> targetClass, String in) {
        if (targetClass == String.class) {
            return (R) in;
        } else {
            return ExceptionHelper.doStrict(() -> mapper.readValue(in, targetClass));
        }
    }

    private HttpRequestBase createHttpRequest(Request request) throws JsonProcessingException {
        Operation operation = request.getOperation();
        URI uri = buildUrl(operation.getUrl(), request.getPathVariables(), request.getQueryParams());
        HttpRequestBase httpRequest = operation.getVerb().get();
        httpRequest.setURI(uri);
        applyRequestBody(request, operation, httpRequest);
        applyHeaders(httpRequest, request.getHeaders());
        return httpRequest;
    }

    private URI buildUrl(String url, Map<String, ?> pathVariables, Map<String, List<String>> queryParams) {
        URIBuilder builder = ExceptionHelper.doStrict(() -> new URIBuilder(endpointUrl));

        String resultPath = url;
        for (Map.Entry<String, ?> entry : pathVariables.entrySet()) {
            resultPath = resultPath.replace('{' + entry.getKey() + '}', entry.getValue().toString());
        }
        builder.setPath(builder.getPath() + resultPath);

        queryParams.entrySet().stream()
            .flatMap(entry -> entry.getValue().stream().map(value -> new BasicNameValuePair(entry.getKey(), value)))
            .forEach(pair -> builder.addParameter(pair.getName(), pair.getValue()));

        return ExceptionHelper.doStrict(() -> builder.build());
    }

    private void applyRequestBody(Request request, Operation operation, HttpRequestBase httpRequest) throws JsonProcessingException {
        if (operation.getRequestClass() == Void.class) return;
        if (!operation.getRequestClass().isInstance(request.getBody())) {
            throw new IllegalArgumentException("Unsupported body type");
        }
        if (!(httpRequest instanceof HttpEntityEnclosingRequestBase)) {
            throw new IllegalArgumentException("Unsupported body type");
        }
        ((HttpEntityEnclosingRequestBase) httpRequest).setEntity(new ByteArrayEntity(mapper.writeValueAsBytes(request.getBody())));
    }

    private void applyHeaders(HttpRequestBase httpRequest, Map<String, List<String>> headers) {
        headers.entrySet().stream()
            .flatMap(it -> createMultiHeaderStream(it.getKey(), it.getValue()))
            .forEach(httpRequest::addHeader);
        httpRequest.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
        httpRequest.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());
    }

    private Stream<Header> createMultiHeaderStream(String key, Collection<String> values) {
        return values.stream().map(value -> new BasicHeader(key, value));
    }

    private ErrorResponseException createErrorResponse(CloseableHttpResponse httpResponse) {
        return new ErrorResponseException(
            httpResponse.getStatusLine().getStatusCode(),
            httpResponse.getStatusLine().getReasonPhrase(),
            ExceptionHelper.doQuiet(() -> mapper.readValue(httpResponse.getEntity().getContent(), Map.class))
        );
    }

}
