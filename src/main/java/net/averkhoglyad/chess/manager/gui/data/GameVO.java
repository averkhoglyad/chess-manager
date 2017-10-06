package net.averkhoglyad.chess.manager.gui.data;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import lombok.Getter;
import lombok.experimental.Delegate;
import net.averkhoglyad.chess.manager.core.sdk.data.Game;

public class GameVO {

    @Delegate
    @Getter
    private final Game game;
    private final BooleanProperty selected;

    public GameVO(Game game, boolean selected) {
        this.game = game;
        this.selected = new SimpleBooleanProperty(selected);
    }

    public boolean isSelected() {
        return selected.get();
    }
    public BooleanProperty selectedProperty() {
        return selected;
    }
    public void setSelected(boolean selected) {
        this.selected.set(selected);
    }
}
