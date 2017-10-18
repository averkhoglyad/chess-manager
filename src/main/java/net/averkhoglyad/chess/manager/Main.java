package net.averkhoglyad.chess.manager;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import net.averkhoglyad.chess.manager.core.service.LichessIntegrationService;
import net.averkhoglyad.chess.manager.core.service.LichessIntegrationServiceImpl;
import net.averkhoglyad.chess.manager.core.service.ProfileService;
import net.averkhoglyad.chess.manager.core.service.ProfileServiceImpl;
import net.averkhoglyad.chess.manager.gui.controller.RootController;
import net.averkhoglyad.chess.manager.gui.view.AlertHelper;
import net.averkhoglyad.chess.manager.gui.view.PopupFactory;
import net.averkhoglyad.chess.manager.gui.view.ViewFactory;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.GlyphFontRegistry;

import java.awt.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Optional;

import static net.averkhoglyad.chess.manager.core.helper.ExceptionHelper.doStrict;

@Slf4j
public class Main extends Application {

    private static final Path workDir = Paths.get(System.getenv("LOCALAPPDATA"), "chess-manager");

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        doStrict(() -> Files.createDirectories(workDir));
        Thread.currentThread().setUncaughtExceptionHandler((thread, ex) -> {
            log.error("Unexpected exception:", ex);
            AlertHelper.error(ex);
        });
        GlyphFontRegistry.register(new FontAwesome(getClass().getClassLoader().getResource("org/controlsfx/glyphfont/fontawesome-webfont.ttf").toString()));

        initPrimaryStage(stage);
        ViewFactory viewFactory = createViewFactory(stage);
        Parent root = viewFactory.loadFxmlView("net/averkhoglyad/chess/manager/gui/view/RootLayout.fxml");
        stage.setScene(new Scene(root));
        stage.show();
        Optional.ofNullable(SplashScreen.getSplashScreen()).ifPresent(SplashScreen::close);
    }

    private ViewFactory createViewFactory(Stage stage) {
        LichessIntegrationService lichessService = new LichessIntegrationServiceImpl();
        ProfileService profileService = new ProfileServiceImpl(workDir.resolve("profiles"));
        PopupFactory popupFactory = new PopupFactory(stage);
        RootController rootController = new RootController(popupFactory, lichessService, profileService);
        return new ViewFactory(Collections.singletonList(rootController));
    }

    private Stage initPrimaryStage(Stage primaryStage) {
        Image icon = new Image(getClass().getClassLoader().getResourceAsStream("images/icon.png"));
        primaryStage.getIcons().add(icon);
        primaryStage.setTitle("Chess Manager");
        primaryStage.setHeight(600);
        primaryStage.setWidth(900);
        return primaryStage;
    }

}
