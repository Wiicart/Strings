package com.pedestriamc.strings.chat.channels;

import com.pedestriamc.strings.api.Membership;
import com.pedestriamc.strings.api.StringsUser;
import com.pedestriamc.strings.api.channels.Channel;
import com.pedestriamc.strings.api.channels.Type;
import com.sun.source.tree.Tree;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

public abstract class ProtectedChannel implements Channel {

    private final String name;

    public ProtectedChannel(String name) {
        this.name = name;
    }


    @Override
    public void sendMessage(Player player, String message) {

    }

    @Override
    public void broadcastMessage(String message) {

    }

    @Override
    public String getFormat() {
        return "";
    }

    @Override
    public void setFormat(String format) {

    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {

    }

    @Override
    public String getDefaultColor() {
        return "";
    }

    @Override
    public void setDefaultColor(String defaultColor) {

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
    public void addPlayer(Player player) {

    }

    @Override
    public void addPlayer(StringsUser user) {

    }

    @Override
    public void removePlayer(Player player) {

    }

    @Override
    public void removePlayer(StringsUser user) {

    }

    @Override
    public Set<Player> getMembers() {
        return Set.of();
    }

    @Override
    public final Type getType() {
        return Type.PROTECTED;
    }

    @Override
    public Map<String, Object> getData() {
        return Map.of();
    }

    @Override
    public Membership getMembership() {
        return Membership.PROTECTED;
    }

    @Override
    public int getPriority() {
        return -1;
    }

    @Override
    public void saveChannel() {

    }

    @Override
    public boolean allows(Player player) {
        return false;
    }

    @Override
    public boolean isCallEvent() {
        return false;
    }
}
