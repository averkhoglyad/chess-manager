package net.averkhoglyad.chess.manager.gui.controller;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.util.Callback;
import net.averkhoglyad.chess.manager.core.data.Paging;
import net.averkhoglyad.chess.manager.core.sdk.data.Color;
import net.averkhoglyad.chess.manager.core.sdk.data.Game;
import net.averkhoglyad.chess.manager.core.service.LichessIntegrationService;
import net.averkhoglyad.chess.manager.core.service.LichessIntegrationServiceImpl;
import net.averkhoglyad.chess.manager.gui.component.ChessDiagram;
import net.averkhoglyad.chess.manager.gui.data.ApplicationModel;
import net.averkhoglyad.chess.manager.gui.data.GameVO;
import net.averkhoglyad.chess.manager.gui.data.StatusBarModel;
import net.averkhoglyad.chess.manager.gui.event.ApplicationEventDispatcher;
import net.averkhoglyad.chess.manager.gui.event.FileEvent;
import net.averkhoglyad.chess.manager.gui.event.ViewEvent;
import net.averkhoglyad.chess.manager.gui.view.AlertHelper;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static net.averkhoglyad.chess.manager.core.helper.ExceptionHelper.doQuiet;
import static net.averkhoglyad.chess.manager.core.helper.ExceptionHelper.doStrict;

public class GamesController {

    private static final int PAGE_SIZE = 25;

    private LichessIntegrationService lichessService = new LichessIntegrationServiceImpl();

    @FXML
    private TableView<GameVO> gamesTable;
    @FXML
    private TableColumn<GameVO, Boolean> selectionColumn;
    @FXML
    private CheckBox selectionColumnCheckBox;
    @FXML
    private TableColumn<GameVO, String> detailsColumn;
    @FXML
    private TableColumn<GameVO, String> whitePlayerColumn;
    @FXML
    private TableColumn<GameVO, String> blackPlayerColumn;
    @FXML
    private TableColumn<GameVO, String> turnsColumn;
    @FXML
    private TableColumn<GameVO, String> statusColumn;
    @FXML
    private TableColumn<GameVO, String> finishedAtColumn;
    @FXML
    private ChessDiagram fenDiagram;
    @FXML
    private Label moves;

    private final ApplicationModel applicationModel = ApplicationModel.getInstance();
    private final StatusBarModel statusBarModel = StatusBarModel.getInstance();
    private final ApplicationEventDispatcher eventDispatcher = ApplicationEventDispatcher.getInstance();

    private final ObservableList<GameVO> games = FXCollections.observableArrayList();
    private final ObservableSet<Game> selectedGames = applicationModel.getSelectedGames();

    private String currentUser;

    private StringProperty displayedGameId = new SimpleStringProperty();

    private int prevSelectedItemIndex = 0;

