package com.pedestriamc.strings.message;

import com.pedestriamc.strings.Strings;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.EnumMap;
import java.util.Map;

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
        Object msgObject = enumMap.get(message);
        if(msgObject instanceof String[] msg){
            for(String str : msg){
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', str));
            }
        }else if(msgObject instanceof String){
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + enumMap.get(message)));
        }else{
            Bukkit.getLogger().info("[Strings] Unknown object type or value not found for message " + message.toString());
        }
    }

    public static void channelCmdMessage(Message message, CommandSender sender, String playerName, String channelName){

        Object msg = enumMap.get(message);

        if(playerName == null){
            playerName = "null";
        }
        if(channelName == null){
            channelName = "null";
        }
        if(msg instanceof String[]){
            for(String str : (String[]) msg){
                str = str.replace("{player}", playerName);
                str = str.replace("{channel}", channelName);
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', str));
            }
            return;
        }
        if(msg instanceof String str){
            str = str.replace("{player}", playerName);
            str = str.replace("{channel}", channelName);
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + str));
        }

    }

    public static void sendMessage(Message message, Map<String, String> placeholders, CommandSender sender){
        Object msg = enumMap.get(message);
        if(msg instanceof String[]){
            for(String str : (String[]) msg){
                for(Map.Entry<String, String> entry : placeholders.entrySet()) {
                    str = str.replace(entry.getKey(), entry.getValue());
                }
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', str));
            }
            return;
        }
        if(msg instanceof String str){
            for(Map.Entry<String, String> entry : placeholders.entrySet()) {
                str = str.replace(entry.getKey(), entry.getValue());
            }
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + str));
            return;
        }
        Bukkit.getLogger().info("[Strings] Unknown object type or value not found for message " + message.toString());
    }
}
