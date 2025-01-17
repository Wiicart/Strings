package com.pedestriamc.strings.commands;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.channels.Channel;
import com.pedestriamc.strings.chat.StringsChannelLoader;
import com.pedestriamc.strings.message.Messenger;
import com.pedestriamc.strings.user.User;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import static com.pedestriamc.strings.message.Message.*;



public final class ChannelCommand extends CommandBase {


    public ChannelCommand(Strings strings){
        super();
        HashMap<String, CommandComponent> map = new HashMap<>();
        JoinCommand joinCommand = new JoinCommand(strings);
        map.put("JOIN", joinCommand);
        map.put("J", joinCommand);
        LeaveCommand leaveCommand = new LeaveCommand(strings);
        map.put("LEAVE", leaveCommand);
        map.put("L", leaveCommand);
        HelpCommand helpCommand = new HelpCommand(strings);
        map.put("HELP", helpCommand);
        map.put("H", helpCommand);
        BaseCommand baseCommand = new BaseCommand(strings);
        initialize(map, baseCommand);
    }

    /**
     * Base command for setting active channel
     */
    private static class BaseCommand extends ChannelProcessor {

        private final Strings strings;
        private final Messenger messenger;

        public BaseCommand(Strings strings){
            super(strings);
            this.strings = strings;
            this.messenger = strings.getMessenger();
        }

        @Override
        public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

            String[] newArgs;

            switch(args.length) {
                case 0 -> {
                    messenger.sendMessage(INSUFFICIENT_ARGS, sender);
                    return true;
                }
                case 1 -> {
                    String channelName = args[0];
                    newArgs = new String[2];
                    newArgs[0] = " ";
                    newArgs[1] = channelName;
                }
                case 2 -> {
                    String channelName = args[0];
                    String targetName = args[1];
                    newArgs = new String[3];
                    newArgs[0] = " ";
                    newArgs[1] = channelName;
                    newArgs[2] = targetName;
                }
                default -> {
                    messenger.sendMessage(TOO_MANY_ARGS, sender);
                    return true;
                }
            }

            BaseData data = process(sender, newArgs, Type.BASE);

            if(data.shouldReturn){
                return true;
            }

            boolean modifyingOther = !sender.equals(data.target);
            User user = strings.getUser(data.target);
            Channel channel = data.channel;

            if(!user.memberOf(channel)) {
                user.joinChannel(channel);
            }

            if(user.getActiveChannel().equals(channel)){
                if(modifyingOther){
                    messenger.sendMessage(ALREADY_ACTIVE_OTHER, data.placeholders, sender);
                }else{
                    messenger.sendMessage(ALREADY_ACTIVE, data.placeholders, sender);
                }
                return true;
            }

            user.setActiveChannel(channel);

            if(modifyingOther) {
                messenger.sendMessage(OTHER_PLAYER_CHANNEL_ACTIVE, data.placeholders, sender);
            }
            messenger.sendMessage(CHANNEL_ACTIVE, data.placeholders, data.target);

