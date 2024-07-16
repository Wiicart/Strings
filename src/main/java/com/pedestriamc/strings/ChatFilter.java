package com.pedestriamc.strings;

import com.pedestriamc.strings.message.Message;
import com.pedestriamc.strings.message.Messenger;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatFilter{
    private final Strings strings;
    private final boolean doLinkFilter;
    private final boolean doProfanityFilter;
    private final String regex = "(?i)\\b((?:https?|ftp)://|www\\.)?[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}(\\S*)?\\b";
    private final Pattern pattern = Pattern.compile(regex);
    private final List<String> urlWhitelist;
    private List<String> bannedWords;

    public ChatFilter(Strings strings){
        this.strings = strings;
        this.doLinkFilter = strings.getConfig().getBoolean("block-urls", false);
        this.doProfanityFilter = strings.getConfig().getBoolean("filter-profanity", false);
        this.urlWhitelist = new ArrayList<>();
        List<?> tempList = strings.getConfig().getList("url-whitelist");
        if(tempList != null)
        for(Object obj : tempList){
            if(obj instanceof String) {
                this.urlWhitelist.add((String) obj);

            }
        }
    }

    public String filter(String msg, Player player){
        boolean urlReplaced = false;
        if(doLinkFilter){
            Matcher matcher = pattern.matcher(msg);
            List<MatchResult> results = matcher.results().toList();
            for(MatchResult result : results){
                if(!urlWhitelist.contains(result.toString())){
                    msg = msg.replace(result.toString(), "");
                    urlReplaced = true;
                }
            }
        }
        if(urlReplaced){
            Messenger.sendMessage(Message.LINKS_PROHIBITED, player);
        }
        return msg;
    }

}
