package com.pedestriamc.strings;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.EnumMap;

public class Messenger {

    private static final EnumMap<Message, Object> enumMap = new EnumMap<>(Message.class);
    private static String prefix;

    public static void initialize(){
        FileConfiguration config = Strings.getInstance().getMessagesFileConfig();
        for(Message msg : Message.values()){
            String configValue = msg.toString().replace("_", "-").toLowerCase();
            try{
                if(config.isList(configValue)){
                    enumMap.put(msg, config.getStringList(configValue).toArray(new String[0]));
                }else{
                    enumMap.put(msg, config.getString(configValue));
                }
            }catch(NullPointerException e){
                Bukkit.getLogger().warning("[Strings] Unable to find message for " + msg);
            }
        }
        prefix = config.getString("prefix", "&8[&3Strings&8] &f");
    }

    public static void sendMessage(Message message, CommandSender sender){
        if(enumMap.get(message) instanceof String[] msg){
            for(String str : msg){
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', str));
            }
        }else if(enumMap.get(message) instanceof String){
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + enumMap.get(message)));
        }else{
            Bukkit.getLogger().info("[Strings] Unknown object type or value not found for message " + message.toString());
        }
