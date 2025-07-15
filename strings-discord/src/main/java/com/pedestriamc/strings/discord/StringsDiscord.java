package com.pedestriamc.strings.discord;

import com.pedestriamc.strings.api.StringsAPI;
import com.pedestriamc.strings.api.StringsProvider;
import com.pedestriamc.strings.discord.command.DiscordCommand;
import com.pedestriamc.strings.discord.configuration.Option;
import com.pedestriamc.strings.discord.configuration.Settings;
import com.pedestriamc.strings.discord.listener.bukkit.CraftChatListener;
import com.pedestriamc.strings.discord.listener.bukkit.PlayerAdvancementListener;
import com.pedestriamc.strings.discord.listener.bukkit.PlayerDeathListener;
import com.pedestriamc.strings.discord.listener.bukkit.PlayerJoinQuitListener;
import com.pedestriamc.strings.discord.listener.bukkit.ServerLoadListener;
import com.pedestriamc.strings.discord.listener.bukkit.StringsReloadListener;
import com.pedestriamc.strings.discord.listener.discord.MessageListener;
import com.pedestriamc.strings.discord.listener.discord.StatusListener;
import com.pedestriamc.strings.discord.manager.ChannelDiscordManager;
import com.pedestriamc.strings.discord.manager.DiscordManager;
import com.pedestriamc.strings.discord.manager.GlobalDiscordManager;
import com.pedestriamc.strings.discord.manager.QueuedDiscordManager;
import com.pedestriamc.strings.discord.misc.ActivityType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.exceptions.InvalidTokenException;
import net.dv8tion.jda.api.requests.GatewayIntent;
import okhttp3.OkHttpClient;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public final class StringsDiscord extends JavaPlugin {

    private JDA jda;

    private DiscordManager manager;

    private Settings settings;

    public StringsDiscord() {
        try {
            System.setProperty("org.slf4j.simpleLogger.log.net.dv8tion.jda", "off");
        } catch(Exception ignored) {}
    }

    @Override
    public void onLoad() {
        saveDefaultConfig();
        settings = new Settings(this);
        initJda();
        getLogger().info("Connecting to Bot...");
    }

    @Override
    public void onEnable() {
        checkForStrings();
        if(jda == null) {
            getLogger().severe("Failed to connect to Bot, disabling...");
            getServer().getPluginManager().disablePlugin(this);
        }

        if(jda.getStatus() == JDA.Status.CONNECTED) {
            manager = createProperManager();
            registerDiscordListener(new StatusListener(this));
            getLogger().info("Enabled!");
        } else {
            getLogger().info("Bot not ready, queueing final plugin enabling.");
            QueuedDiscordManager queuedManager = new QueuedDiscordManager();
            jda.addEventListener(new StatusListener(this, queuedManager));
            manager = queuedManager;
        }

        registerListeners();
        registerCommands();
    }

    @Override
    public void onDisable() {
        try {
            sendServerDisablingMessage();
            shutdownJda();
        } catch(NullPointerException ignored) {}

        jda = null;
        manager = null;
        settings = null;

        HandlerList.unregisterAll(this);
        getServer().getScheduler().cancelTasks(this);
        getLogger().info("Disabled.");
    }

    public void reload() {
        onDisable();
        onLoad();
        onEnable();
    }

    /**
     * Only for use in StatusManager and in this class.
     * @return A new DiscordManager.
     */
    @Contract(" -> new")
    private @NotNull DiscordManager createProperManager() {
        getLogger().info("DiscordManager initializing...");
        if(settings.getBoolean(Option.Bool.GLOBAL)) {
            getLogger().info("DiscordManager instance created.");
            return new GlobalDiscordManager(this);
        } else {
            getLogger().info("DiscordManager instance created.");
            return new ChannelDiscordManager(this);
        }
    }

    // Ensure Strings is found, disable otherwise.
    private void checkForStrings() {
        try {
            StringsAPI api = StringsProvider.get();
            if (api.getVersion() < 6) {
                getLogger().warning("Unsupported Strings version detected, disabling...");
                throw new RuntimeException(api.getVersion() + " not supported");
            }
        } catch(Exception e) {
            getLogger().warning("Disabling...");
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    // Connect to the Discord Bot profile
    private void initJda() {
        String token = settings.getString(Option.Text.TOKEN);
        try {
            JDABuilder builder = JDABuilder.createDefault(token)
                    .enableIntents(
                            GatewayIntent.GUILD_MESSAGES,
                            GatewayIntent.GUILD_MEMBERS,
                            GatewayIntent.MESSAGE_CONTENT,
                            GatewayIntent.GUILD_MEMBERS
                    );

            String activity = settings.getString(Option.Text.ACTIVITY);
            String activityItem = settings.getString(Option.Text.ACTIVITY_ITEM);
            builder.setActivity(ActivityType.of(activity).apply(activityItem));
            builder.setStatus(getOnlineStatus());

            jda = builder.build();
        } catch(final IllegalArgumentException | InvalidTokenException e) {
            getLogger().warning("Failed to log into Discord bot, disabling...");
            getLogger().warning(e.getMessage());
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    // Reads status from the config
    private OnlineStatus getOnlineStatus() {
        String status = settings.getString(Option.Text.DISCORD_STATUS);
        return switch(status.toLowerCase(Locale.ROOT)) {
            case "offline" -> OnlineStatus.OFFLINE;
            case "idle" -> OnlineStatus.IDLE;
            case "invisible" ->OnlineStatus.INVISIBLE;
            default -> OnlineStatus.ONLINE;
        };
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void shutdownJda() {
        OkHttpClient client = jda.getHttpClient();
        try (ExecutorService ex = client.dispatcher().executorService()) {
            client.connectionPool().evictAll();
            ex.shutdown();
            jda.shutdown();
            jda.awaitShutdown(5, TimeUnit.SECONDS);
        } catch(Exception ignored) {}
    }

    private void sendServerDisablingMessage() {
        String message = settings.getString(Option.Text.SERVER_OFFLINE_MESSAGE);
        if(manager != null) {
            manager.broadcastDiscordMessage(message);
        }
    }

    private void registerCommands() {
        if (!settings.getString(Option.Text.DISCORD_COMMAND_MESSAGE).isEmpty()) {
            PluginCommand command = getCommand("discord");
            if (command != null) {
                command.setExecutor(new DiscordCommand(this));
            }
        }
    }

    private void registerListeners() {
        registerDiscordListener(new MessageListener(this));

        registerBukkitListener(new ServerLoadListener(this));
        registerBukkitListener(new CraftChatListener(this));
        registerBukkitListener(new StringsReloadListener(this));

        if (settings.getBoolean(Option.Bool.ENABLE_JOIN_LEAVE_MESSAGES)) {
            registerBukkitListener(new PlayerJoinQuitListener(this));
        }

        if (settings.getBoolean(Option.Bool.ENABLE_DEATH_MESSAGES)) {
            registerBukkitListener(new PlayerDeathListener(this));
        }

        if (settings.getBoolean(Option.Bool.ENABLE_ADVANCEMENT_MESSAGES)) {
            registerBukkitListener(new PlayerAdvancementListener(this));
        }
    }

    private void registerBukkitListener(@NotNull Listener listener) {
        getServer().getPluginManager().registerEvents(listener, this);
    }

    private void registerDiscordListener(@NotNull Object listener) {
        jda.addEventListener(listener);
    }

    public @NotNull JDA getJda() {
        return jda;
    }

    public @NotNull Settings getSettings() {
        return settings;
    }

    public @NotNull  DiscordManager getManager() {
        return manager;
    }

    @SuppressWarnings("unused")
    public void async(@NotNull Runnable runnable) {
        getServer().getScheduler().runTaskAsynchronously(this, runnable);
    }

    // Runs a task on the Server's main Thread
    public void sync(@NotNull Runnable runnable) {
        getServer().getScheduler().runTask(this, runnable);
    }

}
