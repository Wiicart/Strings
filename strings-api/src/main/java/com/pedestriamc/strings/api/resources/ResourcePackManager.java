package com.pedestriamc.strings.api.resources;

import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public interface ResourcePackManager {

    /**
     * Adds a ResourcePack to the list of packs players are prompted to load
     * @param supplier A Supplier supplying the pack
     */
    void addPack(@NotNull Supplier<ResourcePack> supplier);

    /**
     * Adds a ResourcePack to the list of packs players are prompted to load
     * @param pack The ResourcePack
     */
    void addPack(@NotNull ResourcePack pack);
}
