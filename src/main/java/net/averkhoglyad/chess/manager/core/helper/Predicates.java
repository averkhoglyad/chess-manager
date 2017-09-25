package net.averkhoglyad.chess.manager.core.helper;

import java.util.Collection;
import java.util.function.Predicate;

public abstract class Predicates {

    private Predicates() {
    }

    public static Predicate<Boolean> opposite() {
        return (b) -> !b;
    }

    public static <T> Predicate<T> not(Predicate<T> original) {
        return original.negate();
    }

    public static <T> Predicate<T> memberOf(Collection<T> collection) {
        return collection::contains;
    }

    public static <T> Predicate<T> notMemberOf(Collection<T> collection) {
        return (not(memberOf(collection)));
    }

}
