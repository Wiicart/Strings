package com.pedestriamc.strings.api.text.format;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;

/**
 * Immutable simple Component wrapper that makes converting to a legacy String easier.
 */
@SuppressWarnings("unused")
public final class StringsComponent implements ComponentLike {

    private final @NotNull Component component;

    public static StringsComponent fromLegacy(@NotNull String string) {
        return new StringsComponent(LegacyComponentSerializer.legacySection().deserialize(string));
    }

    public static StringsComponent of(Element<?>... elements) {
        TextComponent.Builder builder = Component.text();
        for(Element<?> element : elements) {
            if(element instanceof StringsTextColor c) {
                builder.color(c.toAdventure());
            }
            if(element instanceof StringsTextDecoration d) {
                builder.decorate(d.toAdventure());
            }
        }
        return new StringsComponent(builder.build());
    }

    public StringsComponent(@NotNull Component component) {
        this.component = component;
    }

    @Override
    public @NotNull Component asComponent() {
        return component;
    }

    public @NotNull String asLegacyString() {
        return LegacyComponentSerializer.legacySection().serialize(component);
    }

    @Override
    public @NotNull String toString() {
        return asLegacyString();
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        StringsComponent that = (StringsComponent) o;
        return component.equals(that.component);
    }

    @Override
    public int hashCode() {
        return component.hashCode();
    }
}
