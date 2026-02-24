package dbrighthd.elytracontrails.networking;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class PlayerConfigStore {

    public static final class Entry {
        private volatile byte[] rawConfigPayload; // C2S bytes for PLAYER_CONFIG (no entityId prefix)
        private volatile int twirlState;

        public byte[] rawConfigPayload() { return rawConfigPayload; }
        public int twirlState() { return twirlState; }
    }

    private final ConcurrentHashMap<Integer, Entry> byEntityId = new ConcurrentHashMap<>();

    public void setConfigPayload(int entityId, byte[] rawConfigPayload) {
        Entry entry = byEntityId.computeIfAbsent(entityId, id -> new Entry());
        entry.rawConfigPayload = rawConfigPayload;
    }

    public void setTwirlState(int entityId, int twirlState) {
        Entry entry = byEntityId.computeIfAbsent(entityId, id -> new Entry());
        entry.twirlState = twirlState;
    }

    public Optional<Entry> get(int entityId) {
        return Optional.ofNullable(byEntityId.get(entityId));
    }

    /** Snapshot safe for iteration without holding map locks */
    public Map<Integer, Entry> snapshot() {
        return Map.copyOf(byEntityId);
    }

    public void remove(int entityId) {
        byEntityId.remove(entityId);
    }
}