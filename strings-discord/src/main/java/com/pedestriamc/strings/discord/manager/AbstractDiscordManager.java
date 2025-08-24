package com.pedestriamc.strings.discord.manager;

import com.pedestriamc.strings.api.StringsAPI;
import com.pedestriamc.strings.api.StringsProvider;
import com.pedestriamc.strings.api.collections.BoundedLinkedBuffer;
import com.pedestriamc.strings.api.event.channel.ChannelChatEvent;
import com.pedestriamc.strings.api.user.StringsUser;
import com.pedestriamc.strings.discord.StringsDiscord;
import com.pedestriamc.strings.discord.configuration.Option;
import com.pedestriamc.strings.discord.configuration.Settings;
import com.pedestriamc.strings.discord.manager.console.KConsoleManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.kyori.adventure.chat.SignedMessage;
import net.wiicart.commands.permission.Permissions;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

abstract class AbstractDiscordManager implements DiscordManager {

    public static final Pattern DISCORD_USER_MENTION = Pattern.compile("<@!?(\\d+)>");

    public static final Pattern DISCORD_ROLE_MENTION = Pattern.compile("<@&(\\d+)>");

    private final StringsDiscord strings;
    private final KConsoleManager consoleManager;

    private final BoundedLinkedBuffer<Map.Entry<SignedMessage, Message>> buffer;

    private final String discordToGameMentionFormat;
    private final String discordFormat; // Used for messages coming from Minecraft, going to Discord
    private final String craftFormat; // Used for messages coming from Discord, going to Minecraft

    private final boolean mentionsFromGameEnabled; // If this plugin should convert text-mentions to Discord's format
    private final boolean mentionsToGameEnabled; // If Strings should parse mentions from Discord messages
    private final boolean shouldSyncDeletions;

    protected AbstractDiscordManager(@NotNull StringsDiscord strings) {
        this.strings = strings;

        KConsoleManager tempConsoleManager = null;
        try {
            tempConsoleManager = new KConsoleManager(strings);
        } catch(Exception ignored) {}
        consoleManager = tempConsoleManager;

        buffer = new BoundedLinkedBuffer<>(100);

        Settings settings = strings.getSettings();

        discordFormat = settings.getString(Option.Text.DISCORD_FORMAT);
        craftFormat = settings.getColoredString(Option.Text.MINECRAFT_FORMAT);

        mentionsFromGameEnabled = settings.getBoolean(Option.Bool.ENABLE_MENTIONS_FROM_GAME);
        mentionsToGameEnabled = settings.getBoolean(Option.Bool.ENABLE_MENTIONS_TO_GAME);
        shouldSyncDeletions = settings.getBoolean(Option.Bool.SYNCHRONIZE_DELETIONS);

        discordToGameMentionFormat = settings.getColoredString(Option.Text.MENTION_FORMAT);
    }

    @Override
    public void shutdown() {
        consoleManager.shutdown();
    }

    // Prepares placeholders for a message being sent to Discord
    @NotNull
    protected String processDiscordPlaceholders(@NotNull ChannelChatEvent event) {
        try {
            StringsAPI api = StringsProvider.get();

            StringsUser user = api.getStringsUser(event.getPlayer());
            if(user == null) {
                throw new IllegalStateException("User not found");
            }

            return discordFormat
                    .replace("{channel}", event.getChannel().getName())
                    .replace("{display-name}", user.getDisplayName())
                    .replace("{username}", user.getName())
                    .replace("{prefix}", user.getPrefix())
                    .replace("{suffix}", user.getSuffix())
                    .replace("{message}", event.getMessage());
        } catch(IllegalStateException e) {
            strings.getLogger().warning("Failed to load User data from Strings");

            return discordFormat
                    .replace("{channel}", event.getChannel().getName())
                    .replace("{display-name}", event.getPlayer().getDisplayName())
                    .replace("{username}", event.getPlayer().getName())
                    .replace("{prefix}", "")
                    .replace("{suffix}", "")
                    .replace("{message}", event.getMessage());
        }
    }

    // Prepares placeholders for a message being sent to Minecraft
    @NotNull
    protected String processCraftPlaceholders(@NotNull MessageReceivedEvent event) {
        Member member = event.getGuild().getMember(event.getAuthor());
        String role = "";
        if(member != null) {
            List<Role> roles = member.getRoles();
            if(!roles.isEmpty()) {
                role = roles.getFirst().getName();
            }
        }

        String message = craftFormat
                .replace("{role}", role)
                .replace("{channel}", event.getChannel().getName())
                .replace("{username}", event.getAuthor().getName())
                .replace("{nickname}", event.getAuthor().getEffectiveName());
        message = ChatColor.translateAlternateColorCodes('&', message);

        return message.replace("{message}", event.getMessage().getContentRaw());
    }

    protected boolean playerHasBasicMentionPermission(@NotNull Permissible permissible) {
        return Permissions.anyOfOrAdmin(permissible,
                "strings.*", "strings.discord.*",
                "strings.discord.mention.*", "strings.discord.mention"
        );
    }

    protected boolean playerCannotMentionEveryone(@NotNull Permissible permissible) {
        return !Permissions.anyOfOrAdmin(permissible,
                "strings.*", "strings.discord.*",
                "strings.discord.mention.*", "strings.discord.mention.everyone");
    }

