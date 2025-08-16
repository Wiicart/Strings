package com.pedestriamc.strings.deathmessages;

import org.bukkit.block.Block;
import org.bukkit.entity.DragonFireball;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Firework;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.WitherSkull;
import org.bukkit.entity.minecart.ExplosiveMinecart;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Locale;
import java.util.Map;

import static org.bukkit.event.entity.EntityDamageEvent.DamageCause;

final class PlaceholderGenerator {

    private static final String UNKNOWN = "unknown";

    private PlaceholderGenerator() {}

    @NotNull
    static Map<String, String> generate(@NotNull DamageCause cause, @NotNull EntityDamageEvent finalDamage) {
        return switch (cause) {
            case CONTACT -> getContactPlaceholders(cause, finalDamage);
            case ENTITY_ATTACK, ENTITY_SWEEP_ATTACK, THORNS -> getAttackPlaceholders(cause, finalDamage);
            case PROJECTILE -> getProjectilePlaceholders(cause, finalDamage);
            case BLOCK_EXPLOSION, ENTITY_EXPLOSION -> getExplosionPlaceholders(cause, finalDamage);
            default -> Map.of();
        };
    }

    @NotNull
    @Unmodifiable
    static Map<String, String> getContactPlaceholders(@NotNull DamageCause cause, @NotNull EntityDamageEvent finalDamage) {
        String blockName = UNKNOWN;
        if (finalDamage instanceof EntityDamageByBlockEvent blockEvent) {
            Block block = blockEvent.getDamager();
            if (block != null) {
                blockName = block.getType()
                        .name()
                        .toLowerCase(Locale.ROOT)
                        .replace("_", " ");
            }
        }

        return Map.of("{block}", blockName);
    }

    @NotNull
    @Unmodifiable
    static Map<String, String> getAttackPlaceholders(@NotNull DamageCause cause, @NotNull EntityDamageEvent finalDamage) {
        String killerName = UNKNOWN;
        String killerDisplayName = UNKNOWN;
        if (finalDamage instanceof EntityDamageByEntityEvent entityEvent) {
            Entity entity = entityEvent.getDamager();
            if (entity instanceof Player p) {
                killerName = p.getName();
                killerDisplayName = p.getDisplayName();
            } else {
                String customName = entity.getCustomName();
                killerName = customName != null ? customName : entity.getName();
                killerDisplayName = killerName;
            }
        }

        return Map.of(
                "{killer}", killerName,
                "{killer-display-name}", killerDisplayName
        );
    }

    @NotNull
    @Unmodifiable
    static Map<String, String> getProjectilePlaceholders(@NotNull DamageCause cause, @NotNull EntityDamageEvent finalDamage) {
        String killerName = UNKNOWN;
        String killerDisplayName = UNKNOWN;
        if (finalDamage instanceof EntityDamageByEntityEvent entityEvent) {
            Entity entity = entityEvent.getDamager();
            if (entity instanceof Projectile proj) {
                ProjectileSource source = proj.getShooter();
                if (source instanceof Player p) {
                    killerName = p.getName();
                    killerDisplayName = p.getDisplayName();
                } else if (source instanceof LivingEntity e) {
                    String customName = e.getCustomName();
                    killerName = customName != null ? customName : e.getName();
                    killerDisplayName = killerName;
                }
            } else {
                String customName = entity.getCustomName();
                killerName = customName != null ? customName : entity.getName();
                killerDisplayName = killerName;
            }
        }

        return Map.of(
                "{killer}", killerName,
                "{killer-display-name}", killerDisplayName
        );
    }

    @NotNull
    @Unmodifiable
    static Map<String, String> getExplosionPlaceholders(@NotNull DamageCause cause, @NotNull EntityDamageEvent finalDamage) {
        String killerName = UNKNOWN;
        if (finalDamage instanceof EntityDamageByEntityEvent entityEvent) {
            Entity damager = entityEvent.getDamager();
            switch(damager) {
                case TNTPrimed tntPrimed -> killerName = "TNT";
                case Firework firework -> killerName = "Firework";
                case ExplosiveMinecart cart -> killerName = "TNT Minecart";
                case WitherSkull skull -> killerName = "Wither Skull";
                case DragonFireball dragonBall -> killerName = "Dragon Fireball";
                case Fireball ball -> killerName = "Fireball";
                case LivingEntity livingEntity -> {
                    String customName = livingEntity.getCustomName();
                    killerName = customName != null ? customName : livingEntity.getName();
                }
                default -> {}
            }
        }

        return Map.of("{killer}", killerName);
    }


}
