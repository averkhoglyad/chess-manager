package net.averkhoglyad.chess.manager.core.service;

import net.averkhoglyad.chess.manager.core.data.Paging;
import net.averkhoglyad.chess.manager.core.sdk.data.Game;
import net.averkhoglyad.chess.manager.core.sdk.data.PageResult;
import net.averkhoglyad.chess.manager.core.sdk.data.User;

import java.util.Optional;

public interface LichessIntegrationService {

    Optional<User> getUser(String username);

    Optional<PageResult<Game>> getUserGames(String username, Paging paging);

    Optional<Game> getGame(String gameId);

    String loadGamePgn(String gameId);

}
