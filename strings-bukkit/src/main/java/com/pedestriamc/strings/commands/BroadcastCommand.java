package com.pedestriamc.strings.commands;

import com.pedestriamc.strings.api.text.format.ComponentConverter;
import com.pedestriamc.strings.configuration.Configuration;
import com.pedestriamc.strings.impl.BukkitMessenger;
import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.settings.Option;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.wiicart.commands.permission.Permissions;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.pedestriamc.strings.api.message.Message.NO_PERMS;

public final class BroadcastCommand implements CommandExecutor {

    private final Strings strings;
    private final String broadcastFormat;
    private final BukkitMessenger messenger;
    private final boolean usePAPI;

    private final @Nullable Sound sound;

    public BroadcastCommand(@NotNull Strings strings) {
        this.strings = strings;
        broadcastFormat = strings.getSettings().get(Option.Text.BROADCAST_FORMAT);
        usePAPI = strings.isUsingPlaceholderAPI();
        messenger = strings.getMessenger();
        sound = loadSound();
    }

    @Nullable
    @SuppressWarnings("PatternValidation")
    private Sound loadSound() {
        Configuration config = strings.getSettings();
        if (!config.get(Option.Bool.BROADCAST_SOUND_ENABLE)) {
            return null;
        }

        try {
            String name = config.get(Option.Text.BROADCAST_SOUND_NAME);
            Key key = Key.key(name);

            return Sound.sound()
                    .type(key)
                    .pitch(config.getFloat(Option.Double.BROADCAST_SOUND_PITCH))
                    .volume(config.getFloat(Option.Double.BROADCAST_SOUND_VOLUME))
                    .build();
        } catch(Exception e) {
            return null;
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if(!Permissions.anyOfOrAdmin(sender, "strings.*", "strings.chat.*", "strings.chat.broadcast")) {
            messenger.sendMessage(NO_PERMS, sender);
            return true;
        }

        StringBuilder builder = new StringBuilder(broadcastFormat);
        if(sender.hasPermission("strings.chat.placeholdermsg") && usePAPI && (sender instanceof Player p)) {
            PlaceholderAPI.setPlaceholders(p, builder.toString());
        }

        for(String arg : args) {
            builder.append(arg);
            builder.append(" ");
        }

        String broadcast = builder.toString();
        broadcast = ChatColor.translateAlternateColorCodes('&', broadcast);

        Audience audience = strings.adventure().all();
        audience.sendMessage(ComponentConverter.fromString(broadcast));
        if (sound != null) {
            audience.playSound(sound);
        }

        return true;
    }
}
