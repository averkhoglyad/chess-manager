package net.averkhoglyad.chess.manager.core.util;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public class SimplePoolImpl<E> implements Pool<E> {

    private final Set<E> entries;

    private final Supplier<E> factory;

    public SimplePoolImpl(Supplier<E> factory) {
        this(factory, 0);
    }

    public SimplePoolImpl(Supplier<E> factory, int initial) {
        this.factory = factory;
        entries = new HashSet<>(initial);
        for (int i = 0; i < initial; i++) {
            entries.add(factory.get());
        }
    }

    @Override
    public E provide() {
        E entry = entries.stream()
            .findFirst()
            .orElseGet(factory::get);
        entries.remove(entry);
        return entry;
    }

    @Override
    public void release(E entry) {
        entries.add(entry);
    }

}
