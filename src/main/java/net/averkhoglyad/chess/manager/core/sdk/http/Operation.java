package net.averkhoglyad.chess.manager.core.sdk.http;

public interface Operation {

    String getUrl();

    Verb getVerb();

    Class<?> getRequestClass();

    Class<?> getResponseClass();

}
