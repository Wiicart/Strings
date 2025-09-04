package com.pedestriamc.strings.discord.manager.console

import com.pedestriamc.strings.discord.StringsDiscord
import com.pedestriamc.strings.api.discord.Option
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.md_5.bungee.api.ChatColor
import org.bukkit.command.ConsoleCommandSender
import org.jetbrains.annotations.NotNull
import java.lang.IllegalArgumentException

class KConsoleManager(private val strings: StringsDiscord) : ListenerAdapter() {

    private val channel : MessageChannel = resolveChannel();
    private val appender: KLogAppender = KLogAppender(this);
    private val sender : ConsoleCommandSender = strings.server.consoleSender;

    private fun resolveChannel() : MessageChannel {
        val consoleId = strings.configuration[Option.Text.DISCORD_CONSOLE_ID];
        strings.logger.info("Loading Console Channel");

        val gc = strings.jda.getGuildChannelById(consoleId);
        if (gc is MessageChannel) {
            strings.logger.info("Loaded console Channel ${gc.getName()}")
            return gc;
        }

        throw IllegalArgumentException("Failed to load console channel.");
    }

    init {
        appender.start();
        strings.jda.addEventListener(this);
    }

    override fun onMessageReceived(@NotNull event: MessageReceivedEvent) {
        if (!event.author.isBot) {
            executeCommand(event.message.contentRaw);
        }
    }

    fun shutdown() {
        appender.stop();
    }

    internal fun post(@NotNull message: String) {
        try {
            channel.sendMessage(
                sanitizeForDiscord(message)
            ).queue();
        } catch (ignored: Exception) {
            // stfu sonarqube
        }
    }

    private fun sanitizeForDiscord(@NotNull message : String) : String {
        var sanitized = message;
        if (sanitized.startsWith("[ | ")) {
            sanitized = message.replaceFirst("[ | ", "[")
        }

        sanitized = stripColor(sanitized);

        sanitized = sanitized
            .replace("@everyone", "@/everyone")
            .replace("@here", "@/here");

        return sanitized;
    }

    private fun executeCommand(@NotNull command: String) {
        try {
            strings.synchronous {
                strings.server.dispatchCommand(sender, command.trim());
            };
        } catch(e : Exception) {
            strings.logger.warning("Failed to execute command: $command");
            strings.logger.warning(e.message);
        }
    }

    // https://stackoverflow.com/questions/14652538/remove-ascii-color-codes
    private fun stripColor(@NotNull string: String) : String {
        return ChatColor
            .stripColor(string)
            .replace(Regex("\u001B\\[[;\\d]*m"), "");
    }

}