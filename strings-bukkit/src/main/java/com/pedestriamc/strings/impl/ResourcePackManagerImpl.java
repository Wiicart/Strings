package com.pedestriamc.strings.impl;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.resources.ResourcePack;
import com.pedestriamc.strings.api.resources.ResourcePackManager;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class ResourcePackManagerImpl implements ResourcePackManager {

    private final Set<Supplier<ResourcePack>> packs = new HashSet<>();

    public ResourcePackManagerImpl(@NotNull Strings strings) {

    }

    public void requestApplication(@NotNull Player player) {
        for (ResourcePack pack : getPacks()) {
            byte[] hash = pack.hash();
            if (hash != null) {
                player.setResourcePack(pack.url(), hash);
            } else {
                player.setResourcePack(pack.url());
            }
        }
    }

    @Override
    public void addPack(@NotNull Supplier<ResourcePack> supplier) {
        packs.add(supplier);
    }

    @Override
    public void addPack(@NotNull ResourcePack pack) {
        addPack(() -> pack);
    }

    private Set<ResourcePack> getPacks() {
        return packs.stream()
                .map(Supplier::get)
                .collect(Collectors.toSet());
    }
}
