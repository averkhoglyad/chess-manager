package net.averkhoglyad.chess.manager.core.helper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Predicate;

public abstract class Predicates {

    private Predicates() {
    }

    public static Predicate<? super Boolean> opposite() {
        return (b) -> !b;
    }

    public static Predicate<?> negate(Predicate<?> original) {
        return original.negate();
    }

    public static <T> Predicate<T> memberOf(Collection<T> collection) {
        return new ArrayList<>(collection)::contains;
    }

    public static <T> Predicate<T> notMemberOf(Collection<T> collection) {
        return memberOf(collection).negate();
    }

}
