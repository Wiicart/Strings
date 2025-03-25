package com.pedestriamc.strings.commands.channel;

import com.pedestriamc.strings.api.channel.Channel;
import org.bukkit.entity.Player;

import java.util.Map;

public record BaseData(Channel channel, Player target, Map<String, String> placeholders, boolean shouldReturn) {}
