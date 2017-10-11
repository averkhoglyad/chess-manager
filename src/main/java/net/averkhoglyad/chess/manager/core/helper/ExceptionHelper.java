package net.averkhoglyad.chess.manager.core.helper;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.function.BooleanSupplier;

@Slf4j
public abstract class ExceptionHelper {

    private ExceptionHelper() {
    }

    private static final String ERROR_MESSAGE = "Exception was suppressed:";

    // Throws IllegalStateException on false assertion result
    public static void assertion(boolean result, String message) throws IllegalStateException {
        if (!result) {
            throw new IllegalStateException(message);
        }
    }

    public static void assertion(BooleanSupplier test, String message) throws IllegalStateException {
        assertion(test.getAsBoolean(), message);
    }

    // Wrap checked exception into RuntimeException
    public static void doStrict(ThrowsRunnable callback) {
        try {
            callback.run();
        } catch (Exception e) {
            throw runtimeException(e);
        }
    }

    public static <T> T doStrict(Callable<T> callback) {
        try {
            return callback.call();
        } catch (Exception e) {
            throw runtimeException(e);
        }
    }

    public static RuntimeException runtimeException(Exception e) {
        return (e instanceof RuntimeException) ? (RuntimeException) e : new RuntimeException(e);
    }

    // Ignore any thrown Exception
    public static void doQuiet(ThrowsRunnable callback) {
        try {
            callback.run();
        } catch (Exception e) {
            log.error(ERROR_MESSAGE, e);
        }
    }

    public static <T> T doQuiet(Callable<T> callback) {
        try {
            return callback.call();
        } catch (Exception e) {
            log.error(ERROR_MESSAGE, e);
            return null;
        }
    }

    @FunctionalInterface
    public interface ThrowsRunnable {
        void run() throws Exception;
    }

}
