package com.pedestriamc.strings.managers;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.message.Message;
import com.pedestriamc.strings.commands.BroadcastCommand;
import com.pedestriamc.strings.commands.ChatColorCommand;
import com.pedestriamc.strings.commands.ClearChatCommand;
import com.pedestriamc.strings.commands.message.DirectMessageCommand;
import com.pedestriamc.strings.commands.DisabledCommand;
import com.pedestriamc.strings.commands.HelpOPCommand;
import com.pedestriamc.strings.commands.MentionCommand;
import com.pedestriamc.strings.commands.message.ReplyCommand;
import com.pedestriamc.strings.commands.SocialSpyCommand;
import com.pedestriamc.strings.commands.StringsCommand;
import com.pedestriamc.strings.commands.channel.ChannelCommand;
import com.pedestriamc.strings.configuration.Configuration;
import com.pedestriamc.strings.listeners.ChatListener;
import com.pedestriamc.strings.listeners.DirectMessageListener;
import com.pedestriamc.strings.listeners.JoinListener;
import com.pedestriamc.strings.listeners.LeaveListener;
import com.pedestriamc.strings.listeners.MentionListener;
import com.pedestriamc.strings.tabcompleters.ChannelTabCompleter;
import com.pedestriamc.strings.tabcompleters.ChatColorTabCompleter;
import com.pedestriamc.strings.tabcompleters.ClearChatTabCompleter;
import com.pedestriamc.strings.tabcompleters.MentionCommandTabCompleter;
import com.pedestriamc.strings.tabcompleters.MessageTabCompleter;
import com.pedestriamc.strings.tabcompleters.SocialSpyTabCompleter;
import com.pedestriamc.strings.tabcompleters.StringsTabCompleter;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.event.Listener;

import static com.pedestriamc.strings.configuration.ConfigurationOption.*;

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
        Configuration config = strings.getConfigClass();

        registerCommand("strings", new StringsCommand(strings), new StringsTabCompleter());

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

        if(config.getBoolean(MSG_ENABLED)) {
            DirectMessageCommand directMessageCommand = new DirectMessageCommand(strings);
            MessageTabCompleter messageTabCompleter = new MessageTabCompleter();
            registerCommand("msg", directMessageCommand, messageTabCompleter);
            registerCommand("message", directMessageCommand, messageTabCompleter);

            ReplyCommand replyCommand = new ReplyCommand(strings);
            registerCommand("reply", replyCommand, null);
            registerCommand("r", replyCommand, null);
        }

        if(config.getBoolean(ENABLE_CHATCOLOR_COMMAND)) {
            registerCommand("chatcolor", new ChatColorCommand(strings), new ChatColorTabCompleter());
        }

        if(config.getBoolean(ENABLE_HELPOP)) {
            registerCommand("helpop", new HelpOPCommand(strings), null);
        } else {
            if(!config.getBoolean(DISABLE_HELPOP_COMMAND)) {
                registerCommand("helpop", new DisabledCommand(strings, Message.HELPOP_DISABLED), null);
            }

            try {
                Channel helpOpChannel = strings.getChannelLoader().getChannel("helpop");
                if(helpOpChannel != null) {
                    strings.getChannelLoader().unregisterChannel(helpOpChannel);
                }
            } catch(Exception ignored) {}
        }
    }

    private void registerCommand(String commandName, CommandExecutor executor, TabCompleter tabCompleter) {
        var command = strings.getCommand(commandName);
        if(command == null) {
            return;
        }
        command.setExecutor(executor);
        if(tabCompleter != null) {
            command.setTabCompleter(tabCompleter);
        }
    }


    private void registerListeners() {
        registerListener(new ChatListener(strings));
        registerListener(new JoinListener(strings));
        registerListener(new LeaveListener(strings));
        registerListener(new DirectMessageListener(strings));
        if(strings.getConfig().getBoolean("enable-mentions")) {
            registerListener(new MentionListener(strings));
        }
    }

    private void registerListener(Listener listener) {
        strings.getServer().getPluginManager().registerEvents(listener, strings);
    }

}
