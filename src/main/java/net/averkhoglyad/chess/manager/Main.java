package net.averkhoglyad.chess.manager;

import javafx.application.Application;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import net.averkhoglyad.chess.manager.core.sdk.data.User;
import net.averkhoglyad.chess.manager.gui.data.ApplicationModel;
import net.averkhoglyad.chess.manager.gui.event.ApplicationEventDispatcher;
import net.averkhoglyad.chess.manager.gui.event.FileEvent;
import net.averkhoglyad.chess.manager.gui.event.ViewEvent;
import net.averkhoglyad.chess.manager.gui.view.AlertHelper;
import net.averkhoglyad.chess.manager.gui.view.ViewHelper;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.GlyphFontRegistry;

import java.awt.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static javafx.stage.Modality.WINDOW_MODAL;
import static net.averkhoglyad.chess.manager.core.helper.ExceptionHelper.doStrict;

@Slf4j
public class Main extends Application {

    public static final Path profilesFile = Paths.get(System.getenv("LOCALAPPDATA"), "chess-manager", "profiles");

    private Stage primaryStage;
    private Stage addUserStage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Thread.currentThread().setUncaughtExceptionHandler((thread, ex) -> {
            log.error("Unexpected exception:", ex);
            AlertHelper.error(ex);
        });
        this.primaryStage = primaryStage;
        this.addUserStage = initAddUserStage(primaryStage);
        GlyphFontRegistry.register(new FontAwesome(getClass().getClassLoader().getResource("org/controlsfx/glyphfont/fontawesome-webfont.ttf").toString()));
        initPrimaryStage(primaryStage);
        bindViewEvents();
        primaryStage.show();
        Optional.ofNullable(SplashScreen.getSplashScreen()).ifPresent(SplashScreen::close);
        loadProfiles();
    }

    private Stage initPrimaryStage(Stage primaryStage) {
        Image icon = new Image(getClass().getClassLoader().getResourceAsStream("images/icon.png"));
        primaryStage.getIcons().add(icon);
        primaryStage.titleProperty().bind(getTitleProperty());
        primaryStage.setHeight(600);
        primaryStage.setWidth(900);
        Scene scene = createMainScene();
        primaryStage.setScene(scene);
        return primaryStage;
    }

    private Stage initAddUserStage(Stage primaryStage) {
        Stage stage = new Stage();
        stage.setTitle("Add lichess profile");
        stage.initModality(WINDOW_MODAL);
        stage.initOwner(primaryStage);
        stage.setResizable(false);
        Pane pane = ViewHelper.loadFxmlView("net/averkhoglyad/chess/manager/gui/view/AddUserPopup.fxml");
        Scene scene = new Scene(pane);
        stage.setScene(scene);
        return stage;
    }

    private ObservableValue<String> getTitleProperty() {
        return new ReadOnlyStringWrapper("Chess Manager");
    }

    private Scene createMainScene() {
        Parent rootLayout = ViewHelper.loadFxmlView("net/averkhoglyad/chess/manager/gui/view/RootLayout.fxml");
        Scene scene = new Scene(rootLayout);
        return scene;
    }

    private void bindViewEvents() {
        ApplicationEventDispatcher.getInstance().on(ViewEvent.SHOW_IMPORT_FILE_POPUP, this::showPgnImportPopup);
        ApplicationEventDispatcher.getInstance().on(ViewEvent.SHOW_MANAGE_USERS_POPUP, () -> addUserStage.show());
        ApplicationEventDispatcher.getInstance().on(ViewEvent.CLOSE_ADD_USER_POPUP, () -> addUserStage.close());
    }

    private void showPgnImportPopup(Object o) {
        FileChooser fileChooser = createPgnFileChooser();
        fileChooser.setInitialFileName(".pgn");
        File file = fileChooser.showSaveDialog(primaryStage);
        if (file == null) return; // Nothing selected
        if (!file.getPath().endsWith(".pgn")) file = new File(file.getPath() + ".pgn");
        ApplicationEventDispatcher.getInstance().trigger((FileEvent) o, file.toPath());
    }

    private FileChooser createPgnFileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters()
            .add(new FileChooser.ExtensionFilter("PGN file", "*.pgn"));
        return fileChooser;
    }

    private void loadProfiles() {
        doStrict(() -> Files.createDirectories(profilesFile.getParent()));
        ObservableList<User> users = ApplicationModel.getInstance().getUsers();
        if (Files.exists(profilesFile)) {
            try (Stream<String> lines = doStrict(() -> Files.lines(profilesFile))) {
                List<User> profiles = lines
                    .map(User::new)
                    .collect(Collectors.toList());
                users.setAll(profiles);
            }
        }
    }

}
