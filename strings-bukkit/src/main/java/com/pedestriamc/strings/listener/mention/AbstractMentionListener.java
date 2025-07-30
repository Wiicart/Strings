package com.pedestriamc.strings.listener.mention;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.chat.Mentioner;
import net.wiicart.commands.permission.Permissions;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

abstract class AbstractMentionListener implements Listener {

    private final Mentioner mentioner;

    protected AbstractMentionListener(Strings strings) {
        mentioner = strings.getMentioner();
    }

    protected Mentioner getMentioner() {
        return mentioner;
    }

    /**
     * Checks permissions for using the @everyone mention
     * @param player The player to check the permissions of
     * @return If the player has permission
     */
    protected boolean canMentionAll(Player player) {
        return Permissions.anyOfOrAdmin(player, "strings.*", "strings.mention.*", "strings.mention.all");
    }


}
