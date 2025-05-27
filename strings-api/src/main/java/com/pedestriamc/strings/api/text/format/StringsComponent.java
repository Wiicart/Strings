package com.pedestriamc.strings.api.text.format;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("unused")
public final class StringsComponent implements ComponentLike {

    private final List<Element<?>> elements = new ArrayList<>();

    public StringsComponent(final Element<?> @NotNull ... elements) {
        for(int i=0; i<elements.length; i++) {
            Element<?> element = elements[i];
            Objects.requireNonNull(element);
            this.elements.add(element);
        }
    }

    public @NotNull String toString() {
        StringBuilder builder = new StringBuilder();
        for(Element<?> element : elements) {
            builder.append(element.toString());
        }
        return builder.toString();
    }

    @Override
    public @NotNull Component asComponent() {
        if(elements.isEmpty()) {
            return Component.empty();
        }
        return Component.text("wow");
    }
}
