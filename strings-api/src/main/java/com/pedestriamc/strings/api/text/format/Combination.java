package com.pedestriamc.strings.api.text.format;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("unused")
public final class Combination {

    private final List<Element<?>> elements = new ArrayList<>();

    public Combination(Element<?> @NotNull ... elements) {
        for(int i=0; i<elements.length; i++) {
            Element<?> element = elements[i];
            Objects.requireNonNull(element);
            this.elements.add(element);
        }
    }

    public @NotNull Component toComponent() {
        return Component.text("wow");
    }

    public @NotNull String toString() {
        StringBuilder builder = new StringBuilder();
        for(Element<?> element : elements) {
            builder.append(element.toString());
        }
        return builder.toString();
    }

}
