package net.averkhoglyad.chess.manager.gui.controller;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.stage.Stage;
import javafx.util.Pair;
import net.averkhoglyad.chess.manager.core.data.Paging;
import net.averkhoglyad.chess.manager.core.sdk.data.Color;
import net.averkhoglyad.chess.manager.core.sdk.data.Game;
import net.averkhoglyad.chess.manager.core.sdk.data.Player;
import net.averkhoglyad.chess.manager.core.sdk.data.User;
import net.averkhoglyad.chess.manager.core.service.LichessIntegrationService;
import net.averkhoglyad.chess.manager.core.service.ProfileService;
import net.averkhoglyad.chess.manager.gui.component.GamePreview;
import net.averkhoglyad.chess.manager.gui.component.GamesTable;
import net.averkhoglyad.chess.manager.gui.component.ProfilesManager;
import net.averkhoglyad.chess.manager.gui.component.TopMenu;
import net.averkhoglyad.chess.manager.gui.event.DataCollectionEvent;
import net.averkhoglyad.chess.manager.gui.event.DataEvent;
import net.averkhoglyad.chess.manager.gui.view.AlertHelper;
import net.averkhoglyad.chess.manager.gui.view.PopupFactory;
import org.controlsfx.control.StatusBar;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static net.averkhoglyad.chess.manager.core.helper.ExceptionHelper.doQuiet;
import static net.averkhoglyad.chess.manager.core.helper.ExceptionHelper.doStrict;

public class RootController {

    private static final int PAGE_SIZE = 25;

    private final PopupFactory popupFactory;
    private final LichessIntegrationService lichessService;
    private final ProfileService profileService;

    // Props
    private ObservableSet<Game> selectedGames = FXCollections.observableSet();
    private String currentUser;
    private ObservableList<User> profiles;

    // Nodes
    @FXML
    private TopMenu topMenu;
    @FXML
    private GamesTable gamesTable;
    @FXML
    private GamePreview gamePreview;
    @FXML
    private StatusBar statusBar;
    @FXML
    private ProfilesManager profilesManager;

    private Stage profilesPopup;

    private final IntegerProperty currentPage = new SimpleIntegerProperty(0);
    private final IntegerProperty totalPages = new SimpleIntegerProperty(0);

    public RootController(PopupFactory popupFactory, LichessIntegrationService lichessService, ProfileService profileService) {
        this.popupFactory = popupFactory;
        this.lichessService = lichessService;
        this.profileService = profileService;
    }

    public void initialize() {
        profiles = FXCollections.observableList(profileService.load());
        profilesManager.setProfiles(profiles);
        profilesPopup = popupFactory.create("Manage profiles", profilesManager);

        // Top Menu
        Bindings.bindContent(topMenu.getUsers(), profiles);
        topMenu.currentPageProperty().bind(currentPage);
        topMenu.totalPagesProperty().bind(totalPages);
        topMenu.selectedGamesCountProperty().bind(Bindings.size(selectedGames));

        // Games
        selectedGames.addListener((SetChangeListener<? super Game>) c ->
            gamesTable.setSelectedGames(
                selectedGames.stream()
                    .map(Game::getId)
                    .collect(Collectors.toCollection(() -> FXCollections.observableSet()))
            ));

        currentPage.addListener((observable, oldValue, newValue) -> {
            if (newValue.intValue() < 1) return;
            clearDisplayedGame();
            gamesTable.setGames(FXCollections.emptyObservableList());
            gamesTable.setLoading(true);
            Paging paging = Paging.builder()
                .page(currentPage.get())
                .pageSize(PAGE_SIZE)
                .build();

            lichessService.getUserGames(currentUser, paging)
                .thenAccept(res -> Platform.runLater(() ->
                {
                    clearDisplayedGame();
                    totalPages.set(res.getNbPages());
                    gamesTable.setGames(FXCollections.observableList(res.getCurrentPageResults()));
                }))
                .exceptionally(e -> {
                    Platform.runLater(() -> AlertHelper.error(e));
                    return null;
                })
                .whenComplete((res, ex) -> Platform.runLater(() -> gamesTable.setLoading(false)));
        });
    }

    public void selectUser(DataEvent<User> event) {
        User selectedUser = event.getValue();
        currentPage.set(0);
        selectedGames.clear();
        currentUser = selectedUser.getUsername();
        currentPage.set(1);
    }

