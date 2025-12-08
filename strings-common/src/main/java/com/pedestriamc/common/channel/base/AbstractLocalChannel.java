package com.pedestriamc.common.channel.base;

import com.pedestriamc.strings.api.StringsPlatform;
import com.pedestriamc.strings.api.channel.Membership;
import com.pedestriamc.strings.api.channel.data.LocalChannelBuilder;
import com.pedestriamc.strings.api.channel.local.LocalChannel;
import com.pedestriamc.strings.api.channel.local.Locality;
import com.pedestriamc.strings.api.user.StringsUser;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Class that implements common LocalChannel elements
 */
public abstract class AbstractLocalChannel<T> extends AbstractChannel implements LocalChannel<T> {

    private final Set<Locality<T>> worlds;
    private final Set<Locality<T>> unmodifiableView;

    protected AbstractLocalChannel(@NotNull StringsPlatform strings, @NotNull LocalChannelBuilder<T> data) {
        super(strings, data);
        worlds = new HashSet<>(data.getWorlds());
        unmodifiableView = Collections.unmodifiableSet(worlds);

        if (worlds.isEmpty()) {
            throw new IllegalArgumentException("Worlds cannot be empty");
        }
    }

    @Override
    public boolean containsInScope(@NotNull StringsUser user) {
        return containsLocality(user.getLocality());
    }

    @Override
    @NotNull
    public Set<Locality<T>> getWorlds() {
        return unmodifiableView;
    }

    @Override
    public void setWorlds(@NotNull Set<Locality<T>> worlds) {
        this.worlds.clear();
        this.worlds.addAll(worlds);
    }

    @Override
    public boolean allows(@NotNull StringsUser user) {
        return (getMembership() == Membership.DEFAULT && containsLocality(user.getLocality()) || super.allows(user));
    }

    @Override
    public Map<String, Object> getData() {
        Map<String, Object> map = super.getData();
        map.put("worlds", getWorldNames());
        return map;
    }

    @Override
    public @NotNull Set<StringsUser> getPlayersInScope() {
        return switch(getMembership()) {
            case DEFAULT -> universalSet();
            case PERMISSION -> {
                HashSet<StringsUser> scoped = new HashSet<>(universalSet());
                scoped.removeIf(user -> !allows(user));
                yield scoped;
            }
            default -> {
                HashSet<StringsUser> scoped = new HashSet<>(getMembers());
                scoped.addAll(getMonitors());
                yield scoped;
            }
        };
    }

    @Override
    public boolean containsWorld(@NotNull T world) {
        for (Locality<T> locality : worlds) {
            if (locality.get().equals(world)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean containsLocality(@NotNull Locality<?> locality) {
        return worlds.contains(locality);
    }

    protected List<String> getWorldNames() {
        ArrayList<String> worldNames = new ArrayList<>();
        for (Locality<T> w : getWorlds()) {
            worldNames.add(w.getName());
        }
        return worldNames;
    }

    /**
     * Provides a Set of all Players that can be eligible to receive messages.
     * Intended for determining recipients for an admin.
     * @return A populated Set
     */
    protected Set<StringsUser> universalSet() {
        HashSet<StringsUser> set = new HashSet<>(getMembers());
        set.addAll(getMonitors());

        for(Locality<T> w : worlds) {
            set.addAll(w.getUsers());
        }

        return set;
    }
}