            return true;
        }

    }

    /**
     * Channel join sub command
     */
    private static class JoinCommand extends ChannelProcessor {

        private final Strings strings;
        private final Messenger messenger;

        public JoinCommand(Strings strings) {
            super(strings);
            this.strings = strings;
            this.messenger = strings.getMessenger();
        }

        @Override
        public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

            BaseData data = process(sender, args, Type.JOIN);

            if(data.shouldReturn) {
                return true;
            }

            boolean modifyingOther = !sender.equals(data.target);
            User user = strings.getUser(data.target);
            Channel channel = data.channel;

            if(user.memberOf(channel)){
                if(modifyingOther){
                    messenger.sendMessage(ALREADY_MEMBER_OTHER, data.placeholders, sender);
                }else{
                    messenger.sendMessage(ALREADY_MEMBER, data.placeholders, sender);
                }
                return true;
            }

            user.joinChannel(channel);
            if(modifyingOther) {
                messenger.sendMessage(OTHER_USER_JOINED_CHANNEL, data.placeholders, sender);
            }
            messenger.sendMessage(CHANNEL_JOINED, data.placeholders, data.target);

            return true;
        }

    }

    /**
     * Channel leave subcommand
     */
    private static class LeaveCommand extends ChannelProcessor {

        private final Strings strings;
        private final Messenger messenger;

        public LeaveCommand(Strings strings) {
            super(strings);
            this.strings = strings;
            this.messenger = strings.getMessenger();
        }

        @Override
        public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

            BaseData data = process(sender, args, Type.LEAVE);

            if(data.shouldReturn) {
                return true;
            }

            User user = strings.getUser(data.target);
            Channel channel = data.channel;
            boolean modifyingOther = !sender.equals(data.target);

            if(!user.getChannels().contains(channel)) {
                if(modifyingOther){
                    messenger.sendMessage(NOT_CHANNEL_MEMBER_OTHER, data.placeholders, sender);
                }else{
                    messenger.sendMessage(NOT_CHANNEL_MEMBER, data.placeholders, data.target);
                }
                return true;
            }

            if(channel.getName().equals("global") || channel.getName().equals("default")){
                messenger.sendMessage(CANT_LEAVE_GLOBAL, sender);
                return true;
            }

            user.leaveChannel(channel);
            if(modifyingOther) {
                messenger.sendMessage(OTHER_USER_LEFT_CHANNEL, data.placeholders, sender);
            }
            messenger.sendMessage(LEFT_CHANNEL, data.placeholders, data.target);

            return true;
        }
    }

    private static class HelpCommand implements CommandComponent {

        private final Messenger messenger;

        public HelpCommand(Strings strings){
            this.messenger = strings.getMessenger();
        }

        @Override
        public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
            messenger.sendMessage(CHANNEL_HELP, sender);
            return true;
        }
    }

    /**
     * Class to handle shared sub command processes
     */
    private static abstract class ChannelProcessor implements CommandComponent {

        private final StringsChannelLoader channelLoader;
        private final Messenger messenger;

        protected ChannelProcessor(@NotNull Strings strings) {
            this.channelLoader = (StringsChannelLoader) strings.getChannelLoader();
            this.messenger = strings.getMessenger();
        }

        protected BaseData process(@NotNull CommandSender sender, @NotNull String @NotNull [] args, Type type) {

            Player target;
            boolean modifyingOther = false;
            boolean isPlayer = sender instanceof Player;

            switch(args.length) {
                case 1 -> {
                    messenger.sendMessage(INSUFFICIENT_ARGS, sender);
                    return new BaseData(null,null, null, true);
                }
                case 2 -> {
                    if(!isPlayer) {
                        sender.sendMessage("[Strings] You must define a player to use this command on.");
                        return new BaseData(null,null, null, true);
                    } else {
                        target = (Player) sender;
                    }
                }
                case 3 -> {
                    target = Bukkit.getPlayer(args[2]);
                    modifyingOther = true;
                    if(target == null){
                        HashMap<String, String> map = new HashMap<>();
                        map.put("{player}", args[2]);
                        messenger.sendMessage(INVALID_PLAYER, map, sender);
                        return new BaseData(null,null, null, true);
                    }
                }
                default -> {
                    messenger.sendMessage(TOO_MANY_ARGS, sender);
                    return new BaseData(null,null, null, true);
                }
            }

            String channelName = args[1];

            if(modifyingOther && !isPlayer && forbids(sender, "strings.channels.modifyplayers")) {
                messenger.sendMessage(NO_PERMS, sender);
                return new BaseData(null,null, null, true);
            }

            if(channelName.equalsIgnoreCase("helpop")) {
                messenger.sendMessage(HELPOP_NOT_CHANNEL, sender);
                return new BaseData(null,null, null, true);
            }

            Channel channel = channelLoader.getChannel(channelName);
            if(channel == null) {
                HashMap<String, String> placeholders = new HashMap<>();
                placeholders.put("{channel}", channelName);
                messenger.sendMessage(CHANNEL_DOES_NOT_EXIST, placeholders,  sender);
                return new BaseData(null,null, null, true);
            }

            if(channelLoader.getProtectedChannels().contains(channel)) {
                messenger.sendMessage(PROTECTED_CHANNEL, sender);
                return new BaseData(null,null, null, true);
            }

            if(type == Type.JOIN || type == Type.BASE){

                if (
                        forbids(sender, "strings.channels." + channel.getName()) &&
                        forbids(sender, "strings.channels.*") &&
                        forbids(sender, "strings.*") &&
                        !channel.getMembers().contains(target)
                ) {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("{channel}", channelName);
                    messenger.sendMessage(NO_PERMS_CHANNEL, map, sender);
                    return new BaseData(null, null, null, true);
                }

            }

            HashMap<String, String> placeholders = new HashMap<>();
            placeholders.put("{channel}", channelName);
            placeholders.put("{player}", target.getName());

            return new BaseData(channel, target, placeholders, false);
        }

        protected enum Type {
            JOIN,
            LEAVE,
            BASE
        }

        protected boolean forbids(CommandSender sender, String perm) {
            return !sender.hasPermission(perm);
        }

        protected record BaseData(Channel channel, Player target, Map<String, String> placeholders, boolean shouldReturn){}

    }

}
