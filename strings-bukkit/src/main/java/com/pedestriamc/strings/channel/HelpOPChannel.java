package com.pedestriamc.strings.channel;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.StringsPlatform;
import com.pedestriamc.strings.api.channel.data.IChannelBuilder;
import com.pedestriamc.strings.api.event.channel.ChannelChatEvent;
import com.pedestriamc.strings.api.text.format.StringsTextColor;
import com.pedestriamc.strings.api.user.StringsUser;
import com.pedestriamc.strings.chat.MessageProcessor;
import com.pedestriamc.strings.channel.base.ProtectedChannel;
import com.pedestriamc.strings.api.message.Message;
import com.pedestriamc.strings.impl.BukkitMessenger;
import com.pedestriamc.strings.user.User;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatColor;
import net.wiicart.commands.permission.Permissions;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.permissions.Permissible;
import org.jetbrains.annotations.NotNull;

import static com.pedestriamc.strings.api.channel.data.IChannelBuilder.Identifier;

import java.util.HashSet;
import java.util.Set;

public class HelpOPChannel extends ProtectedChannel {

    public static final Identifier IDENTIFIER = Identifier.HELPOP;

    private final Strings strings;
    private final MessageProcessor messageProcessor;

    private final boolean callEvent;
    private String format;
    private boolean urlFilter;
    private boolean profanityFilter;
    private final BukkitMessenger messenger;
    private final boolean usePAPI;

    public HelpOPChannel(@NotNull Strings strings, String format, boolean callEvent, boolean urlFilter, boolean profanityFilter)
    {
        super("helpop");
        this.strings = strings;
        this.callEvent = callEvent;
        this.format = format;
        this.urlFilter = urlFilter;
        this.profanityFilter = profanityFilter;
        this.messenger = strings.getMessenger();
        usePAPI = strings.isUsingPlaceholderAPI();
        messageProcessor = new MessageProcessor(strings, this);
    }

    public HelpOPChannel(@NotNull StringsPlatform plugin, @NotNull IChannelBuilder<?> builder) {
        this((Strings) plugin, builder);
    }

    public HelpOPChannel(@NotNull Strings strings, @NotNull IChannelBuilder<?> data)
    {
        super("helpop");
        this.strings = strings;
        this.messenger = strings.getMessenger();
        this.callEvent = data.isCallEvent();
        this.format = data.getFormat();
        this.urlFilter = data.isDoUrlFilter();
        this.profanityFilter = data.isDoProfanityFilter();
        usePAPI = strings.isUsingPlaceholderAPI();
        messageProcessor = new MessageProcessor(strings, this);
    }

    @Override
    public void sendMessage(@NotNull StringsUser user, @NotNull String message) {
        Set<Player> members = getRecipients();
        Player player = User.playerOf(user);

        String messageFormat = generateTemplate(player);
        message = messageProcessor.processMessage(player, message);
        String finalMessage = message;
        String formattedMessage = messageFormat.replace("{message}", finalMessage);
        if(callEvent) {
            Bukkit.getScheduler().runTask(strings, () -> {
                AsyncPlayerChatEvent event = new ChannelChatEvent(false, player, finalMessage, members, this, true);
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
            if(Permissions.anyOfOrAdmin(onlinePlayer, "strings.helpop.receive", "strings.helpop.*", "strings.*")) {
                members.add(onlinePlayer);
            }
        }
        return members;
    }

    private String generateTemplate(Player player) {
        User user = strings.users().getUser(player);
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
    public String getDefaultColor() {
        return StringsTextColor.RED.toString();
    }

    @Override
    public boolean callsEvents() {
        return callEvent;
    }

    @Override
    public @NotNull String getFormat() {
        return format;
    }

    @Override
    public void setFormat(@NotNull String format) {
        this.format = format;
    }
    @Override
    public boolean isUrlFiltering() {
        return urlFilter;
    }

    @Override
    public void setUrlFilter(boolean urlFilter) {
        this.urlFilter = urlFilter;
    }

    @Override
    public boolean isProfanityFiltering() {
        return profanityFilter;
    }

    @Override
    public void setProfanityFilter(boolean profanityFilter) {
        this.profanityFilter = profanityFilter;
    }

    @Override
    public boolean allows(@NotNull Permissible permissible) {
        return Permissions.anyOfOrAdmin(permissible, "strings.*", "strings.helpop.use");
    }

}
