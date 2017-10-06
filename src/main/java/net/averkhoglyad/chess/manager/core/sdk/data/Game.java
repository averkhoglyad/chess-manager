package net.averkhoglyad.chess.manager.core.sdk.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Data
public class Game {

    private String id;
    private Variant variant;
    private boolean rated;
    private Status status;
    private Speed speed;
    @JsonProperty("perf")
    private String performance;
    private Instant createdAt;
    private Instant lastMoveAt;
    private int turns;
    @JsonProperty("color")
    private Color currentTurn;
    private Color winner;
    private String url;
    private String moves;
    private Opening opening;
    private Map<Color, Player> players;
    @JsonProperty("fens")
    private List<String> fenDiagrams;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Game other = (Game) o;
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + id.hashCode();
        return result;
    }

}