    /**
     * Converts Minecraft Strings with potential Discord pings to Discord ping format
     *
     * @param message The message to convert
     * @param guild The Guild the message is being sent to
     * @param members The Members of the Guild
     * @return The message with any proper mentions converted to Discord format (<@ID> or <@&ID>)
     */
    protected String convertMentionsToDiscordFormat(@NotNull String message, @NotNull Guild guild, @NotNull Set<Member> members) {
        if(!message.contains("@")) {
            return message; // Avoid unnecessary processing
        }

        if(!mentionsFromGameEnabled) {
            return message;
        }

        String[] sequences = message.split("@");
        for(String sequence : sequences) {
            String[] combinations = getPossibleCombinations(sequence);
            for(String combination : combinations) { // As names can have spaces, iterate through all possible combos
                for(Member member : members) { // Iterate through cached members, as Discord provides a limited amt.
                    if(member.getEffectiveName().equalsIgnoreCase(combination)) {
                        message = message.replace("@" + combination, member.getAsMention());
                        break;
                    }

                    if(member.getUser().getName().equalsIgnoreCase(combination)) {
                        message = message.replace("@" + combination, member.getAsMention());
                        break;
                    }
                }

                List<Role> roles = guild.getRolesByName(combination, true);
                if(!roles.isEmpty()) {
                    String asMention = roles.getFirst().getAsMention();
                    message = message.replace("@" + combination, asMention);
                    break;
                }
            }
        }

        return message;
    }

    // Splits the sequence by spaces, and creates a list of possible, sequential combinations, without skips.
    // Ex. "Hi There Guy" -> {"Hi", "Hi There", "Hi There Guy"}
    @Contract(pure = true)
    @NotNull
    private String[] getPossibleCombinations(@NotNull String sequence) {
        String[] combinations = sequence.split(" ");
        if (combinations.length < 2) {
            return combinations;
        }

        for (int i = 1; i < combinations.length; i++) {
            combinations[i] = combinations[i - 1] + " " + combinations[i];
        }

        return combinations;
    }

    protected void handleMentionsFromDiscord(@NotNull String string, @NotNull Member sender) {
        if(!mentionsToGameEnabled) {
            return;
        }

        try {
            StringsAPI api = StringsProvider.get();
            for(Player player : Bukkit.getOnlinePlayers()) {
                if(string.contains("@" + player.getName())) {
                    String mention = discordToGameMentionFormat.
                            replace("{username}", sender.getUser().getName())
                            .replace("{nickname}", sender.getEffectiveName());
                    api.sendMention(player, mention);
                }
            }
        } catch(Exception ignored) {}
    }

    /**
     * Sanitizes Discord mentions from a String, replacing them with what they would appear as in the Discord client.
     * @param string The original String
     * @param members Members of the Guild where the message was sent
     * @return A sanitized String
     */
    protected @NotNull String sanitizeMentions(@NotNull String string, @NotNull Set<Member> members) {
        if(!string.contains("@") || !string.contains("<") || !string.contains(">")) {
            return string;
        }

        JDA jda = strings.getJda();

        Matcher roleMatcher = DISCORD_ROLE_MENTION.matcher(string);
        StringBuilder roleBuffer = new StringBuilder();
        while(roleMatcher.find()) {
            Role role = jda.getRoleById(roleMatcher.group(1));
            if(role != null) {
                roleMatcher.appendReplacement(roleBuffer, Matcher.quoteReplacement("@" + role.getName().trim()));
            } else {
                roleMatcher.appendReplacement(roleBuffer, Matcher.quoteReplacement(roleMatcher.group()));
            }
        }
        roleMatcher.appendTail(roleBuffer);

        Matcher userMatcher = DISCORD_USER_MENTION.matcher(roleBuffer.toString());
        StringBuilder userBuffer = new StringBuilder();
        while(userMatcher.find()) {
            Member member = findMemberById(members, userMatcher.group(1));
            if(member != null) {
                userMatcher.appendReplacement(userBuffer, Matcher.quoteReplacement("@" + member.getEffectiveName().trim()));
            } else {
                userMatcher.appendReplacement(userBuffer, Matcher.quoteReplacement(userMatcher.group()));
            }
        }
        userMatcher.appendTail(userBuffer);

        return userBuffer.toString();
    }

    @Override
    public void deleteMessage(@NotNull SignedMessage signedMessage) {
        if (!shouldSyncDeletions) {
            return;
        }

        strings.getLogger().info("Deleting message: " + signedMessage);

        buffer.forEach(entry -> {
            if (entry.getKey().equals(signedMessage)) {
                strings.getLogger().info("Deleting message: " + entry.getValue());
                try {
                    entry.getValue().delete().queue(
                            success -> {},
                            failure -> {
                                strings.getLogger().warning("Failed to delete message.");
                                strings.getLogger().warning(failure.getMessage());
                            }
                    );
                } catch(Exception ignored) {}
            }
        });
    }

    public void registerMessageAndSignature(@NotNull SignedMessage signedMessage, @NotNull Message discordMessage) {
        buffer.add(Map.entry(signedMessage, discordMessage));
        strings.getLogger().info("Registered Discord Message: " + signedMessage);
    }

    private @Nullable Member findMemberById(@NotNull Set<Member> members, @NotNull String id) {
        for(Member member : members) {
            if(member.getId().equalsIgnoreCase(id)) {
                return member;
            }
        }

        return null;
    }

}
