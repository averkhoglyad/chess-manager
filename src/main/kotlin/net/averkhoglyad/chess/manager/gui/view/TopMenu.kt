package net.averkhoglyad.chess.manager.gui.view

import javafx.beans.binding.Bindings.*
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleSetProperty
import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.geometry.Pos
import javafx.scene.control.MenuItem
import javafx.scene.control.Tooltip
import net.averkhoglyad.chess.manager.core.data.Profile
import net.averkhoglyad.chess.manager.core.sdk.lichess.data.Game
import net.averkhoglyad.chess.manager.gui.util.fontawesome
import net.averkhoglyad.chess.manager.core.util.noop0
import net.averkhoglyad.chess.manager.core.util.noop1
import net.averkhoglyad.chess.manager.gui.util.splitmenubutton
import org.controlsfx.glyphfont.FontAwesome.Glyph.*
import tornadofx.*

class TopMenu : View() {

    val currentPageProperty = SimpleIntegerProperty(0)
    var currentPage by currentPageProperty

    val totalPagesProperty = SimpleIntegerProperty(0)
    var totalPages by totalPagesProperty

    val profiles = SimpleListProperty<Profile>(FXCollections.observableArrayList<Profile>())
    val selectedGames = SimpleSetProperty<Game>(FXCollections.observableSet())

    private var onProfileManager: () -> Unit = noop0

    private var onSelectProfile: ((Profile) -> Unit) = noop1
    private var onPageChange: ((Int) -> Unit) = noop1

    private var onImportGames: () -> Unit = noop0
    private var onClearSelectedGames: () -> Unit = noop0

    private val profilesMenu = splitmenubutton {
        tooltip = Tooltip("Select profile")
        graphic = fontawesome(USERS)
        item("No user added") {
            isDisable = true
            visibleWhen { isEmpty(profiles) }
        }
        action {
            onProfileManager()
        }
    }

    override val root = vbox {
        menubar {
            menu("_Help") {
                item("_Resources") {
                    action {
                        information(title = "Used resources and technologies", header = "", content = "Platform: Kotlin, JavaFX/TornadoFX\n" +
                                "Compiler: ExcelsiorJet 14.0\n" +
                                "Components: ControlsFX\n" +
                                "Icons and Images: FontAwesome, Veryicon")
                    }
                }
                item("_About") {
                    action {
                        information(title = "About", header = "", content = "Free tool to download games from lichess.com as single PGN file.\n\nCurrent version: 0.1-ALFA\nFeedback email: awer.doc@gmail.com")
                    }
                }
            }
        }
        toolbar {
            this += profilesMenu
            splitmenubutton {
                textProperty().bind(
                        `when`(size(selectedGames).eq(0))
                                .then("No games")
                                .otherwise(concat(size(selectedGames), `when`(size(selectedGames).eq(1)).then(" game").otherwise(" games"))))
                tooltip = Tooltip("Download selected games as PGN")
                graphic = fontawesome(DOWNLOAD)
                enableWhen { size(selectedGames).greaterThan(0) }
                action {
                    onImportGames()
                }
                item("Import").action {
                    onImportGames()
                }
                item("Clear").action {
                    onClearSelectedGames()
                }
            }
            spacer()
            button {
                prefWidth = 25.0
                alignment = Pos.CENTER
                graphic = fontawesome(LONG_ARROW_LEFT) {
                    alignment = Pos.TOP_CENTER
                    fontSize = 10.0
                }
                visibleWhen { totalPagesProperty.greaterThan(1) }
                disableWhen { currentPageProperty.eq(1) }
                action {
                    onPageChange(currentPage - 1)
                }
            }
            label {
                // TODO: Add control to navigate to any page
                textProperty().bind(concat(currentPageProperty, " / ", totalPagesProperty))
                visibleWhen { totalPagesProperty.greaterThan(0) }
            }
            button {
                graphic = fontawesome(LONG_ARROW_RIGHT) {
                    alignment = Pos.TOP_CENTER
                    fontSize = 10.0
                }
                prefWidth = 25.0
                alignment = Pos.CENTER
                visibleWhen { totalPagesProperty.greaterThan(1) }
                disableWhen { currentPageProperty.eq(totalPagesProperty) }
                action {
                    onPageChange(currentPage + 1)
                }
            }
        }
    }

    init {
        profiles.addListener(ListChangeListener<Profile> { c ->
            val noProfilesItem = profilesMenu.items[0]
            profilesMenu.items.setAll(noProfilesItem)
            c.list.sortedBy(Profile::lichessId)
                    .map<Profile, MenuItem> {
                        val item = MenuItem()
                        item.text = it.lichessId
                        item.userData = it
                        item.action { onSelectProfile(it) }
                        return@map item
                    }
                    .forEach {
                        profilesMenu.items.add(it)
                    }
        })
    }

    fun onManageProfiles(op: () -> Unit) {
        onProfileManager = op
    }

    fun onSelectProfile(op: (Profile) -> Unit) {
        onSelectProfile = op
    }

    fun onPageChange(op: (Int) -> Unit) {
        onPageChange = op
    }

    fun onImportGames(op: () -> Unit) {
        onImportGames = op
    }

    fun onClearSelectedGames(op: () -> Unit) {
        onClearSelectedGames = op
    }

}
