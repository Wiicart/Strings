package com.pedestriamc.strings.chat;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.settings.Option;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class ChatSuggestionManager implements Listener {

    private final Strings strings;

    private final boolean emojisEnabled;

    public ChatSuggestionManager(@NotNull Strings strings) {
        this.strings = strings;
        emojisEnabled = strings.settings().get(Option.Bool.ENABLE_EMOJI_REPLACEMENT);
        strings.getServer().getPluginManager().registerEvents(this, strings);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    void onJoin(@NotNull PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (emojisEnabled) {
            player.addCustomChatCompletions(strings.emojiManager().getCodes());
        }


        Set<String> newMentionSuggestion = Set.of("@" + event.getPlayer().getName().toLowerCase(Locale.ROOT));
        Set<String> onlinePlayerMentions = new HashSet<>();

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!p.equals(player)) {
                p.addCustomChatCompletions(newMentionSuggestion);
                onlinePlayerMentions.add("@" + p.getName().toLowerCase(Locale.ROOT));
            }
        }

        player.addCustomChatCompletions(onlinePlayerMentions);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    void onQuit(@NotNull PlayerQuitEvent event) {
        Set<String> mentionSuggestion = Set.of("@" + event.getPlayer().getName().toLowerCase(Locale.ROOT));
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.removeCustomChatCompletions(mentionSuggestion);
        }
    }


}
