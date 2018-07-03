package me.rayzr522.permissioneffects.struct;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class EffectInfo {
    private String key;
    private PotionEffectType type;
    private int amplifier;
    private boolean ambient;
    private boolean particlesEnabled;

    private EffectInfo(String key, PotionEffectType type, int amplifier, boolean ambient, boolean particlesEnabled) {
        this.key = key;
        this.type = type;
        this.amplifier = amplifier;
        this.ambient = ambient;
        this.particlesEnabled = particlesEnabled;
    }

    public static EffectInfo load(String key, ConfigurationSection config) {
        if (!config.contains("type")) {
            throw new IllegalArgumentException("Missing key 'type'!");
        }

        PotionEffectType type = PotionEffectType.getByName(config.getString("type"));
        if (type == null) {
            throw new IllegalArgumentException(String.format("Invalid potion effect type '%s'!", config.getString("type")));
        }

        int amplifier = config.getInt("amplifier", 0);
        boolean ambient = config.getBoolean("ambient", false);
        boolean particlesEnabled = config.getBoolean("particles", false);

        return new EffectInfo(key, type, amplifier, ambient, particlesEnabled);
    }

    public String getKey() {
        return key;
    }

    public void apply(Player player, int duration) {
        player.addPotionEffect(new PotionEffect(type, duration, amplifier, ambient, particlesEnabled));
    }
}
