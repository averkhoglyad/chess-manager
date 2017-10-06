package net.averkhoglyad.chess.manager.gui.component;

import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import net.averkhoglyad.chess.manager.core.sdk.data.Color;
import net.averkhoglyad.chess.manager.core.sdk.data.Game;
import net.averkhoglyad.chess.manager.gui.data.GameVO;
import net.averkhoglyad.chess.manager.gui.event.DataEvent;
import net.averkhoglyad.chess.manager.gui.event.DataListEvent;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class GamesTable extends BaseComponent {

    private static final EventType<DataEvent<Game>> DISPLAY_GAME = new EventType<>("displayGame");
    private static final EventType<DataListEvent<Game>> SELECT_GAMES = new EventType<>("selectGames");
    private static final EventType<DataListEvent<Game>> DESELECT_GAMES = new EventType<>("deselectGames");

    // Properties
    private BooleanProperty loading = new SimpleBooleanProperty(this, "loading");
    private ObjectProperty<List<Game>> games = new SimpleObjectProperty<>(this, "games", Collections.emptyList());
    private ObjectProperty<Set<String>> selectedGames = new SimpleObjectProperty<>(this, "selectedGames", Collections.emptySet());
    private ObjectProperty<Game> displayedGame = new SimpleObjectProperty(this, "displayedGame");

    private ObservableList<GameVO> gameVOs = FXCollections.observableArrayList();

    // Event Handlers
    private ObjectProperty<EventHandler<DataEvent<Game>>> onDisplayGame = createHandler(DISPLAY_GAME);
    private ObjectProperty<EventHandler<DataListEvent<Game>>> onSelectGames = createHandler(SELECT_GAMES);
    private ObjectProperty<EventHandler<DataListEvent<Game>>> onDeselectGames = createHandler(DESELECT_GAMES);

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

        games.addListener(evt -> {
            prevSelectedItemIndex = 0;
            gameVOs.clear();
            games.get().stream()
                .map(game -> new GameVO(game, selectedGames.get().contains(game.getId())))
                .forEach(gameVOs::add);
            selectionColumnCheckBox.setSelected(gameVOs.stream().allMatch(GameVO::isSelected));
        });

        selectedGames.addListener(evt -> {
            gameVOs.forEach(it -> it.setSelected(selectedGames.get().contains(it.getId())));
            selectionColumnCheckBox.setSelected(gameVOs.stream().allMatch(GameVO::isSelected));
        });

        gamesTable.setItems(gameVOs);
        gamesTable.setSortPolicy(param -> false);

        selectionColumn.setCellValueFactory(cellData -> cellData.getValue().selectedProperty());
        selectionColumn.setCellFactory(column -> {
            CheckBoxTableCell<GameVO, Boolean> cell = new CheckBoxTableCell<>();
            cell.setOnMouseClicked(event -> {
                CheckBoxTableCell<GameVO, Boolean> target = (CheckBoxTableCell<GameVO, Boolean>) event.getTarget();
                int currentIndex = target.getIndex();

                GameVO clickedVO = target.getTableView().getItems().get(currentIndex);
                boolean isSelection = !selectedGames.get().contains(clickedVO.getId());
                List<Game> targetGames;
                if (event.isShiftDown()) {
                    int from = Integer.min(prevSelectedItemIndex, currentIndex);
                    int to = Integer.max(prevSelectedItemIndex, currentIndex);
                    targetGames = new ArrayList<>();
                    for (int i = from; i <= to; i++) {
                        GameVO vo = target.getTableView().getItems().get(i);
                        targetGames.add(vo.getGame());
                    }
                } else {
                    GameVO vo = clickedVO;
                    Game game = vo.getGame();
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
                TableCell<GameVO, Game> target = (TableCell<GameVO, Game>) event.getSource();
                Game item = target.getItem();
                fireEvent(new DataEvent<>(DISPLAY_GAME, item));
            });
            return cell;
        });
    }

    private Callback<CellDataFeatures<GameVO, String>, ObservableValue<String>> createPlayerCellValueProvider(Color color) {
        return cellData -> Optional.ofNullable(cellData.getValue().getPlayers().get(color))
            .map(it -> it.getUserId() + " (" + it.getRating() + ")")
            .map(ReadOnlyStringWrapper::new)
            .orElse(null);
    }

    // Properties
    public List<Game> getGames() {
        return games.get();
    }
    public ObjectProperty<List<Game>> gamesProperty() {
        return games;
    }
    public void setGames(List<Game> games) {
        this.games.set(games);
    }

    public Set<String> getSelectedGames() {
        return selectedGames.get();
    }
    public ObjectProperty<Set<String>> selectedGamesProperty() {
        return selectedGames;
    }
    public void setSelectedGames(Set<String> selectedGames) {
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
