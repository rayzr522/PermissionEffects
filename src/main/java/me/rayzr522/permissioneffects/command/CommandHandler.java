package me.rayzr522.permissioneffects.command;

import me.rayzr522.permissioneffects.PermissionEffects;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public class CommandHandler implements CommandExecutor {
    private PermissionEffects plugin;

    public CommandHandler(PermissionEffects plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            showUsage(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "version":
                sender.sendMessage(plugin.tr("command.version", plugin.getDescription().getVersion()));
                break;
            case "reload":
                plugin.reload();
                sender.sendMessage(plugin.tr("command.reloaded"));
                break;
            case "types":
                String types = Arrays.stream(PotionEffectType.values())
                        .filter(Objects::nonNull)
                        .map(PotionEffectType::getName)
                        .collect(Collectors.joining(", "));

                sender.sendMessage(plugin.tr("command.types", types));
                break;
            default:
                showUsage(sender);
        }

        return true;
    }

    private void showUsage(CommandSender sender) {
        sender.sendMessage(plugin.trRaw("command.help"));
    }
}
