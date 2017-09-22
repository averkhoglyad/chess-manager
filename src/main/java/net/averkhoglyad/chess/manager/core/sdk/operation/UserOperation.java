package net.averkhoglyad.chess.manager.core.sdk.operation;

import net.averkhoglyad.chess.manager.core.sdk.http.Verb;
import net.averkhoglyad.chess.manager.core.sdk.data.GamePageResult;
import net.averkhoglyad.chess.manager.core.sdk.data.User;
import net.averkhoglyad.chess.manager.core.sdk.http.Operation;

public enum UserOperation implements Operation {

    GET("/api/user/{username}", Verb.GET, Void.class, User.class),
    GAMES("/api/user/{username}/games", Verb.GET, Void.class, GamePageResult.class);

    private final String url;
    private final Verb verb;
    private final Class<?> requestClass;
    private final Class<?> responseClass;

    UserOperation(String url, Verb verb, Class<?> requestClass, Class<?> responseClass) {
        this.url = url;
        this.verb = verb;
        this.requestClass = requestClass;
        this.responseClass = responseClass;
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public Verb getVerb() {
        return verb;
    }

    @Override
    public Class getRequestClass() {
        return requestClass;
    }

    @Override
    public Class getResponseClass() {
        return responseClass;
    }

}
