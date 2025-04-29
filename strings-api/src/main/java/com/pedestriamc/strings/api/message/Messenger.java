package com.pedestriamc.strings.api.message;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.Map;

public final class Messenger {

    private final EnumMap<Message, Object> enumMap = new EnumMap<>(Message.class);
    private final String prefix;

    public Messenger(FileConfiguration config) {
        for(Message msg : Message.values()) {
            String key = msg.getKey();
            try {
                if(config.isList(key)) {
                    enumMap.put(msg, config.getStringList(key).toArray(new String[0]));
                } else {
                    enumMap.put(msg, config.getString(key));
                }
            } catch(NullPointerException e) {
                Bukkit.getLogger().warning("[Strings] Unable to find message for " + msg);
            }
        }
        prefix = config.getString("prefix", "&8[&3Strings&8] &f");
    }

    public void sendMessage(Message message, CommandSender sender) {
        Object msgObject = enumMap.get(message);
        if(msgObject instanceof String[] msg) {
            for(String str : msg) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', str));
            }
        } else if(msgObject instanceof String) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + enumMap.get(message)));
            return;
        }
        warn(message);
    }

    public void sendMessage(Message message, Map<String, String> placeholders, CommandSender sender) {
        Object msg = enumMap.get(message);
        if(msg instanceof String[] array) {
            for(String str : array) {
                for(Map.Entry<String, String> entry : placeholders.entrySet()) {
                    str = str.replace(entry.getKey(), entry.getValue());
                }
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', str));
            }
            return;
        }

        if(msg instanceof String str) {
            for(Map.Entry<String, String> entry : placeholders.entrySet()) {
                str = str.replace(entry.getKey(), entry.getValue());
            }
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + str));
            return;
        }
        warn(message);
    }

    private void warn(@NotNull Message message) {
        Bukkit.getLogger().warning("[Strings] Unknown object type or value not found for message " + message);
    }
}
