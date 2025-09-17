package com.pedestriamc.strings.api.utlity;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.jetbrains.annotations.NotNull;

public record SerialComponent(String json) implements ComponentLike {

    @Override
    public @NotNull Component asComponent() {
        return GsonComponentSerializer.gson().deserialize(json);
    }

}
