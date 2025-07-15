package com.pedestriamc.strings.discord.manager.member;

import com.pedestriamc.strings.discord.listener.discord.MemberJoinListener;
import com.pedestriamc.strings.discord.manager.DiscordManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A cache for Members in Guilds
 */
public final class MemberDirectory {

    private final Map<Guild, Set<Member>> map = new ConcurrentHashMap<>();

    public MemberDirectory(@NotNull DiscordManager manager, @NotNull JDA jda) {
        initializeMap(manager);
        populateMap();
        jda.addEventListener(new MemberJoinListener(this));
    }

    private void initializeMap(@NotNull DiscordManager manager) {
        Set<Guild> guilds = new HashSet<>();
        for(MessageChannel channel : manager.getDiscordChannels()) {
            if(channel instanceof GuildChannel guildChannel) {
                guilds.add(guildChannel.getGuild());
            }
        }

        for(Guild guild : guilds) {
            map.put(guild, new HashSet<>());
        }
    }

    private void populateMap() {
        map.forEach((guild, members) -> guild.loadMembers().onSuccess(members::addAll));
    }

    public @NotNull Set<Member> getMembers(@NotNull Guild guild) {
        Set<Member> members = map.get(guild);
        if(members != null) {
            return new HashSet<>(members);
        } else {
            return new HashSet<>();
        }
    }

    public void addMember(@NotNull Guild guild, @NotNull Member member) {
        Set<Member> members = map.get(guild);
        if(members != null) {
            members.add(member);
        }
    }
}
