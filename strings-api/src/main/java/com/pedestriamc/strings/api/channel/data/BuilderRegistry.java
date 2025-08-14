package com.pedestriamc.strings.api.channel.data;

import com.pedestriamc.strings.api.StringsPlatform;
import com.pedestriamc.strings.api.channel.Channel;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import static org.jetbrains.annotations.ApiStatus.Internal;

@Internal
@SuppressWarnings("unchecked")
final class BuilderRegistry {

    private static StringsPlatform platform;

    private static final Map<String, BiFunction<StringsPlatform, ChannelBuilder, Channel>> BUILD_FUNCTIONS = new HashMap<>();

    private static final Map<String, BiFunction<StringsPlatform, LocalChannelBuilder<?>, Channel>> LOCAL_BUILD_FUNCTIONS = new HashMap<>();

    private BuilderRegistry() {

    }

    public static <B> Channel build(@NotNull IChannelBuilder<?> builder, @NotNull String type) throws IllegalArgumentException {
        if(AbstractChannelBuilder.isLocal(type)) {
            if (!(builder instanceof LocalChannelBuilder)) {
                throw new IllegalArgumentException("Local channels must be built with LocalChannelBuilder");
            } else {
                try {
                    return LOCAL_BUILD_FUNCTIONS.get(type).apply(platform, (LocalChannelBuilder<B>) builder);
                } catch(Exception e) {
                    throw new IllegalArgumentException(e);
                }
            }
        }

        if(!BUILD_FUNCTIONS.containsKey(type)) {
            throw new IllegalArgumentException("Unknown channel type: " + type);
        }

        try {
            return BUILD_FUNCTIONS.get(type).apply(platform, (ChannelBuilder) builder);
        } catch(Exception e) {
            throw new IllegalArgumentException(e);
        }
    }


    static void registerBuildable(@NotNull String identifier, @NotNull BiFunction<StringsPlatform, ChannelBuilder, Channel> function) {
        BUILD_FUNCTIONS.put(identifier, function);
    }

    static void registerLocalBuildable(@NotNull String identifier, @NotNull BiFunction<StringsPlatform, LocalChannelBuilder<?>, Channel> function) {
        LOCAL_BUILD_FUNCTIONS.put(identifier, function);
    }

    static void registerPlatform(@NotNull StringsPlatform platform) {
        BuilderRegistry.platform = platform;
    }

}
