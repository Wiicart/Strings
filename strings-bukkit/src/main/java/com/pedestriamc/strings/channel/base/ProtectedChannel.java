package com.pedestriamc.strings.channel.base;

import com.pedestriamc.strings.api.channel.Membership;
import com.pedestriamc.strings.api.platform.Platform;
import com.pedestriamc.strings.api.user.StringsUser;
import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.channel.Type;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Map;
import java.util.Set;


public abstract class ProtectedChannel implements Channel {

    private String name;

    protected ProtectedChannel(String name) {
        this.name = name;
    }


    @Override
    public void sendMessage(@NotNull Player player, @NotNull String message) {

    }

    @Override
    public void broadcast(String message) {

    }

    @Override
    public @NotNull String getFormat() {
        return "";
    }

    @Override
    public String getBroadcastFormat() {
        return "";
    }

    @Override
    public void setFormat(@NotNull String format) {

    }

    @Override
    public @NotNull String getName() {
        return name;
    }

    @Override
    public void setName(@NotNull String name) {
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
    public void addMember(@NotNull Player player) {

    }

    @Override
    public void addMember(@NotNull StringsUser user) {
        addMember(user.getPlayer());
    }

    @Override
    public void removeMember(@NotNull Player player) {

    }

    @Override
    public void removeMember(@NotNull StringsUser user) {
        removeMember(user.getPlayer());
    }

    @Override
    public Set<Player> getMembers() {
        return Set.of();
    }

    @Override
    public @NotNull Type getType() {
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
    public boolean allows(@NotNull Permissible permissible) {
        return false;
    }

    @Override
    public boolean isCallEvent() {
        return false;
    }

    @Override
    public Platform getPlatform() {
        return Platform.BUKKIT;
    }

}
