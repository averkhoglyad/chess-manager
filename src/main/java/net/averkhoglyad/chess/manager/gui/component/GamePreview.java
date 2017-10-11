package net.averkhoglyad.chess.manager.gui.component;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import net.averkhoglyad.chess.manager.core.helper.CollectionHelper;
import net.averkhoglyad.chess.manager.core.sdk.data.Color;
import net.averkhoglyad.chess.manager.core.sdk.data.Game;

public class GamePreview extends BaseComponent {

    // Properties
    private BooleanProperty loading = new SimpleBooleanProperty(this, "loading");
    private BooleanProperty flipped = new SimpleBooleanProperty(this, "flipped");
    private ObjectProperty<Game> game = new SimpleObjectProperty<>(this, "game");

    // Nodes
    @FXML
    private ChessDiagram diagram;
    @FXML
    private Label moves;
    @FXML
    private ScrollPane contentBox;
    @FXML
    private Label loadingLabel;

    public GamePreview() {
        super("net/averkhoglyad/chess/manager/gui/component/GamePreview.fxml");
    }

    public void initialize() {
        fitContentToComponentSize();
        contentBox.visibleProperty().bind(game.isNotNull());
        loadingLabel.visibleProperty().bind(game.isNull().and(loading));
        diagram.flippedProperty().bind(flipped);
        game.addListener(c -> {
            Game game = this.game.get();
            if (game == null) return;
            String fen = CollectionHelper.last(game.getFenDiagrams());
            diagram.setFen(fen);
            diagram.setTitle(game.getPlayers().get(Color.white).getUserId() + " - " + game.getPlayers().get(Color.black).getUserId());
            moves.setText(prepareMovesNotation(game));
        });
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

    // Properties
    public boolean isLoading() {
        return loading.get();
    }
    public BooleanProperty loadingProperty() {
        return loading;
    }
    public void setLoading(boolean loading) {
        this.loading.set(loading);
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

    public Game getGame() {
        return game.get();
    }
    public ObjectProperty<Game> gameProperty() {
        return game;
    }
    public void setGame(Game game) {
        this.game.set(game);
    }

}
