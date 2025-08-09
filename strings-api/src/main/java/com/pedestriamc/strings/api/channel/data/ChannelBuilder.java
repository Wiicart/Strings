package com.pedestriamc.strings.api.channel.data;

import com.pedestriamc.strings.api.annotation.Agnostic;
import com.pedestriamc.strings.api.channel.Membership;
import org.jetbrains.annotations.NotNull;

/**
 * Standard ChannelBuilder implementation.
 * If building a LocalChannel, use {@link LocalChannelBuilder} instead.
 */
@Agnostic
public final class ChannelBuilder extends AbstractChannelBuilder<ChannelBuilder> {

    /**
     * ChannelBuilder constructor with universally required parameters.
     *
     * @param name       The Channel name.
     * @param format     The Channel format.
     * @param membership The Channel membership
     */
    public ChannelBuilder(@NotNull String name, @NotNull String format, @NotNull Membership membership) {
        super(name, format, membership);
    }
}
