package com.pedestriamc.strings.api.text.format;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ApiStatus.Internal
final class StringsComponentUtils {

    private static final Pattern STANDARD_CODE = Pattern.compile("§[0-9a-frk-o]", Pattern.CASE_INSENSITIVE);

    // Util class - no instances should be created
    private StringsComponentUtils() {}

    // Compacts an element List by merging adjacent StringsTextComponents
    @Contract(pure = true)
    static @NotNull List<Element<?>> compact(final @NotNull List<Element<?>> elements) {
        List<Element<?>> compacted = new ArrayList<>();

        ListIterator<Element<?>> it = elements.listIterator();
        StringsTextComponent compact = StringsTextComponent.of();
        while(it.hasNext()) {
            Element<?> element = it.next();
            if(element instanceof StringsTextComponent c) {
                compact = compact.append(c);
            } else {
                if(!compact.isEmpty()) {
                    compacted.add(compact);
                    compact = StringsTextComponent.of();
                }
                compacted.add(element);
            }
        }

        // Ensure any final text is added
        if (!compact.isEmpty()) {
            compacted.add(compact);
        }

        return compacted;
    }

    static @NotNull String convertToString(@NotNull StringsComponent component) {
        StringBuilder builder = new StringBuilder();
        for(Element<?> element : component.getElements()) {
            builder.append(element.toString());
        }
        return builder.toString();
    }

    static @NotNull StringsComponent createFromString(@NotNull String string) {
        final List<Element<?>> list = new ArrayList<>();

        Matcher hexMatcher = StringsTextColor.HEX_PATTERN.matcher(string);
        Matcher standardMatcher = STANDARD_CODE.matcher(string);

        int i = 0;
        while (i < string.length()) {
            if (i + 14 <= string.length()) {
                hexMatcher.region(i, i + 14);
                if (hexMatcher.matches()) {
                    String match = hexMatcher.group();
                    list.add(StringsTextColor.fromSectionHexString(match));
                    i += 14;
                    continue;
                }
            }

            if (i + 2 <= string.length()) {
                standardMatcher.region(i, i + 2);
                if (standardMatcher.matches()) {
                    String match = standardMatcher.group();
                    Element<?> el = fromChar(match.charAt(1));
                    if (el != null) {
                        list.add(el);
                    }
                    i += 2;
                    continue;
                }
            }

            String at = String.valueOf(string.charAt(i));
            list.add(StringsTextComponent.of(at));
            i++;
        }

        // The list will be compacted in the of() call
        return StringsComponent.of(list);
    }

    private static @Nullable Element<?> fromChar(char ch) {
        Element<?> el = StringsTextColor.fromChar(ch);
        if(el == null) {
            el = StringsTextDecoration.fromChar(ch);
        }
        return el;
    }
}
