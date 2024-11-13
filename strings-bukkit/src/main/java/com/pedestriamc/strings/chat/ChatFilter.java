package com.pedestriamc.strings.chat;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.message.Message;
import com.pedestriamc.strings.message.Messenger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatFilter{
    private final String regex = "(?i)\\b((?:https?|ftp):\\/\\/|www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*)";
    private final Pattern pattern = Pattern.compile(regex);
    private final List<String> urlWhitelist;
    private List<String> bannedWords;
    private final Messenger messenger;

    public ChatFilter(@NotNull Strings strings){
        this.urlWhitelist = new ArrayList<>();
        this.messenger = strings.getMessenger();
        List<?> tempList = strings.getConfig().getList("url-whitelist");
        if(tempList != null)
            for(Object obj : tempList){
            if(obj instanceof String) {
                this.urlWhitelist.add(normalizeUrl((String) obj));
            }
        }
    }
    private String normalizeUrl(String url) {
        if (url.startsWith("http://")) {
            url = url.substring(7);
        } else if (url.startsWith("https://")) {
            url = url.substring(8);
        }
        if (url.startsWith("www.")) {
            url = url.substring(4);
        }
        return url.toLowerCase();
    }

    public String urlFilter(String msg, Player player){
        boolean urlReplaced = false;
        Matcher matcher = pattern.matcher(msg);
        while(matcher.find()){
            String match = matcher.group();
            if(!urlWhitelist.contains(normalizeUrl(match))){
                msg = msg.replace(match, "");
                urlReplaced = true;
            }
        }
        if(urlReplaced){
            messenger.sendMessage(Message.LINKS_PROHIBITED, player);
        }
        return msg;
    }

    public String profanityFilter(String msg, Player player){
        return msg;
    }
}
