package net.averkhoglyad.chess.manager.gui

import javafx.scene.image.Image
import net.averkhoglyad.chess.manager.core.service.LichessIntegrationServiceImpl
import net.averkhoglyad.chess.manager.gui.layout.RootLayout
import net.averkhoglyad.chess.manager.gui.util.PicoDIContainer
import net.averkhoglyad.chess.manager.gui.util.ProfilesRepositoryImpl
import org.controlsfx.glyphfont.FontAwesome
import org.controlsfx.glyphfont.GlyphFontRegistry
import org.picocontainer.DefaultPicoContainer
import org.slf4j.bridge.SLF4JBridgeHandler
import tornadofx.*
import java.awt.SplashScreen
import java.nio.file.Files
import java.nio.file.Paths

class ChessManagerApp : App(RootLayout::class) {

    private val workDir = Paths.get(System.getProperty("user.home"), ".chess-manager")

    init {
        SLF4JBridgeHandler.removeHandlersForRootLogger()
        SLF4JBridgeHandler.install()
        Files.createDirectories(workDir)
        addStageIcon(Image(resources.stream("/images/icon.png")))
        GlyphFontRegistry.register(FontAwesome(resources.stream("/org/controlsfx/glyphfont/fontawesome-webfont.ttf")))
        FX.dicontainer = createDIContainer()
        SplashScreen.getSplashScreen()?.close()
    }

    private fun createDIContainer(): DIContainer {
        val pico = DefaultPicoContainer()
        pico.addComponent(LichessIntegrationServiceImpl())
        pico.addComponent(ProfilesRepositoryImpl(workDir.resolve("profiles")))
        return PicoDIContainer(pico)
    }

}
