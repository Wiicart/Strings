package com.pedestriamc.strings.commands;

import com.pedestriamc.strings.api.message.Messenger;
import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.settings.Option;
import com.pedestriamc.strings.api.utlity.Permissions;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static com.pedestriamc.strings.api.message.Message.NO_PERMS;

public final class BroadcastCommand implements CommandExecutor {

    private final @NotNull Strings strings;
    private final @NotNull String broadcastFormat;
    private final @NotNull Messenger messenger;
    private final boolean usePAPI;

    public BroadcastCommand(@NotNull Strings strings) {
        this.strings = strings;
        broadcastFormat = strings.getConfiguration().getString(Option.Text.BROADCAST_FORMAT);
        usePAPI = strings.isUsingPlaceholderAPI();
        messenger = strings.getMessenger();
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
        strings.getServer().broadcastMessage(broadcast);

        return true;
    }
}
