package net.averkhoglyad.chess.manager.core.service

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import net.averkhoglyad.chess.manager.core.data.Paging
import net.averkhoglyad.chess.manager.core.sdk.lichess.GameOperation
import net.averkhoglyad.chess.manager.core.sdk.lichess.UserOperation
import net.averkhoglyad.chess.manager.core.sdk.lichess.data.Game
import net.averkhoglyad.chess.manager.core.sdk.lichess.data.PageResult
import net.averkhoglyad.chess.manager.core.sdk.lichess.data.User
import net.averkhoglyad.chess.manager.core.transport.*

class LichessIntegrationServiceImpl() : LichessIntegrationService {

    // TODO: Must be injected!
    private val client: WebClient

    init {
        val mapper = ObjectMapper()
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true)
        mapper.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true)
        mapper.configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false)
        mapper.findAndRegisterModules()
        client = JsonWebClientImpl(mapper)
    }

    override fun getUser(username: String): User? {
        val request = Request.operation(UserOperation.GET)
                .pathVariable("username", username)
                .build()
        return executeOptionalRequest(request)
    }

    override fun getUserGames(username: String, paging: Paging): PageResult<Game>? {
        val request = Request.operation(UserOperation.GAMES)
                .pathVariable("username", username)
                .addQueryParam("nb", paging.limit)
                .addQueryParam("page", paging.page)
                .build()
        return executeOptionalRequest(request)
    }

    override fun getGame(gameId: String): Game? {
        val request = Request.operation(GameOperation.GET)
                .pathVariable("id", gameId)
                .addQueryParam("with_fens", "1")
                .addQueryParam("with_moves", "1")
                .build()
        return executeOptionalRequest(request)
    }

    private fun <E : Any> executeOptionalRequest(request: Request): E? {
        try {
            return client.send(request)
        } catch (e: ErrorResponseException) {
            if (e.statusCode == 404) {
                return null
            }
            throw e
        } catch (e: EmptyResponseException) {
            return null
        }

    }

    override fun loadGamePgn(gameId: String): String {
        val request = Request.operation(GameOperation.PGN)
                .pathVariable("id", gameId)
                .addQueryParam("as", "raw")
                .build()
        return request.url.toURL().readText()
    }

}
