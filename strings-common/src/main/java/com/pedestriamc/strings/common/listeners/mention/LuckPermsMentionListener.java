package com.pedestriamc.strings.common.listeners.mention;

import com.pedestriamc.strings.api.StringsPlatform;
import com.pedestriamc.strings.api.event.ChannelChatEvent;
import com.pedestriamc.strings.api.event.strings.Listener;
import com.pedestriamc.strings.api.user.StringsUser;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.node.matcher.NodeMatcher;
import net.luckperms.api.node.types.InheritanceNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * ChatListener now handles this, being kept for reference.
 */
@SuppressWarnings("unused")
public class LuckPermsMentionListener extends MentionListener {

    private final StringsPlatform strings;

    public LuckPermsMentionListener(@NotNull StringsPlatform strings) {
        super(strings);
        this.strings = strings;
    }

    @Listener
    @Override
    void onEvent(@NotNull ChannelChatEvent event) {
        super.onEvent(event);

        LuckPerms luckPerms = luckPerms();
        if (luckPerms == null) {
            // silently fail, don't want to console spam
            return;
        }

        StringsUser sender = event.getSender();
        if (!mentioner().hasGroupMentionPermission(sender)) {
            return;
        }

        Set<Group> groups = luckPerms.getGroupManager().getLoadedGroups();
        for (Group group : groups) {
            if (event.getMessage().contains("@" + group.getName())) {
               mentionGroup(luckPerms, group, sender);
            }
        }

    }

    // https://github.com/LuckPerms/LuckPerms/issues/2949
    private void mentionGroup(@NotNull LuckPerms luckPerms, @NotNull Group group, @NotNull StringsUser sender) {
        NodeMatcher<InheritanceNode> matcher = NodeMatcher.key(InheritanceNode.builder(group.getName()).build());
        luckPerms.getUserManager().searchAll(matcher)
                .exceptionally(th -> {
                    strings.warning("Failed to find group " + group.getName() + ": " + th.getMessage());
                    return null;
                }).thenAcceptAsync(result -> {
                    if (result != null) {
                        strings.sync(() ->
                                result.keySet().forEach(uuid -> {
                                    StringsUser user = strings.users().getUser(uuid);
                                    mentioner().mention(user, sender);
                                })
                        );
                    }
                });
    }

    @Nullable
    private LuckPerms luckPerms() {
        try {
            return LuckPermsProvider.get();
        } catch(Exception ex) {
            return null;
        }
    }

}
