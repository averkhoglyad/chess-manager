package net.averkhoglyad.chess.manager.core.sdk.lichess

import net.averkhoglyad.chess.manager.core.sdk.lichess.data.Game
import net.averkhoglyad.chess.manager.core.sdk.lichess.data.GamePageResult
import net.averkhoglyad.chess.manager.core.sdk.lichess.data.User
import net.averkhoglyad.chess.manager.core.transport.Operation
import net.averkhoglyad.chess.manager.core.transport.Verb
import kotlin.reflect.KClass

const val endpointUrl = "https://lichess.org"

enum class GameOperation(
        path: String,
        override val verb: Verb,
        override val requestClass: KClass<*>,
        override val responseClass: KClass<*>) : Operation {

    GET("/api/game/{id}", Verb.GET, Nothing::class, Game::class),
    PGN("/game/export/{id}.pgn", Verb.GET, Nothing::class, String::class);

    override val url = endpointUrl + path

}

enum class UserOperation(
        path: String,
        override val verb: Verb,
        override val requestClass: KClass<*>,
        override val responseClass: KClass<*>) : Operation {

    GET("/api/user/{username}", Verb.GET, Nothing::class, User::class),
    GAMES("/api/user/{username}/games", Verb.GET, Nothing::class, GamePageResult::class);

    override val url = endpointUrl + path

}
