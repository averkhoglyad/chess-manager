package net.averkhoglyad.chess.manager.core.helper;

import java.util.Collection;
import java.util.Map;

public abstract class CollectionHelper {

    private CollectionHelper() {
    }

    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean isEmpty(Map<?, ?> collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean isNotEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }

    public static boolean isNotEmpty(Map<?, ?> collection) {
        return !isEmpty(collection);
    }

    public static <T> void addNotNull(Collection<? super T> collection, T... lines) {
        for (T line : lines) {
            if (line == null) continue;
            collection.add(line);
        }
    }

    public static void addNotEmpty(Collection<String> collection, String... items) {
        for (String item : items) {
            if (StringHelper.isEmpty(item)) continue;
            collection.add(item);
        }
    }

    public static <T> void addNotEmpty(Collection<Collection<T>> collection, Collection<T>... items) {
        for (Collection<T> item : items) {
            if (isEmpty(item)) continue;
            collection.add(item);
        }
    }

    public static <K, V> void addNotEmpty(Collection<Map<K, V>> collection, Map<K, V>... items) {
        for (Map<K, V> item : items) {
            if (isEmpty(item)) continue;
            collection.add(item);
        }
    }

}
