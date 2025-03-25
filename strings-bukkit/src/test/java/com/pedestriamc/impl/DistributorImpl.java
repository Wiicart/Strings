/*package com.pedestriamc.impl;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.Distributor;
import com.pedestriamc.strings.api.StringsDistributor;
import com.pedestriamc.strings.api.StringsReceiver;
import com.pedestriamc.strings.channels.Channel;
import org.bukkit.Bukkit;

import java.lang.reflect.Method;
import java.util.HashMap;

public class DistributorImpl implements Distributor {

    private final Strings strings;
    private final HashMap<StringsReceiver, Channel> receiverMap;

    public DistributorImpl(Strings strings) {
        this.strings = strings;
        this.receiverMap = new HashMap<>();
    }


    @Override
    public void registerReceiver(StringsReceiver receiver) {
        Class<?> receiverClass = receiver.getClass();
        if(!receiverClass.isAnnotationPresent(StringsDistributor.class)) {
            Bukkit.getLogger().info("[Strings] Unable to register StringsReceiver " + receiver.getClass().getName() + ".  No annotation present.");
            return;
        }
        for(Method method : receiverClass.getDeclaredMethods()) {
            boolean isAnnotationPresent = method.isAnnotationPresent(StringsDistributor.class);
            boolean isProperlyDefined = false;

            if(isAnnotationPresent && isProperlyDefined) {
                StringsDistributor distributor = method.getAnnotation(StringsDistributor.class);
                String channelName = distributor.channelName();
                Channel channel = strings.getChannel(channelName);
                if(channel == null) {
                    Bukkit.getLogger().info("[Strings] Unable to register StringsReceiver" + receiver.getClass().getName() + ". Invalid channel defined.");
                }else{
                    receiverMap.put(receiver, channel);
                    Bukkit.getLogger().info("[Strings] StringsReceiver " + receiver.getClass().getName() + " registered successfully.");
                }
            }
        }
    }

    @Override
    public void unregisterReceiver(StringsReceiver receiver) {
        receiverMap.remove(receiver);
    }
}
*/