package com.pedestriamc.strings.channels;

import com.pedestriamc.strings.User;
import com.pedestriamc.strings.api.Membership;
import com.pedestriamc.strings.api.StringsChannel;
import com.pedestriamc.strings.api.Type;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A Channel used for Parties, these channels cannot be saved and are deleted on reboot or when all players leave.
 */
public class PartyChannel implements Channel{

    private String name;
    private Player leader;
    private final Set<Player> members;
    private String format;
    private String color;
    private final ChannelManager channelManager;

    public PartyChannel(String name, Player leader, String format, String color, ChannelManager channelManager){
        this.name = name;
        this.leader = leader;
        this.members = ConcurrentHashMap.newKeySet();
        this.format = format;
        this.color = color;
        this.channelManager = channelManager;
    }


    @Override
    public void sendMessage(Player player, String message) {

    }

    @Override
    public void broadcastMessage(String message) {

    }

    @Override
    public void closeChannel() {

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
    public void addPlayer(User user) {
        members.add(user.getPlayer());
    }

    @Override
    public void removePlayer(User user) {
        members.remove(user.getPlayer());
    }

    @Override
    public Set<Player> getMembers() {
        return null;
    }

    @Override
    public boolean doURLFilter() {
        return false;
    }

    @Override
    public void setURLFilter(boolean doURLFilter) {

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
    public void setEnabled(boolean isEnabled) {

    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public Map<String, String> getData() {
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
    public StringsChannel getStringsChannel() {
        return null;
    }

    @Override
    public void saveChannel() {
        channelManager.saveChannel(this);
    }
}
