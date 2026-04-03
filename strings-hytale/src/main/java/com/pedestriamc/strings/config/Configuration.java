package com.pedestriamc.strings.config;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.settings.Option;
import com.pedestriamc.strings.api.settings.Settings;
import com.pedestriamc.strings.api.settings.SettingsRegistry;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Settings implementation for Strings on Hytale. <br/>
 * <a href="https://hytalemodding.dev/en/docs/guides/plugin/creating-configuration-file">Hytale Modding docs</a>
 */
public class Configuration implements Settings {

    public static final BuilderCodec<Configuration> CODEC = BuilderCodec.builder(Configuration.class, Configuration::new)
            .append(new KeyedCodec<>("UsePlaceholderAPI", Codec.BOOLEAN),
                    (config, value) -> config.usePlaceholderAPI = value, // Setter
                    config -> config.usePlaceholderAPI).add() // Getter


            .build();

    private boolean usePlaceholderAPI = true;

    Configuration() {

    }


    SettingsRegistry build() {
        return new SettingsRegistry(builder -> {
            builder.putAll(Map.of(
                    Option.Bool.USE_PAPI, usePlaceholderAPI
            ));


        });
    }

    @Override
    public <E extends Enum<E> & Option.CoreKey<V>, V> @NotNull V get(@NotNull E key) {
        return null;
    }

    @Override
    public Component getComponent(Option.@NotNull Text option) {
        throw new UnsupportedOperationException("Components not supported in Hytale.");
    }

}
