package com.pedestriamc.strings.moderation;

import com.pedestriamc.strings.api.ModerationAPI;
import com.pedestriamc.strings.moderation.chat.ChatModerationManager;
import org.bukkit.entity.Player;

public class APIImplementation implements ModerationAPI {

    private final ChatModerationManager manager;

    public APIImplementation(StringsModeration stringsModeration) {
        manager = stringsModeration.getChatModerationManager();
    }

    @Override
    public boolean isOnCooldown(Player player) {
        return manager.isOnCooldown(player);
    }

    @Override
    public void startCooldown(Player player) {
        manager.startCooldown(player);
    }

    @Override
    public boolean isRepeating(Player player, String message) {
        return manager.isRepeating(player, message);
    }


}
