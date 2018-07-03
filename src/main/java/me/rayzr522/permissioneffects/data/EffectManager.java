package me.rayzr522.permissioneffects.data;

import me.rayzr522.permissioneffects.PermissionEffects;
import me.rayzr522.permissioneffects.struct.EffectInfo;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class EffectManager {
    private PermissionEffects plugin;
    private List<EffectInfo> effects = new ArrayList<>();

    public EffectManager(PermissionEffects plugin) {
        this.plugin = plugin;
    }

    public void load(ConfigurationSection config) {
        effects = config.getKeys(false).stream()
                .filter(config::isConfigurationSection)
                .map(key -> {
                    try {
                        return EffectInfo.load(key, config.getConfigurationSection(key));
                    } catch (IllegalArgumentException e) {
                        plugin.getLogger().log(Level.SEVERE, String.format("Failed to load effect '%s'", key), e);
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private boolean canUse(Player player, EffectInfo effect) {
        if (plugin.shouldIgnoreOp() && player.isOp()) {
            return false;
        }

        return plugin.checkPermission(player, String.format("effect.%s", effect.getKey()), false);
    }

    public void applyAll() {
        Bukkit.getOnlinePlayers().forEach(player -> {
            effects.stream()
                    .filter(effect -> canUse(player, effect))
                    .forEach(effect -> effect.apply(player, plugin.getTimerDelay()));
        });
    }
}
