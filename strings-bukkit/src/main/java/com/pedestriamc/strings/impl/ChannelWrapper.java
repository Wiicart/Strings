package com.pedestriamc.strings.impl;

import com.pedestriamc.strings.api.StringsChannel;
import com.pedestriamc.strings.channels.Channel;

public class ChannelWrapper implements StringsChannel {

    private final Channel channel;

    public ChannelWrapper(Channel channel){
        this.channel = channel;
    }

    public Channel getChannel(){
        return this.channel;
    }
}
