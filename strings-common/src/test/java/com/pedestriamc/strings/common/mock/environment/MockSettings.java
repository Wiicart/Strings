package com.pedestriamc.strings.common.mock.environment;

import com.pedestriamc.strings.api.settings.Option;
import com.pedestriamc.strings.api.settings.Settings;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public class MockSettings implements Settings {

    public static final MockSettings SETTINGS = new MockSettings();

    private MockSettings() {

    }

    @Override
    public <E extends Enum<E> & Option.CoreKey<V>, V> @NotNull V get(@NotNull E key) {
        return key.defaultValue();
    }

    @Override
    public Component getComponent(Option.@NotNull Text option) {
        return Component.text(option.defaultValue());
    }
}