    public void changePage(DataEvent<Integer> event) {
        int page = event.getValue();
        if (page >= 1 && page <= topMenu.getTotalPages()) {
            currentPage.set(page);
        }
    }

    public void importPgn(Event event) {
        popupFactory.fileChooser(".pgn", new Pair("PGN file", "*.pgn"))
            .showSaveDialog()
            .map(it -> it.endsWith(".pgn") ? it : it.getParent().resolve(it.getFileName().toString() + ".pgn"))
            .ifPresent(target -> {
                if (Files.exists(target) && !Files.isWritable(target)) {
                    AlertHelper.error("Error on games import", "Target file is not writable.\nFix problem or select other file and try again");
                    return;
                }

                statusBar.setText("Importing selected games");
                SimpleIntegerProperty progress = new SimpleIntegerProperty(0);
                List<Game> targetGames = new ArrayList<>(selectedGames);
                double total = targetGames.size();
                statusBar.progressProperty().bind(
                    Bindings.when(progress.isEqualTo(0)).then(total / 1000).otherwise(progress.divide(total))
                );
                selectedGames.clear();

                CompletableFuture.runAsync(() -> {
                    Path tempPath = doStrict(() -> Files.createTempFile("lichess.", ".pgn"));
                    try (OutputStream outputStream = Files.newOutputStream(tempPath);
                         PrintStream tempFilePrint = new PrintStream(outputStream)) {
                        AtomicInteger inc = new AtomicInteger();
                        targetGames.stream()
                            .sorted(Comparator.comparing(Game::getCreatedAt))
                            .map(game -> lichessService.loadGamePgn(game.getId()))
                            .forEachOrdered(pgn -> {
                                tempFilePrint.println(pgn);
                                tempFilePrint.println();
                                tempFilePrint.println();
                                Platform.runLater(() -> progress.set(inc.incrementAndGet()));
                            });
                        tempFilePrint.flush();
                        Files.deleteIfExists(target);
                        Files.copy(tempPath, target);
                    } catch (IOException e) {
                        Platform.runLater(() -> AlertHelper.error(e));
                    } finally {
                        doQuiet(() -> Files.delete(tempPath));
                        Platform.runLater(() -> {
                            statusBar.progressProperty().unbind();
                            statusBar.progressProperty().set(0);
                            statusBar.setText("");
                        });
                    }
                });
            });
    }

    public void clearSelectedGames(Event event) {
        selectedGames.clear();
    }

    public void manageUsers(Event event) {
        profilesManager.reset();
        profilesPopup.show();
    }

    private void clearDisplayedGame() {
        gamesTable.setDisplayedGame(null);
        gamePreview.setGame(null);
    }

    public void displayGame(DataEvent<Game> event) {
        clearDisplayedGame();
        gamesTable.setDisplayedGame(event.getValue());
        gamePreview.setLoading(true);
        lichessService.getGame(event.getValue().getId())
            .thenAccept(game ->
                Platform.runLater(() -> {
                    gamesTable.setDisplayedGame(game);
                    gamePreview.setGame(game);
                    Player blackPlayer = game.getPlayers().get(Color.black);
                    gamePreview.setFlipped(currentUser.equals(blackPlayer.getUserId()));
                }))
            .exceptionally(e -> {
                Platform.runLater(() -> {
                    AlertHelper.error(e);
                    clearDisplayedGame();
                });
                return null;
            })
            .whenComplete((game, throwable) ->
                Platform.runLater(() -> gamePreview.setLoading(false))
            );
    }

    public void selectGames(DataCollectionEvent<Game, ?> event) {
        selectedGames.addAll(event.getValue());
    }

    public void deselectGames(DataCollectionEvent<Game, ?> event) {
        selectedGames.removeAll(event.getValue());
    }

    public void addProfile(DataEvent<String> event) {
        String username = event.getValue();
        boolean userAbsent = profiles.stream()
            .map(User::getUsername)
            .noneMatch(it -> it.equals(username));
        if (userAbsent)
        {
            profiles.add(new User(username));
            profileService.save(profiles);
        }
    }

    public void dropProfile(DataEvent<User> event) {
        User user = event.getValue();
        profiles.removeIf(it -> it.getUsername().equals(user.getUsername()));
        profileService.save(profiles);
    }

}
