package com.pedestriamc.strings.api.channels.data;

import org.bukkit.World;

public class WorldChannelData extends ChannelData {

    private World world;

    public WorldChannelData(){
        super();
    }

    public World getWorld() {
        return world;
    }

    public void setWorld(World world) {
        this.world = world;
    }

}
