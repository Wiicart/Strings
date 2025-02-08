package com.pedestriamc.strings.moderation.chat;

import com.pedestriamc.strings.api.StringsProvider;
import com.pedestriamc.strings.api.event.PlayerChatFilteredEvent;
import com.pedestriamc.strings.api.message.Message;
import com.pedestriamc.strings.moderation.StringsModeration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LinkFilter {

    private final StringsModeration stringsModeration;
    private final String regex = "(?i)\\b((?:https?|ftp)://|www\\.)?[-a-zA-Z0-9@:%._+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_+.~#?&/=]*)";
    private final Pattern pattern = Pattern.compile(regex);
    private final List<String> urlWhitelist;

    public LinkFilter(StringsModeration stringsModeration) {
        this.stringsModeration = stringsModeration;
        this.urlWhitelist = new ArrayList<>();
        List<?> tempList = stringsModeration.getConfig().getList("url-whitelist");
        if(tempList != null)
            for(Object obj : tempList) {
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

    public String filter(String msg, Player player) {
        boolean urlReplaced = false;
        String original = msg;
        List<String> filteredElements = new ArrayList<>();
        Matcher matcher = pattern.matcher(msg);
        while(matcher.find()) {
            String match = matcher.group();
            filteredElements.add(match);
            if(!urlWhitelist.contains(normalizeUrl(match))) {
                msg = msg.replace(match, "");
                urlReplaced = true;
            }
        }

        msg = msg.trim();

        if(urlReplaced) {
            StringsProvider.get().getMessenger().sendMessage(Message.LINKS_PROHIBITED, player);
            String finalMsg = msg;
            Bukkit.getScheduler().runTask(stringsModeration, () -> {
                PlayerChatFilteredEvent event = new PlayerChatFilteredEvent(
                        player,
                        original,
                        finalMsg,
                        filteredElements
                );
                Bukkit.getPluginManager().callEvent(event);
            });
        }
        return msg;
    }

}
