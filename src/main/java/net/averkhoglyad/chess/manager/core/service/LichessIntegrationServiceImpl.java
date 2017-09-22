package net.averkhoglyad.chess.manager.core.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import net.averkhoglyad.chess.manager.core.data.Paging;
import net.averkhoglyad.chess.manager.core.sdk.data.Game;
import net.averkhoglyad.chess.manager.core.sdk.data.PageResult;
import net.averkhoglyad.chess.manager.core.sdk.data.User;
import net.averkhoglyad.chess.manager.core.sdk.http.Request;
import net.averkhoglyad.chess.manager.core.sdk.http.WebClient;
import net.averkhoglyad.chess.manager.core.sdk.http.WebClientImpl;
import net.averkhoglyad.chess.manager.core.sdk.operation.GameOperation;
import net.averkhoglyad.chess.manager.core.sdk.operation.UserOperation;

import java.util.concurrent.CompletableFuture;

import static net.averkhoglyad.chess.manager.core.helper.ExceptionHelper.doStrict;

public class LichessIntegrationServiceImpl implements LichessIntegrationService {

    private final WebClient client;

    public LichessIntegrationServiceImpl() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        JavaTimeModule module = new JavaTimeModule();
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
        mapper.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);
        mapper.configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
        mapper.registerModule(module);
        client = new WebClientImpl("https://lichess.org", mapper);
    }

    @Override
    public CompletableFuture<User> getUser(String username) {
        return CompletableFuture.supplyAsync(() -> {
            Request request = Request.operation(UserOperation.GET)
                .pathVariable("username", username)
                .build();
            return doStrict(() -> client.send(request));
        });
    }

    @Override
    public CompletableFuture<PageResult<Game>> getUserGames(String username, Paging paging) {
        return CompletableFuture.supplyAsync(() -> {
            Request request = Request.operation(UserOperation.GAMES)
                .pathVariable("username", username)
                .addQueryParam("nb", paging.getPageSize())
                .addQueryParam("page", paging.getPage())
                .build();
            return doStrict(() -> client.send(request));
        });
    }

    @Override
    public CompletableFuture<Game> getGame(String gameId) {
        return CompletableFuture.supplyAsync(() -> {
            Request request = Request.operation(GameOperation.GET)
                .pathVariable("id", gameId)
                .addQueryParam("with_fens", "1")
                .addQueryParam("with_moves", "1")
                .build();
            return doStrict(() -> client.send(request));
        });
    }

    @Override
    public String loadGamePgn(String gameId) {
        Request request = Request.operation(GameOperation.PGN)
            .pathVariable("id", gameId)
            .addQueryParam("as", "raw")
            .build();
        return doStrict(() -> client.send(request));
    }

}
