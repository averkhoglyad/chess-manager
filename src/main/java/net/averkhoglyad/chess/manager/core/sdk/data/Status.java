package net.averkhoglyad.chess.manager.core.sdk.data;

public enum Status {

    created(false),
    started(false),
    aborted(false),
    mate(false),
    resign(false),
    stalemate(true),
    timeout(false),
    draw(true),
    outoftime(false),
    cheat(false),
    nostart(false),
    unknownfinish(false),
    variantend(false);

    private final boolean drawn;

    Status(boolean drawn) {
        this.drawn = drawn;
    }

    public boolean isDrawn() {
        return drawn;
    }

}
