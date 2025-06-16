package com.pedestriamc.strings.api.text.format;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextComponent;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Immutable simple Component wrapper that makes converting to a legacy String easier.
 */
@SuppressWarnings("unused")
public final class StringsComponent implements ComponentLike {

    private static final Set<WeakReference<StringsComponent>> instances = Collections.newSetFromMap(new ConcurrentHashMap<>());

    private static final Pattern STANDARD_CODE = Pattern.compile("§[0-9a-frk-o]", Pattern.CASE_INSENSITIVE);

    private final List<Element<?>> elements;
    private final Component component;
    private final String toString;

    //TODO: refactor: i.e. -> not every char needs its own StringsTextComponent, so work up until another section sign is met
    //TODO fix §x§f§f§3§3§5§5§l§o§n§mHelloDog§r§oInevergiveyoumypillow -> HelloDogInevergiveyoumypillowHelloDogInevergiveyoumypillow (colored kinda right) (????)
    @Contract("_ -> new")
    public static @NotNull StringsComponent fromString(@NotNull String string) {
        final List<Element<?>> list = new ArrayList<>();

        Matcher hexMatcher = StringsTextColor.HEX_PATTERN.matcher(string);
        Matcher standardMatcher = STANDARD_CODE.matcher(string);

        boolean hexFound = hexMatcher.find();
        boolean stdFound = standardMatcher.find();

        int i = 0;
        while (i < string.length()) {
            int hexStart = hexFound ? hexMatcher.start() : Integer.MAX_VALUE;
            int stdStart = stdFound ? standardMatcher.start() : Integer.MAX_VALUE;

            if (hexStart == i && hexStart < stdStart) {
                String match = hexMatcher.group();
                list.add(StringsTextColor.fromSectionHexString(match));
                i = hexMatcher.end();
                hexMatcher.region(i, string.length());
                hexFound = hexMatcher.find();

                // Resync standard matcher
                standardMatcher.region(i, string.length());
                stdFound = standardMatcher.find();

            } else if (stdStart == i) {
                String match = standardMatcher.group().toLowerCase(Locale.ROOT);
                Element<?> el = fromChar(match.charAt(1));
                if (el != null) {
                    list.add(el);
                }
                i = standardMatcher.end();
                standardMatcher.region(i, string.length());
                stdFound = standardMatcher.find();

                // Resync hex matcher too
                hexMatcher.region(i, string.length());
                hexFound = hexMatcher.find();

            } else {
                list.add(StringsTextComponent.of(String.valueOf(string.charAt(i))));
                i++;
            }
        }

        return of(list);
    }



    private static @Nullable Element<?> fromChar(char ch) {
        Element<?> el = StringsTextColor.fromChar(ch);
        if(el == null) {
            el = StringsTextDecoration.fromChar(ch);
        }
        return el;
    }

    public static @NotNull StringsComponent of(final Element<?> @NotNull ... elements) {
        return of(List.of(elements));
    }

    public static @NotNull StringsComponent of(final @NotNull List<Element<?>> elements) {
        StringsComponent c = resolveInstance(elements);
        if(c != null) {
            return c;
        }
        return new StringsComponent(elements);
    }

    private StringsComponent(final List<Element<?>> elements) {
        this.elements = new ArrayList<>(elements);
        TextComponent.Builder builder = Component.text();
        for(Element<?> element : elements) {
            builder.append(element.asComponent());
        }
        component = builder.build();
        instances.add(new WeakReference<>(this));
        toString = generateString();
    }

    private static @Nullable StringsComponent resolveInstance(List<Element<?>> elements) {
        cleanupInstances();
        for(WeakReference<StringsComponent> instance : instances) {
            StringsComponent component = instance.get();
            if(component != null && component.elements.equals(elements)) {
                return component;
            }
        }
        return null;
    }

    private static void cleanupInstances() {
        instances.removeIf(ref -> ref.get() == null);
    }

    @Override
    public @NotNull Component asComponent() {
        return component;
    }

    private @NotNull String generateString() {
        StringBuilder builder = new StringBuilder();
        for(Element<?> element : elements) {
            builder.append(element.toString());
        }
        return builder.toString();
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
