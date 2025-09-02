package com.pedestriamc.strings.api.channel.data;

import com.pedestriamc.strings.api.StringsPlatform;
import com.pedestriamc.strings.api.channel.Channel;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.BiFunction;

import static org.jetbrains.annotations.ApiStatus.Internal;

@Internal
@SuppressWarnings("unchecked")
final class BuilderRegistry {

    private static StringsPlatform platform;

    private static final Map<IChannelBuilder.Identifier, BiFunction<StringsPlatform, ChannelBuilder, Channel>> BUILD_FUNCTIONS = new EnumMap<>(IChannelBuilder.Identifier.class);

    private static final Map<IChannelBuilder.Identifier, BiFunction<StringsPlatform, LocalChannelBuilder<?>, Channel>> LOCAL_BUILD_FUNCTIONS = new EnumMap<>(IChannelBuilder.Identifier.class);

    private BuilderRegistry() {

    }

    public static <B> Channel build(@NotNull IChannelBuilder<?> builder, @NotNull IChannelBuilder.Identifier identifier) throws IllegalArgumentException {
        if(AbstractChannelBuilder.isLocal(identifier)) {
            if (!(builder instanceof LocalChannelBuilder)) {
                throw new IllegalArgumentException("Local channels must be built with LocalChannelBuilder");
            } else {
                try {
                    return LOCAL_BUILD_FUNCTIONS.get(identifier).apply(platform, (LocalChannelBuilder<B>) builder);
                } catch(Exception e) {
                    throw new IllegalArgumentException(e);
                }
            }
        }

        if(!BUILD_FUNCTIONS.containsKey(identifier)) {
            throw new IllegalArgumentException("Unknown channel type: " + identifier);
        }

        try {
            return BUILD_FUNCTIONS.get(identifier).apply(platform, (ChannelBuilder) builder);
        } catch(Exception e) {
            throw new IllegalArgumentException(e);
        }
    }


    static void registerBuildable(@NotNull IChannelBuilder.Identifier identifier, @NotNull BiFunction<StringsPlatform, ChannelBuilder, Channel> function) {
        BUILD_FUNCTIONS.put(identifier, function);
    }

    static void registerLocalBuildable(@NotNull IChannelBuilder.Identifier identifier, @NotNull BiFunction<StringsPlatform, LocalChannelBuilder<?>, Channel> function) {
        LOCAL_BUILD_FUNCTIONS.put(identifier, function);
    }

    static void registerPlatform(@NotNull StringsPlatform platform) {
        BuilderRegistry.platform = platform;
    }

}
