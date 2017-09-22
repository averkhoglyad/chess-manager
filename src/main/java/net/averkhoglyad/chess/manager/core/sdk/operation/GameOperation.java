package net.averkhoglyad.chess.manager.core.sdk.operation;

import net.averkhoglyad.chess.manager.core.sdk.data.Game;
import net.averkhoglyad.chess.manager.core.sdk.http.Operation;
import net.averkhoglyad.chess.manager.core.sdk.http.Verb;

public enum GameOperation implements Operation {

    GET("/api/game/{id}", Verb.GET, Void.class, Game.class),
    PGN("/game/export/{id}.pgn", Verb.GET, Void.class, String.class);

    private final String url;
    private final Verb verb;
    private final Class<?> requestClass;
    private final Class<?> responseClass;

    GameOperation(String url, Verb verb, Class<?> requestClass, Class<?> responseClass) {
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
