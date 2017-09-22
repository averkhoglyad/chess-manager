package net.averkhoglyad.chess.manager.core.helper;

import java.util.HashMap;
import java.util.Map;

public abstract class MapBuilder {

    private MapBuilder() {
    }

    public static <K, V> Map<K, V> map(Pair<K, V>... pairs) {
        Map<K, V> map = new HashMap<>(pairs.length);
        for (Pair<K, V> pair : pairs) {
            map.put(pair.key, pair.value);
        }
        return map;
    }

    public static <K, V> Pair<K, V> of(K key, V value) {
        return new Pair<>(key, value);
    }

    public static class Pair<K, V> {
        private K key;
        private V value;

        private Pair(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

}
