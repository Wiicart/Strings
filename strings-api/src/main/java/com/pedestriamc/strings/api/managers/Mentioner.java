package com.pedestriamc.strings.api.managers;

import com.pedestriamc.strings.api.user.StringsUser;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * Class for mention utilities.
 */
public interface Mentioner {

    /**
     * Tells if this user has permission to mention anyone.
     * @param user The user whose permission is being checked.
     * @return If the user has permission.
     */
    boolean hasMentionPermission(@NotNull StringsUser user);

    /**
     * Tells if this user has permission to mention everyone with <code>@everyone</code>
     * @param user The user whose permission is being checked.
     * @return If the user has permission.
     */
    boolean hasMentionEveryonePermission(@NotNull StringsUser user);

    /**
     * Tells if this user has permission to mention everyone with <code>@{group}</code>
     * @param user The user whose permission is being checked.
     * @return If the user has permission.
     */
    boolean hasGroupMentionPermission(@NotNull StringsUser user);

    /**
     * Sends a mention to a player.
     * @param recipient The player who is being mentioned.
     * @param sender The player who is mentioning another player.
     */
    void mention(@NotNull StringsUser recipient, @NotNull StringsUser sender);

    /**
     * Sends a mention to multiple players.
     * @param recipients The players who are being mentioned.
     * @param sender The player who is mentioning the other players.
     */
    void mention(@NotNull Set<StringsUser> recipients, @NotNull StringsUser sender);

    /**
     * Processes all mentions within a Component, applying proper styling if the mention is permissible/valid.
     * @param sender The sender of the message.
     * @param message The message.
     * @return An updated Component.
     */
    @NotNull Component processMentions(@NotNull StringsUser sender, @NotNull Component message);


    /**
     * Provides a new {@link ChatProcessor}, which is more efficient and detailed than standard processMentions calls.
     * @param sender The message sender.
     * @param message The message.
     * @param audience A Set of all users who could <i>possibly</i> be mentioned in this message.
     * @return A new ChatProcessor.
     */
    @Contract("_, _, _ -> new")
    @NotNull ChatProcessor processor(StringsUser sender, Component message, Set<StringsUser> audience);

    /**
     * A chat processor for more streamlined mention processing for multiple users.
     */
    interface ChatProcessor {

        /**
         * Provides a Component with the mentions processed for a specific user.
         * @param recipient The message recipient
         * @return The processed Component
         */
        @NotNull Component processMentions(@NotNull StringsUser recipient);

        /**
         * Provides a Set of users who are mentioned in the message.
         * @return A Set of StringsUsers.
         */
        @NotNull Set<StringsUser> mentionedUsers();
    }

}
