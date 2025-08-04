package com.pedestriamc.strings.impl;

import com.pedestriamc.strings.api.message.Message;
import com.pedestriamc.strings.api.message.MessageContext;
import com.pedestriamc.strings.api.message.Messageable;
import com.pedestriamc.strings.api.message.Messenger;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.Map;

/**
 * A system for sending messages to Players.
 * Values are read in from "messages.yml"
 */
public final class BukkitMessenger implements Messenger {

    private final EnumMap<Message, Object> enumMap = new EnumMap<>(Message.class);
    private final String prefix;

    public BukkitMessenger(FileConfiguration config) {
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


    public void sendMessage(Message message, CommandSender recipient) {
        sendMessage(message, MessageableAdapter.of(recipient));
    }

    public void sendMessage(Message message, Map<String, String> placeholders, CommandSender recipient) {
        sendMessage(message, MessageableAdapter.of(recipient), placeholders);
    }

    @Override
    public void sendMessage(@NotNull Message message, @NotNull Messageable recipient, @NotNull Map<String, String> placeholders) {
        Object msg = enumMap.get(message);
        if(msg instanceof String[] array) {
            for(String str : array) {
                for(Map.Entry<String, String> entry : placeholders.entrySet()) {
                    str = str.replace(entry.getKey(), entry.getValue());
                }
                recipient.sendMessage(applyColor(str));
            }
            return;
        }

        if(msg instanceof String str) {
            for(Map.Entry<String, String> entry : placeholders.entrySet()) {
                str = str.replace(entry.getKey(), entry.getValue());
            }
            recipient.sendMessage(applyColor(prefix + str));
            return;
        }

        warn(message);
    }

    @Override
    public void sendMessage(@NotNull Message message, @NotNull Messageable recipient) {
        Object msgObject = enumMap.get(message);

        if(msgObject instanceof String[] msg) {
            for(String str : msg) {
                recipient.sendMessage(applyColor(str));
            }

            return;
        } else if(msgObject instanceof String) {
            recipient.sendMessage(applyColor(prefix + enumMap.get(message)));

            return;
        }

        warn(message);
    }

    public void batchSend(MessageContext @NotNull ... queries) {
        for (MessageContext query : queries) {
            if (query.placeholders() != null) {
                sendMessage(query.message(), query.recipient(), query.placeholders());
            } else {
                sendMessage(query.message(), query.recipient());
            }
        }
    }

    private void warn(@NotNull Message message) {
        Bukkit.getLogger().warning("[Strings] Unknown object type or value not found for message " + message);
    }

    @Contract("_ -> new")
    private @NotNull String applyColor(@NotNull String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }
}
