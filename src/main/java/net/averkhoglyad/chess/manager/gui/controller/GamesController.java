package net.averkhoglyad.chess.manager.gui.controller;

import javafx.application.Platform;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import net.averkhoglyad.chess.manager.core.data.Paging;
import net.averkhoglyad.chess.manager.core.sdk.data.Color;
import net.averkhoglyad.chess.manager.core.sdk.data.Game;
import net.averkhoglyad.chess.manager.core.service.LichessIntegrationService;
import net.averkhoglyad.chess.manager.core.service.LichessIntegrationServiceImpl;
import net.averkhoglyad.chess.manager.gui.component.ChessDiagram;
import net.averkhoglyad.chess.manager.gui.component.GamesTable;
import net.averkhoglyad.chess.manager.gui.data.ApplicationModel;
import net.averkhoglyad.chess.manager.gui.data.StatusBarModel;
import net.averkhoglyad.chess.manager.gui.event.*;
import net.averkhoglyad.chess.manager.gui.view.AlertHelper;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static net.averkhoglyad.chess.manager.core.helper.ExceptionHelper.doQuiet;
import static net.averkhoglyad.chess.manager.core.helper.ExceptionHelper.doStrict;

public class GamesController {

    private static final int PAGE_SIZE = 25;

    private LichessIntegrationService lichessService = new LichessIntegrationServiceImpl();
    private ObservableSet<Game> selectedGames = ApplicationModel.getInstance().getSelectedGames();

    @FXML
    private GamesTable gamesTable;

    @FXML
    private ChessDiagram fenDiagram;
    @FXML
    private Label moves;

    private final ApplicationModel applicationModel = ApplicationModel.getInstance();
    private final StatusBarModel statusBarModel = StatusBarModel.getInstance();

    private final ApplicationEventDispatcher eventDispatcher = ApplicationEventDispatcher.getInstance();

    private String currentUser;

    public void initialize() {
        // Bind
        eventDispatcher.on(ViewEvent.SELECT_USER, o -> {
            applicationModel.getSelectedGames().clear();
            currentUser = o.toString();
            applicationModel.setCurrentPage(1);
        });
        eventDispatcher.on(FileEvent.IMPORT_PGN, (Object o) -> {
            Path path = (Path) o;
            if (Files.exists(path) && !Files.isWritable(path)) {
                AlertHelper.error("Error import games as PGN", "File is not writable. Fix problem ot select other file and try again");
                return;
            }

            statusBarModel.setStatusProgress(0.1 / applicationModel.getSelectedGames().size());
            statusBarModel.setStatusText("Loading selected games");
            CompletableFuture
                .supplyAsync(() -> doStrict(() -> {
                    Path temp = doStrict(() -> Files.createTempFile("lichess.", ".pgn"));
                    AtomicInteger inc = new AtomicInteger();
                    int size = applicationModel.getSelectedGames().size();
                    applicationModel.getSelectedGames()
                        .stream()
                        .sorted(Comparator.comparing(Game::getCreatedAt))
                        .map(Game::getId)
                        .map(lichessService::loadGamePgn)
                        .map(pgn -> pgn + "\n\n\n")
                        .forEach(pgn -> {
                            doStrict(() -> Files.write(temp, pgn.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND));
                            double progress = ((double) inc.incrementAndGet()) / size;
                            Platform.runLater(() -> statusBarModel.setStatusProgress(progress));
                        });
                    Platform.runLater(() -> applicationModel.getSelectedGames().clear());
                    return temp;
                }))
                .thenAccept(temp -> {
                    doStrict(() -> Files.deleteIfExists(path));
                    doStrict(() -> Files.copy(temp, path));
                    doQuiet(() -> Files.delete(temp));
                })
                .exceptionally(e -> {
                    Platform.runLater(() -> AlertHelper.error(e));
                    return null;
                })
                .thenAccept(no -> Platform.runLater(() -> {
                    statusBarModel.setStatusProgress(0);
                    statusBarModel.setStatusText("");
                }));
        });

        selectedGames.addListener((SetChangeListener<? super Game>) c ->
            gamesTable.setSelectedGames(
                selectedGames.stream()
                    .map(Game::getId)
                    .collect(Collectors.toSet())
            ));

        applicationModel.currentPageProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.intValue() < 1) return;
            clearDisplayedGame();
            gamesTable.setGames(Collections.emptyList());
            gamesTable.setLoading(true);
            statusBarModel.setStatusText("Load games for " + currentUser);
            Paging paging = Paging.builder()
                .page(applicationModel.getCurrentPage())
                .pageSize(PAGE_SIZE)
                .build();

            lichessService.getUserGames(currentUser, paging)
                .thenAccept(res -> Platform.runLater(() ->
                {
                    clearDisplayedGame();
                    applicationModel.setTotalPages(res.getNbPages());
                    gamesTable.setGames(res.getCurrentPageResults());
                    statusBarModel.setStatusText("");
                }))
                .exceptionally(e -> {
                    Platform.runLater(() -> AlertHelper.error(e));
                    return null;
                })
                .whenComplete((res, ex) -> Platform.runLater(() -> gamesTable.setLoading(false)));
        });
    }

    private void clearDisplayedGame() {
        gamesTable.setDisplayedGame(null);
        fenDiagram.setVisible(false);
        fenDiagram.setTitle("");
        moves.setText("");
    }

    public void displayGame(DataEvent<Game> event) {
        clearDisplayedGame();
        gamesTable.setDisplayedGame(event.getValue());
        statusBarModel.setStatusText("Load game");
        lichessService.getGame(event.getValue().getId())
            .thenAccept(game ->
                Platform.runLater(() -> {
                    gamesTable.setDisplayedGame(game);
                    String fen = game.getFenDiagrams().get(game.getFenDiagrams().size() - 1);
                    fenDiagram.setVisible(true);
                    fenDiagram.setFen(fen);
                    fenDiagram.setTitle(game.getPlayers().get(Color.white).getUserId() + " - " + game.getPlayers().get(Color.black).getUserId());
                    fenDiagram.setFlipped(currentUser.equals(game.getPlayers().get(Color.black).getUserId()));
                    moves.setText(prepareMovesNotation(game));
                }))
            .exceptionally(e -> {
                Platform.runLater(() -> {
                    AlertHelper.error(e);
                    clearDisplayedGame();
                });
                return null;
            })
            .whenComplete((game, throwable) ->
                Platform.runLater(() -> statusBarModel.setStatusText(""))
            );
    }

    private String prepareMovesNotation(Game game) {
        StringBuilder builder = new StringBuilder();
        String[] halfMoves = game.getMoves().split("\\s");
        for (int i = 0; i < halfMoves.length; i += 2) {
            builder.append(i / 2 + 1);
            builder.append('.');
            builder.append(' ');
            builder.append(halfMoves[i]);
            builder.append(' ');
            if (i + 1 < halfMoves.length) {
                builder.append(halfMoves[i + 1]);
            }
            builder.append(' ');
        }
        builder.append(' ');
        builder.append(parseNotationResult(game));
        return builder.toString();
    }

    private String parseNotationResult(Game game) {
        String result = "*";
        if (game.getWinner() == Color.white) {
            result = "1-0";
        }
        if (game.getWinner() == Color.black) {
            result = "0-1";
        }
        if (game.getStatus().isDrawn()) {
            result = "½-½";
        }
        return result;
    }

    public void selectGames(DataCollectionEvent<Game, ?> event) {
        selectedGames.addAll(event.getValue());
    }

    public void deselectGames(DataCollectionEvent<Game, ?> event) {
        selectedGames.removeAll(event.getValue());
    }

}
