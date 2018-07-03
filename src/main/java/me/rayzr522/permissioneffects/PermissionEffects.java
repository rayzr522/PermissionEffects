package me.rayzr522.permissioneffects;

import me.rayzr522.permissioneffects.command.CommandHandler;
import me.rayzr522.permissioneffects.data.EffectManager;
import me.rayzr522.permissioneffects.utils.MessageHandler;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;

/**
 * @author Rayzr
 */
public class PermissionEffects extends JavaPlugin {
    private MessageHandler messages = new MessageHandler();
    private EffectManager effectManager = new EffectManager(this);
    private int timerDelay;
    private boolean ignoreOp;

    private BukkitRunnable effectTask = null;

    @Override
    public void onEnable() {
        getCommand("permissioneffects").setExecutor(new CommandHandler(this));

        reload();
    }

    /**
     * (Re)loads all configs from the disk
     */
    public void reload() {
        saveDefaultConfig();
        reloadConfig();

        messages.load(getConfig("messages.yml"));
        timerDelay = getConfig().getInt("global.timer-delay", 40);
        ignoreOp = getConfig().getBoolean("global.ignore-op", true);
        effectManager.load(getConfig().getConfigurationSection("effects"));

        startEffectTask();
    }

    private void startEffectTask() {
        if (effectTask != null) {
            effectTask.cancel();
        }

        effectTask = new BukkitRunnable() {
            @Override
            public void run() {
                effectManager.applyAll();
            }
        };
        effectTask.runTaskTimer(this, timerDelay, timerDelay);
    }

    /**
     * If the file is not found and there is a default file in the JAR, it saves the default file to the plugin data folder first
     *
     * @param path The path to the config file (relative to the plugin data folder)
     * @return The {@link YamlConfiguration}
     */
    private YamlConfiguration getConfig(String path) {
        if (!getFile(path).exists() && getResource(path) != null) {
            saveResource(path, true);
        }
        return YamlConfiguration.loadConfiguration(getFile(path));
    }


    /**
     * @param path The path of the file (relative to the plugin data folder)
     * @return The {@link File}
     */
    private File getFile(String path) {
        return new File(getDataFolder(), path.replace('/', File.separatorChar));
    }

    /**
     * Returns a message from the language file
     *
     * @param key     The key of the message to translate
     * @param objects The formatting objects to use
     * @return The formatted message
     */
    public String tr(String key, Object... objects) {
        return messages.tr(key, objects);
    }

    /**
     * Returns a message from the language file without adding the prefix
     *
     * @param key     The key of the message to translate
     * @param objects The formatting objects to use
     * @return The formatted message
     */
    public String trRaw(String key, Object... objects) {
        return messages.trRaw(key, objects);
    }

    /**
     * Checks a target {@link CommandSender} for a given permission (excluding the permission base). Example:
     * <p>
     * <pre>
     *     checkPermission(sender, "command.use", true);
     * </pre>
     * <p>
     * This would check if the player had the permission <code>"{plugin name}.command.use"</code>, and if they didn't, it would send them the no-permission message from the messages config file.
     *
     * @param target      The target {@link CommandSender} to check
     * @param permission  The permission to check, excluding the permission base (which is the plugin name)
     * @param sendMessage Whether or not to send a no-permission message to the target
     * @return Whether or not the target has the given permission
     */
    public boolean checkPermission(CommandSender target, String permission, boolean sendMessage) {
        String fullPermission = String.format("%s.%s", getName(), permission);

        if (!target.hasPermission(fullPermission)) {
            if (sendMessage) {
                target.sendMessage(tr("no-permission", fullPermission));
            }

            return false;
        }

        return true;
    }

    public int getTimerDelay() {
        return timerDelay;
    }

    public boolean shouldIgnoreOp() {
        return ignoreOp;
    }
}
