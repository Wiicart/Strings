package com.pedestriamc.strings.common.chat.messages.dispatch;

import com.pedestriamc.strings.api.StringsPlatform;
import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.user.StringsUser;
import com.pedestriamc.strings.common.chat.messages.StandardAdventureRenderer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class AdventureMessageDispatcher extends AbstractMessageDispatcher {

    private final StandardAdventureRenderer renderer;

    public AdventureMessageDispatcher(@NotNull StringsPlatform strings, @NotNull Channel channel) {
        super(strings, channel);
        renderer = new StandardAdventureRenderer(strings);
    }

    @Override
    void dispatchMessageToPlayers(@NotNull StringsUser user, @NotNull String rawMessage, @NotNull Set<StringsUser> recipients) {
        String stringFormat = renderer.generateStringTemplate(user, channel());
        Component base = MiniMessage.miniMessage().deserialize(stringFormat);
        base = renderer.finalizeTemplate(user, channel(), base);

        Component message = renderer.processMessage(user, channel(), rawMessage);

        Component result = renderer.insertMessage(base, message);

        for (StringsUser recipient : recipients) {
            recipient.sendMessage(result);
        }
    }

}
