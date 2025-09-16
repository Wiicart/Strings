package com.pedestriamc.strings.api.resources;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class ResourcePack {

    @NotNull
    public static ResourcePack of(@NotNull String url) {
        return new ResourcePack(url, null);
    }

    @NotNull
    public static ResourcePack of(@NotNull String url, byte[] hash) {
        return new ResourcePack(url, hash);
    }

    private final String url;
    private final byte[] hash;

    private ResourcePack(String url, byte[] hash) {
        this.url = url;
        this.hash = hash;
    }

    public String url() {
        return url;
    }

    public byte[] hash() {
        return Arrays.copyOf(hash, hash.length);
    }

}
