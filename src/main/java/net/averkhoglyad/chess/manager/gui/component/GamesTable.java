package net.averkhoglyad.chess.manager.gui.component;

import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.collections.*;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.util.Callback;
import net.averkhoglyad.chess.manager.core.helper.CollectionHelper;
import net.averkhoglyad.chess.manager.core.sdk.data.Color;
import net.averkhoglyad.chess.manager.core.sdk.data.Game;
import net.averkhoglyad.chess.manager.gui.data.GameVO;
import net.averkhoglyad.chess.manager.gui.event.DataEvent;
import net.averkhoglyad.chess.manager.gui.event.DataListEvent;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class GamesTable extends BaseComponent {

    private static final EventType<DataEvent<Game>> DISPLAY_GAME = new EventType<>("displayGame");
    private static final EventType<DataListEvent<Game>> SELECT_GAMES = new EventType<>("selectGames");
    private static final EventType<DataListEvent<Game>> DESELECT_GAMES = new EventType<>("deselectGames");

    // Properties
    private BooleanProperty loading = new SimpleBooleanProperty(this, "loading");
    private ListProperty<Game> games = new SimpleListProperty<>(this, "games", FXCollections.observableArrayList());
    private SetProperty<String> selectedGames = new SimpleSetProperty<>(this, "selectedGames", FXCollections.observableSet());
    private ObjectProperty<Game> displayedGame = new SimpleObjectProperty<>(this, "displayedGame");

    private ObservableList<GameVO> gameVOs = FXCollections.observableArrayList();

    // Events
    private ObjectProperty<EventHandler<DataEvent<Game>>> onDisplayGame = new EventHandlerProperty<>(DISPLAY_GAME);
    private ObjectProperty<EventHandler<DataListEvent<Game>>> onSelectGames = new EventHandlerProperty<>(SELECT_GAMES);
    private ObjectProperty<EventHandler<DataListEvent<Game>>> onDeselectGames = new EventHandlerProperty<>(DESELECT_GAMES);

    // Nodes
    @FXML
    private TableView<GameVO> gamesTable;
    @FXML
    private CheckBox selectionColumnCheckBox;
    @FXML
    private TableColumn<GameVO, Boolean> selectionColumn;
    @FXML
    private TableColumn<GameVO, Game> detailsColumn;
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

    private int prevSelectedItemIndex = 0;

    public GamesTable() {
        super("net/averkhoglyad/chess/manager/gui/component/GamesTable.fxml");
    }

    public void initialize() {
        fitContentToComponentSize();

        games.addListener((ListChangeListener<? super Game>) evt -> {
            prevSelectedItemIndex = 0;
            gameVOs.clear();
            games.get().stream()
                .map(game -> new GameVO(game, selectedGames.get().contains(game.getId())))
                .forEach(gameVOs::add);
            selectionColumnCheckBox.setSelected(
                CollectionHelper.isNotEmpty(gameVOs) && gameVOs.stream().allMatch(GameVO::isSelected)
            );
        });

        selectedGames.addListener((SetChangeListener<? super String>) evt -> {
            gameVOs.forEach(it -> it.setSelected(selectedGames.get().contains(it.getId())));
            selectionColumnCheckBox.setSelected(gameVOs.stream().allMatch(GameVO::isSelected));
        });

        gamesTable.setItems(gameVOs);
        gamesTable.setSortPolicy(param -> false);

        selectionColumn.setCellValueFactory(cellData -> cellData.getValue().selectedProperty());
        selectionColumn.setCellFactory(column -> {
            CheckBoxTableCell<GameVO, Boolean> cell = new CheckBoxTableCell<>();
            cell.setOnMouseClicked(event -> {
                @SuppressWarnings("unchecked")
                CheckBoxTableCell<GameVO, Boolean> target = (CheckBoxTableCell<GameVO, Boolean>) event.getTarget();
                int currentIndex = target.getIndex();

                GameVO clickedVO = target.getTableView().getItems().get(currentIndex);
                boolean isSelection = !selectedGames.get().contains(clickedVO.getId());
                List<Game> targetGames;
                if (event.isShiftDown()) {
                    int from = Integer.min(prevSelectedItemIndex, currentIndex);
                    int to = Integer.max(prevSelectedItemIndex, currentIndex);
                    targetGames = new ArrayList<>(to - from + 1);
                    for (int i = from; i <= to; i++) {
                        GameVO vo = target.getTableView().getItems().get(i);
                        targetGames.add(vo.getGame());
                    }
                } else {
                    Game game = clickedVO.getGame();
                    targetGames = Collections.singletonList(game);
                }

                prevSelectedItemIndex = currentIndex;

                if (isSelection) {
                    fireEvent(new DataListEvent<>(SELECT_GAMES, targetGames));
                } else {
                    fireEvent(new DataListEvent<>(DESELECT_GAMES, targetGames));
                }

            });
            return cell;
        });

        selectionColumnCheckBox.disableProperty().bind(Bindings.isEmpty(gameVOs));
        selectionColumnCheckBox.setOnMouseClicked(o -> {
            boolean needSelect = selectionColumnCheckBox.isSelected();
            EventType<DataListEvent<Game>> eventType = needSelect ? SELECT_GAMES : DESELECT_GAMES;
            fireEvent(new DataListEvent<>(eventType, games.get()));
        });

        whitePlayerColumn.setCellValueFactory(createPlayerCellValueProvider(Color.white));
        blackPlayerColumn.setCellValueFactory(createPlayerCellValueProvider(Color.black));

        turnsColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(Integer.toString(((Double) Math.ceil(cellData.getValue().getTurns() / 2)).intValue())));
        statusColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getStatus().toString()));
        finishedAtColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(
            DateTimeFormatter.ofPattern("HH:mm dd-MM-yyyy")
                .format(cellData.getValue().getLastMoveAt().atZone(ZoneId.systemDefault()))
        ));

        detailsColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getGame()));

        detailsColumn.setCellFactory((TableColumn<GameVO, Game> column) -> {
            TableCell<GameVO, Game> cell = new TableCell<>();
            Glyph glyph = new Glyph("FontAwesome", FontAwesome.Glyph.EYE);
            glyph.visibleProperty().bind(cell.emptyProperty().not());

            glyph.textFillProperty().bind(
                Bindings.when(displayedGame.isEqualTo(cell.itemProperty()))
                    .then(javafx.scene.paint.Color.DARKGREEN)
                    .otherwise(javafx.scene.paint.Color.BLACK)
            );
            cell.setGraphic(glyph);
            cell.setAlignment(Pos.CENTER);

            cell.setOnMouseClicked(event -> {
                @SuppressWarnings("unchecked")
                TableCell<GameVO, Game> target = (TableCell<GameVO, Game>) event.getSource();
                Game item = target.getItem();
                fireEvent(new DataEvent<>(DISPLAY_GAME, item));
            });
            return cell;
        });
    }

    private Callback<CellDataFeatures<GameVO, String>, ObservableValue<String>> createPlayerCellValueProvider(Color color) {
        return cellData -> {
            GameVO game = cellData.getValue();
            if (game == null) {
                return null;
            }
            // TODO: Mark winner!
            return Optional.ofNullable(game.getPlayers())
                .map(it -> it.get(color))
                .map(it -> it.getUserId() + " (" + it.getRating() + ")" + (color == game.getWinner() ? " *" : ""))
                .map(ReadOnlyStringWrapper::new)
                .orElse(null);
        };
    }

    // Properties
    public ObservableList<Game> getGames() {
        return games.get();
    }
    public ListProperty<Game> gamesProperty() {
        return games;
    }
    public void setGames(ObservableList<Game> games) {
        this.games.set(games);
    }

    public ObservableSet<String> getSelectedGames() {
        return selectedGames.get();
    }
    public SetProperty<String> selectedGamesProperty() {
        return selectedGames;
    }
    public void setSelectedGames(ObservableSet<String> selectedGames) {
        this.selectedGames.set(selectedGames);
    }

    public Game getDisplayedGame() {
        return displayedGame.get();
    }
    public ObjectProperty<Game> displayedGameProperty() {
        return displayedGame;
    }
    public void setDisplayedGame(Game displayedGame) {
        this.displayedGame.set(displayedGame);
    }

    public boolean isLoading() {
        return loading.get();
    }
    public BooleanProperty loadingProperty() {
        return loading;
    }
    public void setLoading(boolean loading) {
        this.loading.set(loading);
    }

    // Events
    public EventHandler<DataEvent<Game>> getOnDisplayGame() {
        return onDisplayGame.get();
    }
    public ObjectProperty<EventHandler<DataEvent<Game>>> onDisplayGameProperty() {
        return onDisplayGame;
    }
    public void setOnDisplayGame(EventHandler<DataEvent<Game>> onDisplayGame) {
        this.onDisplayGame.set(onDisplayGame);
    }

    public EventHandler<DataListEvent<Game>> getOnSelectGames() {
        return onSelectGames.get();
    }
    public ObjectProperty<EventHandler<DataListEvent<Game>>> onSelectGamesProperty() {
        return onSelectGames;
    }
    public void setOnSelectGames(EventHandler<DataListEvent<Game>> onSelectGames) {
        this.onSelectGames.set(onSelectGames);
    }

    public EventHandler<DataListEvent<Game>> getOnDeselectGames() {
        return onDeselectGames.get();
    }
    public ObjectProperty<EventHandler<DataListEvent<Game>>> onDeselectGamesProperty() {
        return onDeselectGames;
    }
    public void setOnDeselectGames(EventHandler<DataListEvent<Game>> onDeselectGames) {
        this.onDeselectGames.set(onDeselectGames);
    }

}
