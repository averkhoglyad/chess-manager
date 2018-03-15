package net.averkhoglyad.chess.manager.core.service

import net.averkhoglyad.chess.manager.core.data.Paging
import net.averkhoglyad.chess.manager.core.sdk.lichess.data.Game
import net.averkhoglyad.chess.manager.core.sdk.lichess.data.PageResult
import net.averkhoglyad.chess.manager.core.sdk.lichess.data.User

interface LichessIntegrationService {

    fun getUser(username: String): User?

    fun getUserGames(username: String, paging: Paging): PageResult<Game>?

    fun getGame(gameId: String): Game?

    fun loadGamePgn(gameId: String): String

}
