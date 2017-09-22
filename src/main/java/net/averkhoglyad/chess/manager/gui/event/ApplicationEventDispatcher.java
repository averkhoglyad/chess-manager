package net.averkhoglyad.chess.manager.gui.event;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;

public class ApplicationEventDispatcher {

    private final ConcurrentMap<Enum<?>, Map<UUID, Consumer<Object>>> registeredEvents = new ConcurrentHashMap<>();

    public void trigger(Enum<?> event) {
        trigger(event, null);
    }

    public void trigger(Enum<?> event, Object data) {
        Map<UUID, Consumer<Object>> consumers = registeredEvents.getOrDefault(event, new LinkedHashMap<>());
        consumers.values().forEach(c -> c.accept(data));
    }

    public UUID on(Enum<?> event, Runnable runnable) {
        return on(event, o -> runnable.run());
    }

    public UUID on(Enum<?> event, Consumer<Object> consumer) {
        registeredEvents.putIfAbsent(event, new LinkedHashMap<>());
        UUID uid = UUID.randomUUID();
        registeredEvents.get(event).put(uid, consumer);
        return uid;
    }

    public void off(Enum<?> event, UUID uid) {
        Map<UUID, Consumer<Object>> consumers = registeredEvents.getOrDefault(event, new LinkedHashMap<>());
        consumers.remove(uid);
    }

    // Singleton implementation
    public static ApplicationEventDispatcher getInstance() {
        return AppEventDispatcherHolder.instance;
    }

    private ApplicationEventDispatcher() {
    }

    private static class AppEventDispatcherHolder {
        private static ApplicationEventDispatcher instance = new ApplicationEventDispatcher();
    }

}
