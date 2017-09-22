package net.averkhoglyad.chess.manager.core.util;

public interface Pool<E> {

    E provide();
    void release(E entry);

}
