package dbrighthd.elytracontrails.networking;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

import static dbrighthd.elytracontrails.networking.ElytraTrailConstants.*;

public class ModPayloadListener implements PluginMessageListener {

    private final Plugin plugin;
    private final PlayerConfigStore store;

    public ModPayloadListener(Plugin plugin, PlayerConfigStore store) {
        this.plugin = plugin;
        this.store = store;
    }

    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player sender, @NotNull byte[] bytes) {
        if (!channel.startsWith(MOD_ID + ":")) return;

        try {
            switch (channel) {
                case TWIRL_STATE -> handleTwirlStateC2S(sender, bytes);
                case PLAYER_CONFIG -> handlePlayerConfigC2S(sender, bytes);
                case GET_CONFIGS_REQUEST -> handleGetAllConfigs(sender);
                case REMOVE_CONFIG -> handleRemoveFromStoreC2S(sender);
                default -> { }
            }
        } catch (Throwable t) {
            plugin.getLogger().warning("Failed handling " + channel + " from " + sender.getName() + ": " + t);
        }
    }

    private void handleTwirlStateC2S(Player sender, byte[] bytes) throws IOException {
        int twirlState = VarInts.readVarInt(new ByteArrayInputStream(bytes));
        int entityId = sender.getEntityId();

        store.setTwirlState(entityId, twirlState);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        VarInts.writeVarInt(out, entityId);
        VarInts.writeVarInt(out, twirlState);

        broadcastToListeningPlayers(TWIRL_STATE, out.toByteArray());
    }

    private void handlePlayerConfigC2S(Player sender, byte[] rawConfigBytes) throws IOException {
        int entityId = sender.getEntityId();

        store.setConfigPayload(entityId, rawConfigBytes);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        VarInts.writeVarInt(out, entityId);
        out.write(rawConfigBytes);

        broadcastToListeningPlayers(PLAYER_CONFIG, out.toByteArray());
    }

    private void handleGetAllConfigs(Player requester) throws IOException {
        for (Map.Entry<Integer, PlayerConfigStore.Entry> playerConfigEntry : store.snapshot().entrySet()) {
            int entityId = playerConfigEntry.getKey();
            byte[] rawConfigBytes = playerConfigEntry.getValue().rawConfigPayload();
            if (rawConfigBytes == null) continue;

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            VarInts.writeVarInt(out, entityId);
            out.write(rawConfigBytes);

            sendToPlayerIfListening(requester, PLAYER_CONFIG, out.toByteArray());
        }
    }

    private void handleRemoveFromStoreC2S(Player sender) throws IOException {
        removeAndBroadcast(sender);
    }

    public void removeAndBroadcast(Player player) throws IOException {
        int entityId = player.getEntityId();
        store.remove(entityId);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        VarInts.writeVarInt(out, entityId);

        broadcastToListeningPlayers(REMOVE_CONFIG, out.toByteArray());
    }

    private void broadcastToListeningPlayers(String channel, byte[] payload) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            sendToPlayerIfListening(player, channel, payload);
        }
    }

    private void sendToPlayerIfListening(Player player, String channel, byte[] payload) {
        if (!player.getListeningPluginChannels().contains(channel)) return;
        player.sendPluginMessage(plugin, channel, payload);
    }

    public static final class VarInts {
        private VarInts() {}

        public static int readVarInt(ByteArrayInputStream in) throws IOException {
            int numRead = 0;
            int result = 0;
            int read;
            do {
                read = in.read();
                if (read == -1) throw new IOException("Unexpected end of stream while reading VarInt");

                int value = (read & 0b0111_1111);
                result |= (value << (7 * numRead));

                numRead++;
                if (numRead > 5) throw new IOException("VarInt is too big");
            } while ((read & 0b1000_0000) != 0);

            return result;
        }

        public static void writeVarInt(ByteArrayOutputStream out, int value) {
            while ((value & 0xFFFFFF80) != 0) {
                out.write((value & 0x7F) | 0x80);
                value >>>= 7;
            }
            out.write(value & 0x7F);
        }
    }
}