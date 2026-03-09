package dbrighthd.elytracontrails;

import dbrighthd.elytracontrails.networking.ModPayloadListener;
import dbrighthd.elytracontrails.networking.PlayerConfigStore;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

import static dbrighthd.elytracontrails.networking.ElytraTrailConstants.*;

public class ElytraContrails extends JavaPlugin implements Listener {

    private PlayerConfigStore store;
    private ModPayloadListener listener;

    @Override
    public void onEnable() {
        store = new PlayerConfigStore();
        listener = new ModPayloadListener(this, store);

        var messenger = getServer().getMessenger();

        // Incoming channels (C2S)
        messenger.registerIncomingPluginChannel(this, TWIRL_STATE, listener);
        messenger.registerIncomingPluginChannel(this, PLAYER_CONFIG, listener);
        messenger.registerIncomingPluginChannel(this, GET_CONFIGS_REQUEST, listener);
        messenger.registerIncomingPluginChannel(this, REMOVE_CONFIG, listener);
        messenger.registerIncomingPluginChannel(this, PLAYER_CONFIG_DEPRECATED, listener);

        // Outgoing channels (S2C)
        messenger.registerOutgoingPluginChannel(this, TWIRL_STATE);
        messenger.registerOutgoingPluginChannel(this, PLAYER_CONFIG);
        messenger.registerOutgoingPluginChannel(this, REMOVE_CONFIG);

        getServer().getPluginManager().registerEvents(this, this);

        getLogger().info("ElytraContrails enabled");
    }

    @Override
    public void onDisable() {
        getLogger().info("ElytraContrailsRelay disabled");
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        listener.clearDeprecatedWarning(e.getPlayer());
        try {
            listener.removeAndBroadcast(e.getPlayer());
        } catch (IOException ex) {
            getLogger().warning("Failed to broadcast remove_from_store on quit: " + ex);
        }
    }
}