package com.pedestriamc.strings.api.message;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public interface Messageable {

    void sendMessage(@NotNull String message);

    void sendMessage(@NotNull Component message);

}
