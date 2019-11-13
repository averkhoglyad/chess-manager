package net.averkhoglyad.chess.manager.gui.layout

import javafx.beans.property.SimpleSetProperty
import javafx.collections.FXCollections
import javafx.geometry.Side
import javafx.stage.FileChooser
import javafx.stage.StageStyle
import net.averkhoglyad.chess.manager.core.data.Page
import net.averkhoglyad.chess.manager.core.data.Paging
import net.averkhoglyad.chess.manager.core.data.Profile
import net.averkhoglyad.chess.manager.core.sdk.lichess.data.Game
import net.averkhoglyad.chess.manager.gui.controller.RootController
import net.averkhoglyad.chess.manager.gui.data.AsyncResult.*
import net.averkhoglyad.chess.manager.gui.fragment.ProfilesManager
import net.averkhoglyad.chess.manager.gui.util.ProfilesRepository
import net.averkhoglyad.chess.manager.gui.util.animated
import net.averkhoglyad.chess.manager.gui.util.masterdetailpane
import net.averkhoglyad.chess.manager.gui.view.GamesTableView
import net.averkhoglyad.chess.manager.gui.view.PreviewView
import net.averkhoglyad.chess.manager.gui.view.TopMenu
import org.controlsfx.control.Notifications
import org.controlsfx.control.StatusBar
import tornadofx.*
import java.io.File

private val pgnFilters = arrayOf(FileChooser.ExtensionFilter("PGN file", "*.pgn"))

class RootLayout : View("Chess Manager") {

    private val controller by inject<RootController>()
    private val profilesRepo by di<ProfilesRepository>()

    private val pageSize by param(100) // TODO: inject from params/properties

    private val topMenu by inject<TopMenu>()
    private val gamesTable by inject<GamesTableView>()
    private val gamePreview by inject<PreviewView>()

    private val statusBar = StatusBar()

    private var currentProfile: Profile? = null
    private var displayedGame: Game? = null

    private val selectedGames = SimpleSetProperty<Game>(FXCollections.observableSet())

    private var initDirToImportFile = File("/")

    ///
    override val root = borderpane {
        top = topMenu.root
        center = masterdetailpane(Side.RIGHT) {
            dividerPosition = 0.7
            masterNode = gamesTable.root
            detailNode = gamePreview.root
            animated = true
        }
        bottom = statusBar.apply {
            text = ""
        }
    }
    ///

    init {
        primaryStage.apply {
            width = 900.0
            height = 600.0
        }

        topMenu.profiles.value = profilesRepo.list().sortedBy { it.lichessId }.asObservable()
        topMenu.onManageProfiles {
            val sortedProfiles = profilesRepo.list().sortedBy { it.lichessId }
            val profileManager = find<ProfilesManager>(mapOf(ProfilesManager::profiles to sortedProfiles))
            profileManager.onAdd {
                topMenu.profiles.setAll(profilesRepo.put(it))
            }
            profileManager.onDrop {
                topMenu.profiles.setAll(profilesRepo.drop(it))
            }
            profileManager.openModal(stageStyle = StageStyle.UTILITY, resizable = false)
        }
        topMenu.onPageChange { page ->
            toPage(page)
        }
        topMenu.selectedGames.bind(selectedGames)
        topMenu.onClearSelectedGames {
            selectedGames.clear()
        }
        topMenu.onImportGames {
            val file: File = chooseFile(owner = primaryStage, mode = FileChooserMode.Save, filters = pgnFilters) {
                this.initialDirectory = initDirToImportFile
            }.firstOrNull() ?: return@onImportGames

            initDirToImportFile = file.parentFile

            val path = if (file.name.endsWith(".pgn")) {
                file.toPath()
            } else {
                file.toPath().parent.resolve(file.name + ".pgn")
            }
            statusBar.text = "Importing selected games"
            statusBar.progress = -1.0
            val targetGames = selectedGames.toList()
            selectedGames.clear()
            val status = controller.loadGamesToFileAsPgn(path, targetGames) { res ->
                statusBar.text = ""
                statusBar.progressProperty().unbind()
                statusBar.progress = 0.0
                when (res) {
                    is Error -> error(title = "Error on games import", header = "", content = "Unexpected I/O error.\n${res.ex.message}")
                    else -> Notifications.create()
                            .title("Success")
                            .text("Import completed successfully")
                            .showInformation()
                }
            }
            statusBar.progressProperty().bind(status.progress)
        }
        topMenu.onSelectProfile { profile ->
            displayedGame = null
            currentProfile = profile
            gamePreview.game = null
            gamePreview.currentUserId = profile.lichessId
            gamesTable.selectedGames.clear()
            topMenu.currentPage = 0
            topMenu.totalPages = 0
            toPage(1)
        }

        gamesTable.selectedGames.bind(selectedGames)
        gamesTable.onPreview {
            if (displayedGame == it) return@onPreview

            displayedGame = it
            gamePreview.game = null
            gamePreview.loading = true
            controller.loadGame(it.id) { res ->
                if (it.id != displayedGame?.id) return@loadGame
                gamePreview.loading = false
                when (res) {
                    Empty -> warning(title = "Not found", header = "", content = "Game not found")
                    is Error -> throw res.ex
                    is Success -> gamePreview.game = res.data as? Game
                }
            }
        }
        gamesTable.onSelect { games ->
            selectedGames.addAll(games)
        }
        gamesTable.onDeselect { games ->
            selectedGames.removeAll(games)
        }
    }

    private fun toPage(pageNumber: Int) {
        currentProfile?.let {
            topMenu.currentPage = pageNumber
            gamesTable.games.clear()
            gamesTable.loading = true
            val paging = page(pageNumber)
            controller.loadGames(it, paging) { res ->
                if (it != currentProfile || paging.page != topMenu.currentPage) {
                    return@loadGames
                }
                gamesTable.loading = false
                when (res) {
                    Empty -> warning(title = "Not found", header = "", content = "Games for requested profile not found")
                    is Error -> throw res.ex
                    is Success -> {
                        val page = (res.data as? Page<Game>) ?: return@loadGames
                        gamesTable.games = page.items.asObservable()
                        topMenu.totalPages = page.total
                    }
                }
            }
        }
    }

    private fun page(number: Int): Paging = Paging((number - 1) * pageSize, pageSize)
}
