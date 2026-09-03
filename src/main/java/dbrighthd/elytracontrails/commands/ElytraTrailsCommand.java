package dbrighthd.elytracontrails.commands;

import dbrighthd.elytracontrails.ElytraContrails;
import dbrighthd.elytracontrails.networking.PlayerConfigStore;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public final class ElytraTrailsCommand implements CommandExecutor {

    private static final String USAGE =
            ChatColor.YELLOW + "Usage: /elytratrailsconfig <enabletrails|enabletwirls> <true|false>";

    private final ElytraContrails plugin;

    public ElytraTrailsCommand(ElytraContrails plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args
    ) {
        if (args.length != 2) {
            sender.sendMessage(USAGE);
            return true;
        }

        Boolean enabled = parseBoolean(args[1]);

        if (enabled == null) {
            sender.sendMessage(ChatColor.RED + "Expected true or false.");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "enabletrails" -> {
                try {
                    plugin.setTrailsEnabled(enabled);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                sender.sendMessage(
                        ChatColor.GREEN + "Elytra trails " +
                                (enabled ? "enabled" : "disabled") + "."
                );
            }

            case "enabletwirls" -> {
                plugin.setTwirlEnabled(enabled);
                sender.sendMessage(
                        ChatColor.GREEN + "Elytra twirls " +
                                (enabled ? "enabled" : "disabled") + "."
                );
            }

            default -> sender.sendMessage(USAGE);
        }

        return true;
    }

    private static Boolean parseBoolean(String value) {
        return switch (value.toLowerCase()) {
            case "true" -> true;
            case "false" -> false;
            default -> null;
        };
    }
}