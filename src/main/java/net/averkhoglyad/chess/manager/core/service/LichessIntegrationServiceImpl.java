package net.averkhoglyad.chess.manager.core.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import net.averkhoglyad.chess.manager.core.data.Paging;
import net.averkhoglyad.chess.manager.core.sdk.data.Game;
import net.averkhoglyad.chess.manager.core.sdk.data.PageResult;
import net.averkhoglyad.chess.manager.core.sdk.data.User;
import net.averkhoglyad.chess.manager.core.sdk.http.*;
import net.averkhoglyad.chess.manager.core.sdk.operation.GameOperation;
import net.averkhoglyad.chess.manager.core.sdk.operation.UserOperation;

import java.util.Optional;

import static net.averkhoglyad.chess.manager.core.helper.ExceptionHelper.doStrict;
import static net.averkhoglyad.chess.manager.core.helper.ExceptionHelper.runtimeException;

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
        client = new JsonWebClientImpl("https://lichess.org", mapper);
    }

    @Override
    public Optional<User> getUser(String username) {
        Request request = Request.operation(UserOperation.GET)
            .pathVariable("username", username)
            .build();
        return executeOptionalRequest(request);
    }

    @Override
    public Optional<PageResult<Game>> getUserGames(String username, Paging paging) {
        Request request = Request.operation(UserOperation.GAMES)
            .pathVariable("username", username)
            .addQueryParam("nb", paging.getPageSize())
            .addQueryParam("page", paging.getPage())
            .build();
        return executeOptionalRequest(request);
    }

    @Override
    public Optional<Game> getGame(String gameId) {
        Request request = Request.operation(GameOperation.GET)
            .pathVariable("id", gameId)
            .addQueryParam("with_fens", "1")
            .addQueryParam("with_moves", "1")
            .build();
        return executeOptionalRequest(request);
    }

    private <E> Optional<E> executeOptionalRequest(Request request) {
        try {
            return Optional.of(client.send(request));
        } catch (ErrorResponseException e) {
            if(e.getStatusCode() == 404) {
                return Optional.empty();
            }
            throw runtimeException(e);
        } catch (EmptyResponseException e) {
            return Optional.empty();
        }
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
