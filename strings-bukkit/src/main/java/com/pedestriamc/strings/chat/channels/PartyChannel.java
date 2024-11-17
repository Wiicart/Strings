package com.pedestriamc.strings.chat.channels;

import com.pedestriamc.strings.api.StringsUser;
import com.pedestriamc.strings.api.channels.Channel;
import com.pedestriamc.strings.api.channels.ChannelLoader;
import com.pedestriamc.strings.user.User;
import com.pedestriamc.strings.api.Membership;
import com.pedestriamc.strings.api.channels.Type;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A Channel used for Parties, these channels cannot be saved and are deleted on reboot or when all players leave.
 */
public class PartyChannel implements Channel {

    private String name;
    private Player leader;
    private final Set<Player> members;
    private String format;
    private String color;
    private final ChannelLoader channelLoader;

    public PartyChannel(String name, Player leader, String format, String color, ChannelLoader channelLoader){
        this.name = name;
        this.leader = leader;
        this.members = ConcurrentHashMap.newKeySet();
        this.format = format;
        this.color = color;
        this.channelLoader = channelLoader;
    }


    @Override
    public void sendMessage(Player player, String message) {

    }

    @Override
    public void broadcastMessage(String message) {

    }

    @Override
    public String getFormat() {
        return format;
    }

    @Override
    public String getDefaultColor() {
        return null;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void setDefaultColor(String defaultColor) {

    }

    @Override
    public void setFormat(String format) {

    }

    @Override
    public void addPlayer(Player player) {
        members.add(player);
    }

    @Override
    public void removePlayer(Player player) {
        members.remove(player);
    }

    @Override
    public void addPlayer(StringsUser user) {
        members.add(user.getPlayer());
    }

    @Override
    public void removePlayer(StringsUser user) {
        members.remove(user.getPlayer());
    }

    @Override
    public Set<Player> getMembers() {
        return null;
    }

    @Override
    public boolean doUrlFilter() {
        return false;
    }

    @Override
    public void setUrlFilter(boolean doUrlFilter) {

    }

    @Override
    public boolean doProfanityFilter() {
        return false;
    }

    @Override
    public void setProfanityFilter(boolean doProfanityFilter) {

    }

    @Override
    public boolean doCooldown() {
        return false;
    }

    @Override
    public void setDoCooldown(boolean doCooldown) {

    }

    @Override
    public Type getType() {
        return Type.PARTY;
    }

    @Override
    public Map<String, Object> getData() {
        return null;
    }

    @Override
    public Membership getMembership() {
        return Membership.PARTY;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public boolean isCallEvent() {
        return false;
    }

    @Override
    public void saveChannel() {
        channelLoader.saveChannel(this);
    }

    @Override
    public boolean allows(Player player) {
        return (
                player.hasPermission("strings.channels." + getName()) ||
                player.hasPermission("strings.channels.*") ||
                player.hasPermission("strings.*"
                )
        );
    }
}
