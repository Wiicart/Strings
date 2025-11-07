package com.pedestriamc.strings.api.collections;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * An immutable {@link Map} optimized for enums.
 * Looks up by using Enum ordinals.
 * @param <K> The key type (An enum)
 * @param <V> The value type
 */
@SuppressWarnings("unchecked")
public class ImmutableEnumMap<K extends Enum<K>, V> implements Map<K, V> {

    private static final ImmutableEnumMap<?, ?> EMPTY = new ImmutableEnumMap<>(Generic.class);

    /**
     * Provides an empty ImmutableEnumMap
     * @return An existing empty ImmutableEnumMap
     */
    @NotNull
    public static <K extends Enum<K>, V> ImmutableEnumMap<K, V> of() {
        return (ImmutableEnumMap<K, V>) EMPTY;
    }

    /**
     * Creates an ImmutableEnumMap with the specified entries
     * @param clazz The Enum class
     * @param entries The entries
     * @return a new ImmutableEnumMap
     * @param <K> The Key type
     * @param <V> The value type
     */
    @NotNull
    public static <K extends Enum<K>, V> ImmutableEnumMap<K, V> ofEntries(@NotNull Class<K> clazz, @NotNull Map.Entry<K, V>... entries) {
        return new ImmutableEnumMap<>(clazz, entries);
    }

    /**
     * Converts an EnumMap into a ImmutableEnumMap
     * @param map The original Map
     * @return An ImmutableEnumMap with the same entries as the original Map
     * @param <K> The key type
     * @param <V> The value type
     */
    @NotNull
    public static <K extends Enum<K>, V> ImmutableEnumMap<K, V> asImmutable(@NotNull EnumMap<K, V> map) {
        return new ImmutableEnumMap<>(map);
    }

    private static <K extends Enum<K>, V> Class<K> determineEnumClass(@NotNull EnumMap<K, V> map) {
        Object[] keys = map.keySet().toArray();
        return (Class<K>) (keys.length == 0 ? Generic.class : keys[0].getClass());
    }

    private final Class<K> enumClass;
    private final K[] keys;
    private final V[] vals;

    private ImmutableEnumMap(@NotNull EnumMap<K, V> map) {
        this(determineEnumClass(map), map.entrySet().toArray(new Entry[0]));
    }

    /**
     * Constructs an ImmutableEnumMap
     * @param enumClass The enum class used for keys
     * @param entries The entries for the Map
     */
    private ImmutableEnumMap(@NotNull Class<K> enumClass, @NotNull Entry<K, V>... entries) {
        if (!enumClass.isEnum()) {
            throw new IllegalArgumentException("The class used when creating a EnumMap must be an enum.");
        }

        entries = filterDuplicates(entries);
        this.enumClass = enumClass;
        keys = (K[]) Array.newInstance(enumClass, entries.length);
        vals = (V[]) new Object[enumClass.getEnumConstants().length];

        int keyIndex = 0;
        for (Entry<K, V> entry : entries) {
            validate(entry);

            K key = entry.getKey();
            V value = entry.getValue();

            vals[key.ordinal()] = value;
            keys[keyIndex++] = key;
        }
    }

    @Override
    public @Nullable V get(@Nullable Object key) {
        if (!enumClass.isInstance(key)) {
            return null;
        }

        return vals[((Enum<?>) key).ordinal()];
    }

    @Override
    public int size() {
        return keys.length;
    }

    @Override
    public boolean isEmpty() {
        return keys.length == 0;
    }

    @Override
    public boolean containsKey(Object key) {
        for (K k : keys) {
            if (k.equals(key)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean containsValue(Object value) {
        for (K key : keys) {
            if (Objects.equals(value, get(key))) {
                return true;
            }
        }

        return false;
    }

    @Override
    public @NotNull Set<K> keySet() {
        return Set.of(keys);
    }

    @Override
    public @NotNull Collection<V> values() {
        Set<V> set = new HashSet<>();
        for (K key : keys) {
            set.add(get(key));
        }

        return Collections.unmodifiableSet(set);
    }

    @Override
    public @NotNull Set<Entry<K, V>> entrySet() {
        Set<Entry<K, V>> entries = new HashSet<>(keys.length);
        for (K key : keys) {
            entries.add(new AbstractMap.SimpleEntry<>(key, get(key)));
        }

        return Collections.unmodifiableSet(entries);
    }

    @Override
    public @Nullable V put(K key, V value) {
        throw new UnsupportedOperationException("Cannot modify immutable map");
    }

    @Override
    public V remove(Object key) {
        throw new UnsupportedOperationException("Cannot modify immutable map");
    }

    @Override
    public void putAll(@NotNull Map<? extends K, ? extends V> m) {
        throw new UnsupportedOperationException("Cannot modify immutable map");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Cannot modify immutable map");
    }

    @Override
    public int hashCode() {
        return entrySet().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Map)) {
            return false;
        }
        return entrySet().equals(((Map<?, ?>) o).entrySet());
    }

    private void validate(@NotNull Entry<K, V> entry) {
        if (!enumClass.isInstance(entry.getKey())) {
            throw new IllegalArgumentException("Invalid entry provided, all entries must be of the same enum type.");
        }
    }

    @NotNull
    private Entry<K, V>[] filterDuplicates(@NotNull Entry<K, V>[] entries) {
        Map<K, Entry<K, V>> map = new HashMap<>(entries.length);
        for (Entry<K, V> entry : entries) {
            map.put(entry.getKey(), entry);
        }
        
        return map.values().toArray(new Entry[0]);
    }

    private enum Generic {}

}
