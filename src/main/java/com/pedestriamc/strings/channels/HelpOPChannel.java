package com.pedestriamc.strings.channels;

import com.pedestriamc.strings.Strings;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class HelpOPChannel extends Channel{

    public HelpOPChannel(@NotNull Strings strings, String name, String format, String defaultColor, ChannelManager channelManager, boolean callEvent) {
        super(strings, name, format, defaultColor, channelManager, callEvent);
    }

    @Override
    public void sendMessage(Player player, String message){

    }
}
