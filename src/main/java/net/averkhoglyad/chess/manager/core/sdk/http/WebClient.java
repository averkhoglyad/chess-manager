package net.averkhoglyad.chess.manager.core.sdk.http;

public interface WebClient {

    <R> R send(Request request) throws ErrorResponseException;

}
