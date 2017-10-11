package net.averkhoglyad.chess.manager.core.helper;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public abstract class CollectionHelper {

    private CollectionHelper() {
    }

    public static int size(Collection<?> collection) {
        return collection == null ? 0 : collection.size();
    }

    public static int size(Map<?, ?> map) {
        return map == null ? 0 : map.size();
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

    public static <T> T first(Collection<T> collection) {
        if (collection instanceof List<?>) return first((List<T>) collection);
        if (collection == null) throw new NullPointerException();
        if (collection.isEmpty()) throw new IndexOutOfBoundsException();
        return collection.iterator().next();
    }

    public static <T> T first(List<T> list) {
        if (list == null) throw new NullPointerException();
        if (list.isEmpty()) throw new IndexOutOfBoundsException();
        return list.get(0);
    }

    public static <T> T last(Collection<T> collection) {
        if (collection instanceof List<?>) return last((List<T>) collection);
        if (collection == null) throw new NullPointerException();
        if (collection.isEmpty()) throw new IndexOutOfBoundsException();
        T last = null;
        Iterator<T> iterator = collection.iterator();
        while (iterator.hasNext()) {
            last = iterator.next();
        }
        return last;
    }

    public static <T> T last(List<T> list) {
        if (list == null) throw new NullPointerException();
        if (list.isEmpty()) throw new IndexOutOfBoundsException();
        return list.get(list.size() - 1);
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
