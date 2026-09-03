package dbrighthd.elytracontrails;

import dbrighthd.elytracontrails.commands.ElytraTrailsCommand;
import dbrighthd.elytracontrails.networking.ModPayloadListener;
import dbrighthd.elytracontrails.networking.PlayerConfigStore;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRuleCategory;
import org.bukkit.GameRules;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static dbrighthd.elytracontrails.networking.ElytraTrailConstants.*;

public class ElytraContrails extends JavaPlugin implements Listener {

    private PlayerConfigStore store;
    private ModPayloadListener listener;
    public static boolean trailsEnabled;
    public static boolean twirlsEnabled;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        trailsEnabled = getConfig().getBoolean("enable-trails");
        twirlsEnabled = getConfig().getBoolean("enable-twirls");
        store = new PlayerConfigStore();
        listener = new ModPayloadListener(this, store);
        getCommand("elytratrailsconfig")
                .setExecutor(new ElytraTrailsCommand(this));
        var messenger = getServer().getMessenger();

        // Incoming channels (C2S)
        messenger.registerIncomingPluginChannel(this, TWIRL_STATE, listener);
        messenger.registerIncomingPluginChannel(this, PLAYER_CONFIG, listener);
        messenger.registerIncomingPluginChannel(this, GET_CONFIGS_REQUEST, listener);
        messenger.registerIncomingPluginChannel(this, REMOVE_CONFIG, listener);
        messenger.registerIncomingPluginChannel(this, PLAYER_CONFIG_DEPRECATED, listener);
        messenger.registerIncomingPluginChannel(this, TWIRL_DATA, listener);

        // Outgoing channels (S2C)
        messenger.registerOutgoingPluginChannel(this, TWIRL_STATE);
        messenger.registerOutgoingPluginChannel(this, PLAYER_CONFIG);
        messenger.registerOutgoingPluginChannel(this, REMOVE_CONFIG);
        messenger.registerOutgoingPluginChannel(this, TWIRL_DATA);
        messenger.registerOutgoingPluginChannel(this, TRAIL_STATUS);

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


    public void setTwirlEnabled(boolean enabled)
    {
        twirlsEnabled = enabled;

        getConfig().set("enable-twirls", enabled);
        saveConfig();
    }

    public void setTrailsEnabled(boolean enabled) throws IOException {
        trailsEnabled = enabled;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.write(ElytraContrails.trailsEnabled ? 1 : 0);
        listener.broadcastToListeningPlayers(TRAIL_STATUS, out.toByteArray());
        getConfig().set("enable-trails", enabled);
        saveConfig();
        listener.sendAllConfigsToAllPlayers();
    }
}