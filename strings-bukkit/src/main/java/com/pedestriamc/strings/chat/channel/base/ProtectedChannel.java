package com.pedestriamc.strings.chat.channel.base;

import com.pedestriamc.strings.api.channel.Membership;
import com.pedestriamc.strings.api.StringsUser;
import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.channel.Type;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;

import java.util.Collections;
import java.util.Map;
import java.util.Set;


public abstract class ProtectedChannel implements Channel {

    private String name;

    protected ProtectedChannel(String name) {
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
        this.name = name;
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
    public void addMember(Player player) {

    }

    @Override
    public void addMember(StringsUser user) {
        addMember(user.getPlayer());
    }

    @Override
    public void removeMember(Player player) {

    }

    @Override
    public void removeMember(StringsUser user) {
        removeMember(user.getPlayer());
    }

    @Override
    public Set<Player> getMembers() {
        return Set.of();
    }

    @Override
    public Type getType() {
        return Type.PROTECTED;
    }

    @Override
    public Map<String, Object> getData() {
        return Collections.emptyMap();
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
    public boolean allows(Permissible permissible) {
        return false;
    }

    @Override
    public boolean isCallEvent() {
        return false;
    }
}
