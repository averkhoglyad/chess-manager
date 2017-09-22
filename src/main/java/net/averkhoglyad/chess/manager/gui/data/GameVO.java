package net.averkhoglyad.chess.manager.gui.data;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import lombok.Value;
import net.averkhoglyad.chess.manager.core.sdk.data.Game;

@Value
public class GameVO {

    private final Game game;
    private final BooleanProperty selected = new SimpleBooleanProperty();

    public boolean isSelected() {
        return selected.get();
    }
    public void setSelected(boolean selected) {
        this.selected.set(selected);
    }
    public BooleanProperty selectedProperty() {
        return selected;
    }
    public void toggleSelected() {
        selected.set(!selected.get());
    }

}
