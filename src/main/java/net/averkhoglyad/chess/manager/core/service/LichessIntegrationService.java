package net.averkhoglyad.chess.manager.core.service;

import net.averkhoglyad.chess.manager.core.data.Paging;
import net.averkhoglyad.chess.manager.core.sdk.data.Game;
import net.averkhoglyad.chess.manager.core.sdk.data.PageResult;
import net.averkhoglyad.chess.manager.core.sdk.data.User;

import java.util.concurrent.CompletableFuture;

public interface LichessIntegrationService {

    CompletableFuture<User> getUser(String username);

    CompletableFuture<PageResult<Game>> getUserGames(String username, Paging paging);

    CompletableFuture<Game> getGame(String gameId);

    String loadGamePgn(String gameId);

}
