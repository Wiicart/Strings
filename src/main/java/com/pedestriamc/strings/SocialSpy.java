package com.pedestriamc.strings;

import com.pedestriamc.strings.directmessage.PlayerDirectMessenger;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashSet;

public class SocialSpy {

    private final String socialSpyFormat;
    private final PlayerDirectMessenger playerDirectMessenger;
    private final HashSet<CommandSender> spiesList = new HashSet<>();

    public SocialSpy(Strings strings){
        this. socialSpyFormat = strings.getConfig().getString("social-spy-format");
        this.playerDirectMessenger = strings.getPlayerDirectMessenger();
    }

    public void sendOutMessage(Player sender, Player recipient, String message){
        String msg = socialSpyFormat;
        msg = playerDirectMessenger.processPlaceholders(sender, recipient, msg);
        msg = msg.replace("{message}", message);
        msg = ChatColor.translateAlternateColorCodes('&', msg);
        for(CommandSender spies : spiesList){
            spies.sendMessage(msg);
        }
    }

    public void addSpy(CommandSender sender){
        spiesList.add(sender);
    }

    public void removeSpy(CommandSender sender){
        spiesList.remove(sender);
    }
}
