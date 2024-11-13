package com.pedestriamc.strings.impl;

import com.pedestriamc.strings.api.channels.StringsChannel;
import com.pedestriamc.strings.chat.ChatManager;
import com.pedestriamc.strings.api.*;
import com.pedestriamc.strings.chat.Mentioner;
import com.pedestriamc.strings.chat.channels.Channel;
import com.pedestriamc.strings.chat.ChannelManager;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public final class StringsImpl implements StringsAPI {

    private final com.pedestriamc.strings.Strings strings;
    private final ChannelManager channelManager;
    private final ChatManager chatManager;
    private final Mentioner mentioner;
    private boolean apiUsed;

    public StringsImpl(@NotNull com.pedestriamc.strings.Strings strings){
        this.strings = strings;
        this.channelManager = strings.getChannelManager();
        this.chatManager = strings.getChatManager();
        this.mentioner = strings.getMentioner();
    }

    @Override
    public Set<StringsChannel> getChannels() {
        this.apiUsed = true;
        return channelManager.getChannelList().stream()
                .map(Channel::getStringsChannel)
                .collect(Collectors.toSet());
    }

    @Override
    public Optional<StringsChannel> getOptionalChannel(String name) {
        this.apiUsed = true;
        return Optional.ofNullable(this.getChannel(name));
    }

    @Override
    public @Nullable StringsChannel getChannel(String name) {
        this.apiUsed = true;
        Channel c = channelManager.getChannel(name);
        if(c == null){
            return null;
        }
        return c.getStringsChannel();
    }

    @Override
    public Optional<StringsUser> getOptionalStringsUser(UUID uuid) {
        this.apiUsed = true;
        return Optional.ofNullable(strings.getUser(uuid).getStringsUser());
    }

    @Override
    public @Nullable StringsUser getStringsUser(UUID uuid){
        this.apiUsed = true;
        return strings.getUser(uuid).getStringsUser();
    }

    @Override
    public StringsChannel[] getWorldChannels(World world) {
        this.apiUsed = true;
        Channel[] worldChannels = channelManager.getWorldChannels(world);
        StringsChannel[] worldStringsChannels = new StringsChannel[worldChannels.length];
        for(int i=0; i<worldChannels.length; i++){
            worldStringsChannels[i] = worldChannels[i].getStringsChannel();
        }
        return worldStringsChannels;
    }

    @Override
    public void deleteChannel(StringsChannel channel) {
        this.apiUsed = true;
        ChannelWrapper wrapper = (ChannelWrapper) channel;
        channelManager.deleteChannel(wrapper.getChannel());
    }

    @Override
    public boolean isPaper() {
        this.apiUsed = true;
        return strings.isPaper();
    }

    @Override
    public boolean isOnCooldown(Player player) {
        return chatManager.isOnCoolDown(player);
    }

    @Override
    public void startCooldown(Player player) {
        chatManager.startCoolDown(player);
    }

    @Override
    public void mention(Player subject, Player sender) {
        mentioner.mention(subject, sender);
    }

    @Override
    public void mention(StringsUser subject, StringsUser sender) {
        if(!(subject instanceof UserWrapper subj)){
            return;
        }
        if(!(sender instanceof UserWrapper sndr)){
            return;
        }
        mentioner.mention(subj.getUser(), sndr.getUser());
    }

    public short getVersion(){
        this.apiUsed = true;
        return strings.getPluginNum();
    }

    public boolean isApiUsed(){
        return apiUsed;
    }
}
