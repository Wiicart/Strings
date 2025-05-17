package com.pedestriamc.strings.api.exception;

import com.pedestriamc.strings.api.channel.Channel;

/**
 * An UnsupportedOperationException with the additional field of a Channel.
 */
@SuppressWarnings("unused")
public class ChannelUnsupportedOperationException extends UnsupportedOperationException {

    private final transient Channel channel;

    public ChannelUnsupportedOperationException(String message) {
        super(message);
        channel = null;
    }

    public ChannelUnsupportedOperationException(String message, Throwable cause) {
        super(message, cause);
        channel = null;
    }

    public ChannelUnsupportedOperationException(String message, Channel channel) {
        super(message);
        this.channel = channel;
    }

    public ChannelUnsupportedOperationException(String message, Throwable cause, Channel channel) {
        super(message, cause);
        this.channel = channel;
    }

    /**
     * Provides the Channel that threw the Exception.
     * @return The Channel.
     */
    public Channel getChannel() {
        return channel;
    }
}
