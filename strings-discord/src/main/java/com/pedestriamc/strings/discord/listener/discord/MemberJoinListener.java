package com.pedestriamc.strings.discord.listener.discord;

import com.pedestriamc.strings.discord.manager.member.MemberDirectory;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

// Tries to keep the MemberDirectory up to date w/ new members
public class MemberJoinListener extends ListenerAdapter {

    private final MemberDirectory directory;

    public MemberJoinListener(@NotNull MemberDirectory directory) {
        this.directory = directory;
    }

    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        directory.addMember(event.getGuild(), event.getMember());
    }
}
