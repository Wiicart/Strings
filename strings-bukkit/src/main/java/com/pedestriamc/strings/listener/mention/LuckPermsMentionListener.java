package com.pedestriamc.strings.listener.mention;

import com.google.common.base.Preconditions;
import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.chat.Mentioner;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.node.matcher.NodeMatcher;
import net.luckperms.api.node.types.InheritanceNode;
import net.wiicart.commands.permission.Permissions;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

/**
 * Mention Listener used when LuckPerms is available, allowing for group mentions.
 */
public class LuckPermsMentionListener extends AbstractMentionListener {

    private final Strings strings;

    public LuckPermsMentionListener(@NotNull Strings strings) {
        super(strings);
        this.strings = strings;
        Preconditions.checkState(strings.getServer()
                .getPluginManager()
                .getPlugin("LuckPerms") != null, "LuckPerms plugin is not loaded");
    }

    @EventHandler
    void onEvent(@NotNull AsyncPlayerChatEvent event) {
        Player sender = event.getPlayer();
        String message = event.getMessage().toLowerCase(Locale.ROOT);
        if(!Mentioner.hasMentionPermission(sender)) {
            return;
        }

        LuckPerms luckPerms = getLuckPerms();
        if (luckPerms == null) {
            strings.warning("Failed to process message for mentions: LuckPerms unavailable.");
            return;
        }

        for(Player subj : Bukkit.getOnlinePlayers()) {
            if(message.contains("@" + subj.getName().toLowerCase(Locale.ROOT))) {
                getMentioner().mention(subj, sender);
            }
        }

        if(canMentionAll(sender) && message.contains("@everyone".toLowerCase(Locale.ROOT))) {
            for(Player subj : Bukkit.getOnlinePlayers()) {
                getMentioner().mention(subj, sender);
            }
        }

        if(Permissions.anyOfOrAdmin(sender, "strings.*", "strings.mention.*", "strings.mention.group")) {
            for(Group group : luckPerms.getGroupManager().getLoadedGroups()) {
                if(message.contains("@" + group.getName().toLowerCase(Locale.ROOT))) {
                    mentionGroup(luckPerms, group, sender);
                }
            }
        }

    }

    // https://github.com/LuckPerms/LuckPerms/issues/2949
    private void mentionGroup(@NotNull LuckPerms luckPerms, @NotNull Group group, @NotNull Player player) {
        NodeMatcher<InheritanceNode> matcher = NodeMatcher.key(InheritanceNode.builder(group.getName()).build());
        luckPerms.getUserManager().searchAll(matcher).exceptionally(throwable -> {
            strings.warning("Failed to message group '" + group.getName() + "'");
            strings.warning(throwable.getMessage());
            return null;
        }).thenAcceptAsync(result -> {
            if(result == null) {
                return;
            }

            Bukkit.getScheduler().runTask(strings, () -> {
                Mentioner mentioner = getMentioner();
                result.keySet().forEach(uuid -> {
                    Player p = Bukkit.getPlayer(uuid);
                    if(p != null) {
                        mentioner.mention(p, player);
                    }
                });
            });
        });
    }

    private @Nullable LuckPerms getLuckPerms() {
        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider == null) {
            return null;
        }
        return provider.getProvider();
    }
}
