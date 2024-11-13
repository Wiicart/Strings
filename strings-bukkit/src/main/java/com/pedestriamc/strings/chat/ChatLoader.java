package com.pedestriamc.strings.chat;

import com.pedestriamc.strings.api.channels.data.ChannelData;
import com.pedestriamc.strings.api.channels.ChannelLoader;
import com.pedestriamc.strings.api.channels.StringsChannel;
import com.pedestriamc.strings.api.channels.Type;
import com.pedestriamc.strings.api.channels.data.ProximityChannelData;
import com.pedestriamc.strings.api.channels.data.WorldChannelData;
import org.jetbrains.annotations.Nullable;

public class ChatLoader implements ChannelLoader {

    @Override
    public void registerChannel(StringsChannel channel) {

    }

    @Override
    public void saveChannel(StringsChannel channel) {

    }

    @Override
    public @Nullable StringsChannel build(ChannelData data, Type type) throws Exception {
        switch(type){

            case NORMAL -> {

            }

            case WORLD -> {
                if(!(data instanceof WorldChannelData)){
                    throw new Exception("Failed to create Channel, cannot use ChannelData class. Use class WorldChannelData instead.");
                }

            }

            case PROXIMITY -> {
                if(!(data instanceof ProximityChannelData)){
                    throw new Exception("Failed to create Channel, cannot use ChannelData class. Use class ProximityChannelData instead.");
                }

            }

        }
        return null;
    }

    @Override
    public @Nullable StringsChannel getChannel(String name) {
        return null;
    }


}