    public void initialize() {

        // Init columns
        gamesTable.setItems(games);
        gamesTable.setSortPolicy(param -> false);

        selectionColumn.setCellFactory(column -> {
            CheckBoxTableCell<GameVO, Boolean> cell = new CheckBoxTableCell<>();
            cell.setSelectedStateCallback((Integer param) -> cell.getTableView().getItems().get(param).getSelected());
            cell.setOnMouseClicked(event -> {
                CheckBoxTableCell<GameVO, Boolean> target = (CheckBoxTableCell<GameVO, Boolean>) event.getTarget();
                int currentIndex = target.getIndex();

                if (event.isShiftDown()) {
                    boolean isSelection = selectedGames.contains(target.getTableView().getItems().get(currentIndex).getGame());

                    int from = Integer.min(prevSelectedItemIndex, currentIndex);
                    int to = Integer.max(prevSelectedItemIndex, currentIndex);

                    for (int i = from; i <= to; i++) {
                        GameVO vo = target.getTableView().getItems().get(i);
                        Game game = vo.getGame();
                        if (isSelection) {
                            selectedGames.remove(game);
                        } else {
                            selectedGames.add(game);
                        }
                    }

                } else {
                    GameVO vo = target.getTableView().getItems().get(currentIndex);
                    Game game = vo.getGame();
                    boolean isSelection = selectedGames.contains(game);
                    if (isSelection) {
                        selectedGames.remove(game);
                    } else {
                        selectedGames.add(game);
                    }
                }

                prevSelectedItemIndex = currentIndex;
            });
            return cell;
        });
        selectionColumn.setCellValueFactory(cellData -> cellData.getValue().getSelected());

        whitePlayerColumn.setCellValueFactory(createPlayerCellValueProvider(Color.white));
        blackPlayerColumn.setCellValueFactory(createPlayerCellValueProvider(Color.black));
        turnsColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(Integer.toString(((Double) Math.ceil(cellData.getValue().getGame().getTurns() / 2)).intValue())));
        statusColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getGame().getStatus().toString()));
        finishedAtColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(
            DateTimeFormatter.ofPattern("HH:mm dd-MM-yyyy")
                .format(cellData.getValue().getGame().getLastMoveAt().atZone(ZoneId.systemDefault()))
        ));
        detailsColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getGame().getId()));
        detailsColumn.setCellFactory(column -> {
            TableCell<GameVO, String> cell = new TableCell<>();
            Glyph glyph = new Glyph("FontAwesome", FontAwesome.Glyph.EYE);
            glyph.visibleProperty().bind(cell.emptyProperty().not());
            glyph.textFillProperty().bind(
                Bindings.when(cell.itemProperty().isEqualTo(displayedGameId))
                    .then(javafx.scene.paint.Color.DARKGREEN)
                    .otherwise(javafx.scene.paint.Color.BLACK)
            );
            cell.setGraphic(glyph);
            cell.setAlignment(Pos.CENTER);
            cell.setOnMouseClicked(event -> {
                TableCell<GameVO, String> target = (TableCell<GameVO, String>) event.getSource();
                displayGame(target.getItem());
            });
            return cell;
        });

        // Bind
        eventDispatcher.on(ViewEvent.SELECT_USER, o -> {
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
                    Platform.runLater(() -> {
                        AlertHelper.error(e);
                        displayedGameId.set(null);
                    });
                    return null;
                })
                .thenAccept(no -> Platform.runLater(() -> {
                    statusBarModel.setStatusProgress(0);
                    statusBarModel.setStatusText("");
                }));
        });

        selectedGames.addListener((SetChangeListener<? super Game>) c -> {
            games.forEach(it -> it.setSelected(selectedGames.contains(it.getGame())));
            List<Game> allGames = games.stream()
                .map(GameVO::getGame)
                .collect(Collectors.toList());
            selectionColumnCheckBox.setSelected(!games.isEmpty() && selectedGames.containsAll(allGames));
        });

        applicationModel.currentPageProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.intValue() < 1) return;
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
                    List<GameVO> vos = res.getCurrentPageResults().stream()
                        .map(GameVO::new)
                        .peek(vo -> vo.setSelected(selectedGames.contains(vo.getGame())))
                        .collect(Collectors.toList());
                    games.setAll(vos);
                    statusBarModel.setStatusText("");

                    List<Game> allGames = games.stream()
                        .map(GameVO::getGame)
                        .collect(Collectors.toList());
                    selectionColumnCheckBox.setSelected(!games.isEmpty() && selectedGames.containsAll(allGames));
                }))
                .exceptionally(e -> {
                    Platform.runLater(() -> AlertHelper.error(e));
                    return null;
                });
        });

        selectionColumnCheckBox.disableProperty().bind(Bindings.isEmpty(games));

        selectionColumnCheckBox.setOnMouseClicked(o -> {
            List<Game> allGames = games.stream()
                .map(GameVO::getGame)
                .collect(Collectors.toList());
            boolean notChangedYetState = selectionColumnCheckBox.isSelected();
            if (!notChangedYetState) {
                selectedGames.removeAll(allGames);
            } else {
                selectedGames.addAll(allGames);
            }
        });
    }

    private Callback<CellDataFeatures<GameVO, String>, ObservableValue<String>> createPlayerCellValueProvider(Color color) {
        return cellData -> Optional.ofNullable(cellData.getValue().getGame().getPlayers().get(color))
            .map(it -> it.getUserId() + " (" + it.getRating() + ")")
            .map(ReadOnlyStringWrapper::new)
            .orElse(null);
    }

    private void clearDisplayedGame() {
        displayedGameId.set(null);
        fenDiagram.setVisible(false);
        fenDiagram.setTitle("");
        moves.setText("");
    }

    private void displayGame(String gameId) {
        clearDisplayedGame();
        statusBarModel.setStatusText("Load game");
        displayedGameId.set(gameId);
        lichessService.getGame(gameId)
            .whenComplete((game1, throwable) -> Platform.runLater(() -> statusBarModel.setStatusText("")))
            .thenAccept(game -> Platform.runLater(() ->
            {
                displayedGameId.set(game.getId());
                String fen = game.getFenDiagrams().get(game.getFenDiagrams().size() - 1);
                fenDiagram.setVisible(true);
                fenDiagram.setFen(fen);
                fenDiagram.setTitle(game.getPlayers().get(Color.white).getUserId() + " - " + game.getPlayers().get(Color.black).getUserId());
                fenDiagram.setFlipped(currentUser.equals(game.getPlayers().get(Color.black).getUserId()));
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
                moves.setText(prepareMovesNotation(game.getMoves(), result));
            }))
            .exceptionally(e -> {
                Platform.runLater(() -> {
                    AlertHelper.error(e);
                    displayedGameId.set(null);
                });
                return null;
            });
    }

    private String prepareMovesNotation(String raw, String result) {
        StringBuilder builder = new StringBuilder();
        String[] halfMoves = raw.split("\\s");
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
        builder.append(result);
        return builder.toString();
    }

}
