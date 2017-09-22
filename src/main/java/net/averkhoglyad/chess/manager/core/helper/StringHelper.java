package net.averkhoglyad.chess.manager.core.helper;

public abstract class StringHelper {

    private StringHelper() {
    }

    public static boolean isEmpty(CharSequence cs) {
        return length(cs) == 0;
    }

    public static boolean isNotEmpty(CharSequence cs) {
        return !isEmpty(cs);
    }

    public static boolean isBlank(CharSequence cs) {
        if (cs == null || cs.length() == 0) return true;
        for (int i = 0; i < cs.length(); i++) {
            if (Character.isWhitespace(cs.charAt(i))) continue;
            return false;
        }
        return true;
    }

    public static boolean isNotBlank(final CharSequence cs) {
        return !isBlank(cs);
    }

    public static int length(CharSequence cs) {
        return cs == null ? 0 : cs.length();
    }

}
