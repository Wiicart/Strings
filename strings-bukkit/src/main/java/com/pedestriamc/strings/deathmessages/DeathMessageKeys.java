package com.pedestriamc.strings.deathmessages;

import org.bukkit.event.entity.EntityDamageEvent;

import java.util.Map;

final class DeathMessageKeys {

    private DeathMessageKeys() {}

    static final Map<EntityDamageEvent.DamageCause, String> KEYS = Map.ofEntries(
            Map.entry(EntityDamageEvent.DamageCause.KILL, "messages.command"),
            Map.entry(EntityDamageEvent.DamageCause.WORLD_BORDER, "messages.world-border"),
            Map.entry(EntityDamageEvent.DamageCause.CONTACT, "messages.contact"),
            Map.entry(EntityDamageEvent.DamageCause.ENTITY_ATTACK, "messages.attack"),
            Map.entry(EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK, "messages.attack"),
            Map.entry(EntityDamageEvent.DamageCause.PROJECTILE, "messages.projectile"),
            Map.entry(EntityDamageEvent.DamageCause.SUFFOCATION, "messages.suffocation"),
            Map.entry(EntityDamageEvent.DamageCause.FALL, "messages.fall"),
            Map.entry(EntityDamageEvent.DamageCause.FIRE, "messages.fire"),
            Map.entry(EntityDamageEvent.DamageCause.FIRE_TICK, "messages.fire"),
            Map.entry(EntityDamageEvent.DamageCause.LAVA, "messages.lava"),
            Map.entry(EntityDamageEvent.DamageCause.DROWNING, "messages.drowning"),
            Map.entry(EntityDamageEvent.DamageCause.BLOCK_EXPLOSION, "messages.explosion"),
            Map.entry(EntityDamageEvent.DamageCause.ENTITY_EXPLOSION, "messages.explosion"),
            Map.entry(EntityDamageEvent.DamageCause.VOID, "messages.void"),
            Map.entry(EntityDamageEvent.DamageCause.LIGHTNING, "messages.lightning"),
            Map.entry(EntityDamageEvent.DamageCause.SUICIDE, "messages.suicide"),
            Map.entry(EntityDamageEvent.DamageCause.STARVATION, "messages.starvation"),
            Map.entry(EntityDamageEvent.DamageCause.POISON, "messages.poison"),
            Map.entry(EntityDamageEvent.DamageCause.MAGIC, "messages.magic"),
            Map.entry(EntityDamageEvent.DamageCause.WITHER, "messages.wither"),
            Map.entry(EntityDamageEvent.DamageCause.FALLING_BLOCK, "messages.falling-block"),
            Map.entry(EntityDamageEvent.DamageCause.THORNS, "messages.thorns"),
            Map.entry(EntityDamageEvent.DamageCause.DRAGON_BREATH, "messages.dragon-breath"),
            Map.entry(EntityDamageEvent.DamageCause.FLY_INTO_WALL, "messages.fly-into-wall"),
            Map.entry(EntityDamageEvent.DamageCause.HOT_FLOOR, "messages.hot-floor"),
            Map.entry(EntityDamageEvent.DamageCause.FREEZE, "messages.freeze"),
            Map.entry(EntityDamageEvent.DamageCause.SONIC_BOOM, "messages.sonic-boom")
    );
}
