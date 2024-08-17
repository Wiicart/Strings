package com.pedestriamc.strings.api;

import com.pedestriamc.strings.api.event.StringsMessage;

public class ReceiverTest implements StringsReceiver{

    @StringsDistributor(channelName = "global")
    public void receiveChannelMessage(StringsMessage message){
        message.getMessage();
        message.getChannel();
        message.getRecipients();
        message.getSender();
        message.getRecipients();
        message.getFinalMessage();

    }

}
