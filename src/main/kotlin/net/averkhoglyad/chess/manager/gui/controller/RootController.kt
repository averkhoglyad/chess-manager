package net.averkhoglyad.chess.manager.gui.controller

import net.averkhoglyad.chess.manager.core.data.Page
import net.averkhoglyad.chess.manager.core.data.Paging
import net.averkhoglyad.chess.manager.core.data.Profile
import net.averkhoglyad.chess.manager.core.sdk.lichess.data.Game
import net.averkhoglyad.chess.manager.core.service.LichessIntegrationService
import net.averkhoglyad.chess.manager.gui.data.AsyncResult
import net.averkhoglyad.chess.manager.gui.data.AsyncResult.*
import tornadofx.Controller
import tornadofx.FXTask
import tornadofx.TaskStatus
import java.io.IOException
import java.io.PrintStream
import java.nio.file.Files
import java.nio.file.Path

class RootController : Controller() {

    private val service by di<LichessIntegrationService>()

    fun loadGames(profile: Profile, paging: Paging, op: (AsyncResult) -> Unit) {
        executeAsync(op) {
            val games = service.getUserGames(profile.lichessId, paging)
            if (games == null) Empty
            else Success(Page(games.currentPageResults, games.nbPages))
        }
    }

    fun loadGame(id: String, op: (AsyncResult) -> Unit) {
        executeAsync(op) {
            val game = service.getGame(id)
            if (game == null) Empty
            else Success(game)
        }
    }

    fun loadGamesToFileAsPgn(target: Path, games: List<Game>, op: (AsyncResult) -> Unit): TaskStatus {
        val status = TaskStatus()
        executeAsync(status, op) {
            if (Files.exists(target) && !Files.isWritable(target)) {
                throw IOException("Target file is not writable")
            }
            val tempPath = Files.createTempFile("lichess.", ".pgn")
            try {
                Files.newOutputStream(tempPath).use { out ->
                    updateProgress(-1L, games.size.toLong())
                    val tempFilePrint = PrintStream(out)
                    games.asSequence()
                            .sortedBy { it.createdAt }
                            .map { service.loadGamePgn(it.id) }
                            .forEachIndexed { i, pgn ->
                                tempFilePrint.println(pgn)
                                tempFilePrint.println()
                                tempFilePrint.flush()
                                updateProgress(i.toLong(), games.size.toLong())
                            }
                    Files.deleteIfExists(target)
                    Files.copy(tempPath, target)
                }
            } finally {
                try {
                    Files.delete(tempPath)
                } catch (ex: Exception) {
                    // do nothing
                }
            }
            return@executeAsync Empty
        }

        return status
    }

    private inline fun executeAsync(cb: (AsyncResult) -> Unit, crossinline exec: FXTask<*>.() -> AsyncResult) {
        runAsync {
            wrapWithTryCatch {
                this.exec()
            }
        } ui (cb)
    }

    private inline fun executeAsync(status: TaskStatus, cb: (AsyncResult) -> Unit, crossinline exec: FXTask<*>.() -> AsyncResult) {
        runAsync(status) {
            wrapWithTryCatch {
                this.exec()
            }
        } ui (cb)
    }

    private inline fun wrapWithTryCatch(exec: () -> AsyncResult): AsyncResult {
        try {
            return exec()
        } catch (e: Exception) {
            return Error(e)
        }
    }

}