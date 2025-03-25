package com.pedestriamc.strings.chat.channel;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.channel.data.ChannelData;
import com.pedestriamc.strings.api.event.ChannelChatEvent;
import com.pedestriamc.strings.chat.MessageProcessor;
import com.pedestriamc.strings.chat.channel.base.ProtectedChannel;
import com.pedestriamc.strings.api.message.Message;
import com.pedestriamc.strings.api.message.Messenger;
import com.pedestriamc.strings.user.User;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.permissions.Permissible;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class HelpOPChannel extends ProtectedChannel {

    private final Strings strings;
    private final MessageProcessor messageProcessor;

    private final boolean callEvent;
    private String format;
    private String defaultColor;
    private boolean urlFilter;
    private boolean profanityFilter;
    private final Messenger messenger;
    private final boolean usePAPI;

    public HelpOPChannel(@NotNull Strings strings, String format, String defaultColor, boolean callEvent, boolean urlFilter, boolean profanityFilter)
    {
        super("helpop");
        this.strings = strings;
        this.callEvent = callEvent;
        this.format = format;
        this.defaultColor = defaultColor;
        this.urlFilter = urlFilter;
        this.profanityFilter = profanityFilter;
        this.messenger = strings.getMessenger();
        usePAPI = strings.usePlaceholderAPI();
        messageProcessor = new MessageProcessor(strings, this);
    }

    public HelpOPChannel(Strings strings, ChannelData data)
    {
        super("helpop");
        this.strings = strings;
        this.messenger = strings.getMessenger();
        this.callEvent = data.isCallEvent();
        this.format = data.getFormat();
        this.defaultColor = data.getDefaultColor();
        this.urlFilter = data.isDoUrlFilter();
        this.profanityFilter = data.isDoProfanityFilter();
        usePAPI = strings.usePlaceholderAPI();
        messageProcessor = new MessageProcessor(strings, this);
    }

    @Override
    public void sendMessage(@NotNull Player player, @NotNull String message) {
        Set<Player> members = getRecipients();
        String messageFormat = generateTemplate(player);
        message = messageProcessor.processMessage(player, message);
        String finalMessage = message;
        String formattedMessage = messageFormat.replace("{message}", finalMessage);
        if(callEvent){
            Bukkit.getScheduler().runTask(strings, () -> {
                AsyncPlayerChatEvent event = new ChannelChatEvent(false, player, finalMessage, members, this);
                Bukkit.getPluginManager().callEvent(event);
                if(!event.isCancelled()) {
                    for(Player p : members) {
                        p.sendMessage(formattedMessage);
                    }
                    Bukkit.getLogger().info(ChatColor.stripColor(formattedMessage));
                }
            });
        } else {
            for(Player p : members) {
                p.sendMessage(formattedMessage);
            }
            Bukkit.getLogger().info(ChatColor.stripColor(formattedMessage));
        }
        messenger.sendMessage(Message.HELPOP_SENT, player);
    }

    private Set<Player> getRecipients() {
        HashSet<Player> members = new HashSet<>();
        for(OfflinePlayer op : Bukkit.getOperators()) {
            if(op.getName() != null) {
                Player p = Bukkit.getPlayer(op.getName());
                if(p != null) {
                    members.add(p);
                }
            }
        }

        for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if(onlinePlayer.hasPermission("strings.helpop.receive") || onlinePlayer.hasPermission("strings.helpop.*") || onlinePlayer.hasPermission("strings.*")) {
                members.add(onlinePlayer);
            }
        }
        return members;
    }

    private String generateTemplate(Player player) {
        User user = strings.getUser(player);
        String template = getFormat();

        template = template
                .replace("{prefix}", user.getPrefix())
                .replace("{suffix}", user.getSuffix())
                .replace("{displayname}", user.getDisplayName())
                .replace("{message}", user.getChatColor(this) + "{message}");

        if(usePAPI) {
            template = PlaceholderAPI.setPlaceholders(player, template);
        }
        template = org.bukkit.ChatColor.translateAlternateColorCodes('&', template);

        return template;
    }

    @Override
    public boolean isCallEvent() {
        return callEvent;
    }

    @Override
    public String getFormat() {
        return format;
    }

    @Override
    public void setFormat(String format) {
        this.format = format;
    }

    @Override
    public String getDefaultColor() {
        return defaultColor;
    }

    @Override
    public void setDefaultColor(String defaultColor) {
        this.defaultColor = defaultColor;
    }

    @Override
    public boolean doUrlFilter() {
        return urlFilter;
    }

    @Override
    public void setUrlFilter(boolean urlFilter) {
        this.urlFilter = urlFilter;
    }

    @Override
    public boolean doProfanityFilter() {
        return profanityFilter;
    }

    @Override
    public void setProfanityFilter(boolean profanityFilter) {
        this.profanityFilter = profanityFilter;
    }

    @Override
    public boolean allows(@NotNull Permissible permissible) {
        return (permissible.hasPermission("strings.helpop.use") ||  permissible.hasPermission("strings.*"));
    }

}
