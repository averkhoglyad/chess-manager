package net.averkhoglyad.chess.manager.gui.component;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.util.Pair;
import net.averkhoglyad.chess.manager.core.helper.StringHelper;
import net.averkhoglyad.chess.manager.core.util.Pool;
import net.averkhoglyad.chess.manager.core.util.SimplePoolImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static net.averkhoglyad.chess.manager.core.helper.MapBuilder.map;
import static net.averkhoglyad.chess.manager.core.helper.MapBuilder.of;

public class ChessDiagram extends BaseComponent {

    public static final String EMPTY_BOARD = "8/8/8/8/8/8/8/8";
    public static final String START_BOARD = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

    private static final int CELL_SIZE = 25;

    private static final Map<String, String> PIECES_MAP = Collections.unmodifiableMap(map(
        of("B", "\u2657"),
        of("K", "\u2654"),
        of("N", "\u2658"),
        of("P", "\u2659"),
        of("Q", "\u2655"),
        of("R", "\u2656"),
        of("b", "\u265D"),
        of("k", "\u265A"),
        of("n", "\u265E"),
        of("p", "\u265F"),
        of("q", "\u265B"),
        of("r", "\u265C")
    ));

    // Properties
    private StringProperty fen = new SimpleStringProperty(EMPTY_BOARD);
    private StringProperty title = new SimpleStringProperty("");
    private BooleanProperty flipped = new SimpleBooleanProperty(false);

    // Private
    private final Pool<Label> labelsPool = new SimplePoolImpl<>(Label::new, 32);
    private final List<Label> usedLabels = new ArrayList<>(32);

    // Nodes
    @FXML
    private GridPane board;
    @FXML
    private StackPane diagram;

    private final Font font =
        Font.loadFont(getClass().getClassLoader().getResourceAsStream("fonts/arial-unicode-ms.ttf"), CELL_SIZE * 0.8);

    public ChessDiagram() {
        super("net/averkhoglyad/chess/manager/gui/component/ChessDiagram.fxml");
    }

    public void initialize() {
        buildBoardCells(board);
        fen.addListener((observable, oldValue, newValue) -> renderPieces(parseFen(newValue)));
        flipped.addListener((observable, oldValue, newValue) -> renderPieces(parseFen(fen.get())));
        fitContentToComponentSize();
    }

    private void buildBoardCells(GridPane board) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Color color = (row + col) % 2 == 0 ? Color.WHITESMOKE : Color.BURLYWOOD;
                Rectangle cell = new Rectangle();
                cell.setFill(color);
                cell.setWidth(CELL_SIZE);
                cell.setHeight(CELL_SIZE);
                board.add(cell, col, row);
            }
        }
    }

    private List<Pair<String, Integer>> parseFen(String fen) {
        fen = fen.trim();
        int i = fen.indexOf(' ');
        if (i > 0) {
            fen = fen.substring(0, i);
        }
        List<Pair<String, Integer>> result = new ArrayList<>();
        String[] rows = fen.split("/");
        int position = 0;
        for (String row : rows) {
            for (char ch : row.toCharArray()) {
                String s = new String(new char[]{ch});
                if (Character.isAlphabetic(ch)) {
                    result.add(new Pair(s, position++));
                } else {
                    position += Integer.valueOf(s);
                }
            }
        }
        return result;
    }

    private void renderPieces(List<Pair<String, Integer>> pieces) {
        usedLabels.forEach(diagram.getChildren()::remove);
        usedLabels.forEach(labelsPool::release);
        pieces.forEach(pair -> parsePiece(pair.getKey(), pair.getValue()));
    }

    private void parsePiece(String figure, int index) {
        Label piece = labelsPool.provide();
        piece.setFont(font);
        piece.setText(PIECES_MAP.get(figure));
        int effectedIndex = flipped.get() ? 63 - index : index;
        Node cell = board.getChildren().get(effectedIndex);
        piece.translateXProperty().bind(cell.layoutXProperty().subtract(piece.layoutXProperty()).add(Bindings.subtract(CELL_SIZE, piece.widthProperty()).divide(2)));
        piece.translateYProperty().bind(cell.layoutYProperty().subtract(piece.layoutYProperty()).add(Bindings.subtract(CELL_SIZE, piece.heightProperty()).divide(2)));
        usedLabels.add(piece);
        diagram.getChildren().add(piece);
    }

    public void flipBoard() {
        flipped.set(!flipped.get());
    }

    // Properties
    public String getFen() {
        return fen.get();
    }
    public StringProperty fenProperty() {
        return fen;
    }
    public void setFen(String fen) {
        this.fen.set(fen);
    }

    public String getTitle() {
        return title.get();
    }
    public StringProperty titleProperty() {
        return title;
    }
    public void setTitle(String title) {
        this.title.set(StringHelper.isNotEmpty(title) ? title : EMPTY_BOARD);
    }

    public boolean isFlipped() {
        return flipped.get();
    }
    public BooleanProperty flippedProperty() {
        return flipped;
    }
    public void setFlipped(boolean flipped) {
        this.flipped.set(flipped);
    }

}
