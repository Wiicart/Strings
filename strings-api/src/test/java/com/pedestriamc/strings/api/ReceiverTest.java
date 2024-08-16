package com.pedestriamc.strings.api;

public class ReceiverTest implements StringsReceiver{

    @StringsDistributor(channelName = "global")
    public void receiveChannelMessage(StringsMessage message){
        message.getMessage();
        message.getChannel();
        message.getRecipients();
        message.getSender();
        message.getRecipients();

    }

}
