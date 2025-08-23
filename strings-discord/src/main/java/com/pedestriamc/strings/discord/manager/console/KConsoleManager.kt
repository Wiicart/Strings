package com.pedestriamc.strings.discord.manager.console

import com.pedestriamc.strings.discord.StringsDiscord
import com.pedestriamc.strings.discord.configuration.Option
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.bukkit.command.ConsoleCommandSender
import java.lang.IllegalArgumentException

class KConsoleManager(private val strings: StringsDiscord) : ListenerAdapter() {

    private val channel : MessageChannel = resolveChannel();
    private val appender: KLogAppender = KLogAppender(this);
    private val sender : ConsoleCommandSender = strings.server.consoleSender;

    private fun resolveChannel() : MessageChannel {
        val consoleId = strings.settings.getString(Option.Text.DISCORD_CONSOLE_ID);

        val gc = strings.jda.getGuildById(consoleId);
        if (gc is MessageChannel) {
            return gc;
        }

        throw IllegalArgumentException("Failed to load console channel.");
    }

    override fun onMessageReceived(event: MessageReceivedEvent) {
        if (!event.author.isBot) {
            executeCommand(event.message.contentRaw);
        }
    }

    fun shutdown() {
        appender.stop();
    }

    internal fun post(message: String) {
        try {
            channel.sendMessage(message).queue();
        } catch (ignored: Exception) {
            // stfu sonarqube
        }
    }

    private fun executeCommand(command: String) {
        strings.server.dispatchCommand(sender, command);
    }

}