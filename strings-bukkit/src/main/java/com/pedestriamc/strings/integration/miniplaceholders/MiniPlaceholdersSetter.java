package com.pedestriamc.strings.integration.miniplaceholders;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.annotation.Platform;
import com.pedestriamc.strings.api.user.StringsUser;
import com.pedestriamc.strings.user.User;
import io.github.miniplaceholders.api.MiniPlaceholders;
import net.kyori.adventure.pointer.Pointered;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Platform.Paper
public class MiniPlaceholdersSetter {

    private final boolean enabled;
    private final TagResolver resolver;

    public MiniPlaceholdersSetter(@NotNull Strings strings) {
        boolean classFound = false;
        TagResolver resolverTemp = null;
        try {
            Class.forName("io.github.miniplaceholders.api.MiniPlaceholders");
            classFound = true;
            resolverTemp = MiniPlaceholders.audienceGlobalPlaceholders();
        } catch(ClassNotFoundException ignored) {}

        resolver = resolverTemp;
        enabled = classFound;
    }

    public Component setPlaceholders(@NotNull Player player, @NotNull String raw) {
        Component result = null;
        if (enabled && resolver != null) {
            try {
                result = MiniMessage.miniMessage()
                        .deserialize(raw, (Pointered) player, resolver);
            } catch(Exception ignored) {}
        }
        if (result == null) {
            result = MiniMessage.miniMessage().deserialize(raw);
        }
        return result;
    }

    public Component setPlaceholders(@NotNull StringsUser user, @NotNull String raw) {
        return setPlaceholders(User.playerOf(user), raw);
    }
}
