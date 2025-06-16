package com.pedestriamc.strings.api.text.format;

import com.google.common.collect.ImmutableList;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextComponent;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * Immutable simple Component wrapper that makes converting to a String easier.
 */
@SuppressWarnings("unused")
public final class StringsComponent implements ComponentLike {

    private static final Set<StringsComponent> instances = Collections
            .newSetFromMap(Collections.synchronizedMap(new WeakHashMap<>()));

    private final List<Element<?>> elements;
    private final Component component;
    private final String toString;

    @Contract("_ -> new")
    public static @NotNull StringsComponent fromString(@NotNull String string) {
        return StringsComponentUtils.createFromString(string);
    }

    public static @NotNull StringsComponent of(final Element<?> @NotNull ... elements) {
        return of(List.of(elements));
    }

    public static @NotNull StringsComponent of(@NotNull List<Element<?>> elements) {
        elements = StringsComponentUtils.compact(elements);
        StringsComponent c = resolveInstance(elements);
        if(c != null) {
            return c;
        }
        return new StringsComponent(elements);
    }

    private StringsComponent(final List<Element<?>> elements) {
        this.elements = ImmutableList.copyOf(elements);

        TextComponent.Builder builder = Component.text();
        for(Element<?> element : elements) {
            builder.append(element.asComponent());
        }
        component = builder.build();

        instances.add(this);
        toString = StringsComponentUtils.convertToString(this);
    }

    public @NotNull StringsComponent append(final @NotNull Element<?> @NotNull ... elements) {
        List<Element<?>> newElements = new ArrayList<>(this.elements);
        newElements.addAll(List.of(elements));
        return of(newElements);
    }

    public @NotNull StringsComponent append(final @NotNull StringsComponent that) {
        List<Element<?>> newElements = new ArrayList<>(this.elements);
        newElements.addAll(that.elements);
        return of(newElements);
    }

    public @NotNull StringsComponent append(final @NotNull String string) {
        List<Element<?>> newElements = new ArrayList<>(this.elements);
        newElements.add(StringsTextComponent.of(string));
        return of(newElements);

    }

    private static @Nullable StringsComponent resolveInstance(List<Element<?>> elements) {
        for(StringsComponent stringsComponent : instances) {
            if(stringsComponent != null && stringsComponent.elements.equals(elements)) {
                return stringsComponent;
            }
        }
        return null;
    }

    List<Element<?>> getElements() {
        return elements;
    }

    @Override
    public @NotNull Component asComponent() {
        return component;
    }

    @Override
    public @NotNull String toString() {
        return toString;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        StringsComponent that = (StringsComponent) o;
        return elements.equals(that.elements);
    }

    @Override
    public int hashCode() {
        return elements.hashCode();
    }
}
