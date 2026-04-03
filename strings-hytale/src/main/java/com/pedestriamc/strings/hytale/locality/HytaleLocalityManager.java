package com.pedestriamc.strings.hytale.locality;

import com.hypixel.hytale.server.core.universe.world.World;
import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.channel.local.Locality;
import com.pedestriamc.strings.api.channel.local.LocalityManager;

import java.util.HashMap;
import java.util.Map;

public class HytaleLocalityManager implements LocalityManager<World> {

    private final Strings strings;

    private final Map<World, Locality<World>> map = new HashMap<>();

    public HytaleLocalityManager(Strings strings) {
        this.strings = strings;
    }

    @Override
    public Locality<World> get(Object object) {
        if (!(object instanceof World world)) {
            throw new IllegalArgumentException("The passed object must be an instance of org.bukkit.World");
        }

        return map.computeIfAbsent(world, w -> new HytaleLocality(strings, w));
    }
}
