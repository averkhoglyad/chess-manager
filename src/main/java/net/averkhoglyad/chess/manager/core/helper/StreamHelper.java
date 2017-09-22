package net.averkhoglyad.chess.manager.core.helper;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public abstract class StreamHelper {

    private StreamHelper() {
    }

    public static <T> Stream<T> parallel(Iterator<T> iterator) {
        return parallel(new SingleTimeIterable<>(iterator));
    }

    public static <T> Stream<T> parallel(Iterable<T> iterable) {
        return parallel(iterable.spliterator());
    }

    public static <T> Stream<T> parallel(Spliterator<T> spliterator) {
        return StreamSupport.stream(spliterator, true);
    }

    public static <T> Stream<T> sequential(Iterator<T> iterator) {
        return sequential(new SingleTimeIterable<>(iterator));
    }

    public static <T> Stream<T> sequential(Iterable<T> iterable) {
        return sequential(iterable.spliterator());
    }

    public static <T> Stream<T> sequential(Spliterator<T> spliterator) {
        return StreamSupport.stream(spliterator, false);
    }

    private static class SingleTimeIterable<T> implements Iterable<T> {

        private final Iterator<T> iterator;

        public SingleTimeIterable(Iterator<T> iterator) {
            this.iterator = iterator;
        }

        @Override
        public Iterator<T> iterator() {
            return iterator;
        }

    }

}
