package com.pedestriamc.strings.api.collections.trie;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

// maybe refactor this sometime
final class LeetSpeakNormalizer {

    private LeetSpeakNormalizer() {}

    private static final Map<Character, Character> LEET_CHAR_MAP = Map.ofEntries(
            Map.entry('@', 'a'),
            Map.entry('4', 'a'),
            Map.entry('∆', 'a'),
            Map.entry('^', 'a'),
            Map.entry('8', 'b'),
            Map.entry('ß', 'b'),
            Map.entry('(', 'c'),
            Map.entry('<', 'c'),
            Map.entry('{', 'c'),
            Map.entry('[', 'c'),
            Map.entry('¢', 'c'),
            Map.entry(')', 'd'),
            Map.entry('3', 'e'),
            Map.entry('€', 'e'),
            Map.entry('£', 'e'),
            Map.entry('ë', 'e'),
            Map.entry('ƒ', 'f'),
            Map.entry('6', 'g'),
            Map.entry('&', 'g'),
            Map.entry('#', 'h'),
            Map.entry('!', 'i'),
            Map.entry('|', 'i'),
            Map.entry('¡', 'i'),
            Map.entry('î', 'i'),
            Map.entry(']', 'j'),
            Map.entry('κ', 'k'),
            Map.entry('1', 'l'),
            Map.entry('₪', 'n'),
            Map.entry('и', 'n'),
            Map.entry('0', 'o'),
            Map.entry('¤', 'o'),
            Map.entry('ρ', 'p'),
            Map.entry('9', 'q'),
            Map.entry('®', 'r'),
            Map.entry('Я', 'r'),
            Map.entry('5', 's'),
            Map.entry('$', 's'),
            Map.entry('§', 's'),
            Map.entry('ŝ', 's'),
            Map.entry('7', 't'),
            Map.entry('+', 't'),
            Map.entry('†', 't'),
            Map.entry('µ', 'u'),
            Map.entry('ü', 'u'),
            Map.entry('Ш', 'w'),
            Map.entry('×', 'x'),
            Map.entry('¥', 'y'),
            Map.entry('j', 'y'),
            Map.entry('2', 'z'),
            Map.entry('≥', 'z')
    );

    private static final Map<String, String> LEET_STRING_MAP = Map.ofEntries(
            Map.entry("ph", "f"),
            Map.entry("vv", "w"),
            Map.entry("ii", "u"),
            Map.entry("|\\|", "n"),
            Map.entry("|-|", "h"),
            Map.entry("|\\/", "v"),
            Map.entry("|_", "l"),
            Map.entry("(_)","u"),
            Map.entry("()","o")
    );

    @NotNull
    static String normalize(@NotNull String input) {
        StringBuilder builder = new StringBuilder();
        for (char c : input.toCharArray()) {
            builder.append(LEET_CHAR_MAP.getOrDefault(c, c));
        }

        String normal = builder.toString();
        for (Map.Entry<String, String> entry : LEET_STRING_MAP.entrySet()) {
            normal = normal.replace(entry.getKey(), entry.getValue());
        }

        return normal;
    }




}
