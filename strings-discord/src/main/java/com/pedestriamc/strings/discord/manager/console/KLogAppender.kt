package com.pedestriamc.strings.discord.manager.console

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.core.LogEvent
import org.apache.logging.log4j.core.appender.AbstractAppender
import org.apache.logging.log4j.core.config.Property
import org.apache.logging.log4j.core.Logger

/* https://www.spigotmc.org/threads/capturing-console-output.307132/ */
class KLogAppender(private val m: KConsoleManager) : AbstractAppender(
    "Strings",
    null,
    null,
    false,
    Property.EMPTY_ARRAY
) {

    init {
        // org.apache.logging.log4j.core.Logger
        (LogManager.getRootLogger() as Logger).addAppender(this)
        start();
    }

    override fun append(event: LogEvent?) {
        val immut = event?.toImmutable() ?: return
        m.post("${genPrefix(event)} ${immut.message.formattedMessage}")
    }

    private fun genPrefix(immutableEvent: LogEvent) : String {
        val loggerName = immutableEvent.loggerName;
        return when (loggerName.isNotBlank()) {
            true -> "[${loggerName} | ${immutableEvent.level}]"
            false -> "[${immutableEvent.level}]"
        }
    }


}