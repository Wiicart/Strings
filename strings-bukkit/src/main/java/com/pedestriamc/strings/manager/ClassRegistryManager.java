package com.pedestriamc.strings.manager;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.message.Message;
import com.pedestriamc.strings.api.settings.Option;
import com.pedestriamc.strings.commands.BroadcastCommand;
import com.pedestriamc.strings.commands.ChatColorCommand;
import com.pedestriamc.strings.commands.ClearChatCommand;
import com.pedestriamc.strings.commands.RulesCommand;
import com.pedestriamc.strings.commands.StringsCommand;
import com.pedestriamc.strings.commands.ignore.IgnoreCommand;
import com.pedestriamc.strings.commands.ignore.UnIgnoreCommand;
import com.pedestriamc.strings.commands.message.DirectMessageCommand;
import com.pedestriamc.strings.commands.MessengerCommand;
import com.pedestriamc.strings.commands.HelpOPCommand;
import com.pedestriamc.strings.commands.MentionCommand;
import com.pedestriamc.strings.commands.message.ReplyCommand;
import com.pedestriamc.strings.commands.SocialSpyCommand;
import com.pedestriamc.strings.commands.channel.ChannelCommand;
import com.pedestriamc.strings.configuration.Configuration;
import com.pedestriamc.strings.listener.chat.PaperChatListener;
import com.pedestriamc.strings.listener.chat.SpigotChatListener;
import com.pedestriamc.strings.listener.DirectMessageListener;
import com.pedestriamc.strings.listener.player.PlayerDamageListener;
import com.pedestriamc.strings.listener.player.PlayerDeathListener;
import com.pedestriamc.strings.listener.player.PlayerJoinListener;
import com.pedestriamc.strings.listener.player.PlayerQuitListener;
import com.pedestriamc.strings.listener.mention.LuckPermsMentionListener;
import com.pedestriamc.strings.listener.mention.MentionListener;
import com.pedestriamc.strings.tabcompleters.ChannelTabCompleter;
import com.pedestriamc.strings.tabcompleters.ChatColorTabCompleter;
import com.pedestriamc.strings.tabcompleters.ClearChatTabCompleter;
import com.pedestriamc.strings.tabcompleters.IgnoreTabCompleter;
import com.pedestriamc.strings.tabcompleters.MentionCommandTabCompleter;
import com.pedestriamc.strings.tabcompleters.MessageTabCompleter;
import com.pedestriamc.strings.tabcompleters.SocialSpyTabCompleter;
import com.pedestriamc.strings.tabcompleters.StringsTabCompleter;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Registers CommandExecutors and Listeners
 */
public class ClassRegistryManager {

    private final Strings strings;

    private ClassRegistryManager(Strings strings) {
        this.strings = strings;
    }

    /**
     * Registers CommandExecutors and Listeners
     * @param strings Strings instance
     */
    public static void register(Strings strings) {
        ClassRegistryManager manager = new ClassRegistryManager(strings);
        manager.registerCommands();
        manager.registerListeners();
    }

    private void registerCommands() {
        Configuration config = strings.getConfiguration();

        registerCommand("strings", new StringsCommand(strings), new StringsTabCompleter());

        registerCommand("ignore", new IgnoreCommand(strings), new IgnoreTabCompleter());
        registerCommand("unignore", new UnIgnoreCommand(strings), new IgnoreTabCompleter());

        BroadcastCommand broadcastCommand = new BroadcastCommand(strings);
        registerCommand("broadcast", broadcastCommand, null);
        registerCommand("announce", broadcastCommand, null);

        ClearChatCommand clearChatCommand = new ClearChatCommand(strings);
        ClearChatTabCompleter clearChatTabCompleter = new ClearChatTabCompleter();
        registerCommand("clearchat", clearChatCommand, clearChatTabCompleter);
        registerCommand("chatclear", clearChatCommand, clearChatTabCompleter);

        registerCommand("socialspy", new SocialSpyCommand(strings), new SocialSpyTabCompleter());

        ChannelCommand channelCommand = new ChannelCommand(strings);
        ChannelTabCompleter channelTabCompleter = new ChannelTabCompleter(strings);
        registerCommand("channel", channelCommand, channelTabCompleter);
        registerCommand("c", channelCommand, channelTabCompleter);

        MentionCommand mentionCommand = new MentionCommand(strings);
        MentionCommandTabCompleter mentionCommandTabCompleter = new MentionCommandTabCompleter();
        registerCommand("mention", mentionCommand, mentionCommandTabCompleter);
        registerCommand("mentions", mentionCommand, mentionCommandTabCompleter);

        if(config.getBoolean(Option.Bool.ENABLE_DIRECT_MESSAGES)) {
            DirectMessageCommand directMessageCommand = new DirectMessageCommand(strings);
            MessageTabCompleter messageTabCompleter = new MessageTabCompleter();
            registerCommand("msg", directMessageCommand, messageTabCompleter);
            registerCommand("message", directMessageCommand, messageTabCompleter);

            ReplyCommand replyCommand = new ReplyCommand(strings);
            registerCommand("reply", replyCommand, null);
            registerCommand("r", replyCommand, null);
        }

        if(config.getBoolean(Option.Bool.ENABLE_CHATCOLOR_COMMAND)) {
            registerCommand("chatcolor", new ChatColorCommand(strings), new ChatColorTabCompleter());
        }

        if(config.getBoolean(Option.Bool.ENABLE_HELPOP)) {
            registerCommand("helpop", new HelpOPCommand(strings), null);
        } else {
            if(!config.getBoolean(Option.Bool.DISABLE_HELPOP_COMMAND)) {
                registerCommand("helpop", new MessengerCommand(strings, Message.HELPOP_DISABLED), null);
            }

            try {
                Channel helpOpChannel = strings.getChannelLoader().getChannel("helpop");
                if(helpOpChannel != null) {
                    strings.getChannelLoader().unregister(helpOpChannel);
                }
            } catch(Exception ignored) {}
        }

        if (config.getBoolean(Option.Bool.ENABLE_RULES_COMMAND)) {
            registerCommand("rules", new RulesCommand(strings), null);
        }
    }

    private void registerCommand(@NotNull String commandName, @NotNull CommandExecutor executor, @Nullable TabCompleter tabCompleter) {
        var command = strings.getCommand(commandName);
        if(command == null) {
            return;
        }
        command.setExecutor(executor);
        if(tabCompleter != null) {
            command.setTabCompleter(tabCompleter);
        }
    }

    /**
     * Registers Listeners for Strings.
     */
    private void registerListeners() {
        if(strings.isPaper()) {
            registerListener(new PaperChatListener(strings));
        } else {
            registerListener(new SpigotChatListener(strings));
        }

        registerListener(new PlayerJoinListener(strings));
        registerListener(new PlayerQuitListener(strings));
        registerListener(new DirectMessageListener(strings));
        registerListener(new PlayerDeathListener(strings));
        registerListener(new PlayerDamageListener(strings));

        if(strings.getConfiguration().getBoolean(Option.Bool.ENABLE_MENTIONS)) {
            if(strings.isUsingLuckPerms()) {
                registerListener(new LuckPermsMentionListener(strings));
            } else {
                registerListener(new MentionListener(strings));
            }
        }
    }

    private void registerListener(Listener listener) {
        strings.getServer().getPluginManager().registerEvents(listener, strings);
    }

}
