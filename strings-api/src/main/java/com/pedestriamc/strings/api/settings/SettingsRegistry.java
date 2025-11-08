package com.pedestriamc.strings.api.settings;

import com.google.common.collect.ImmutableMap;
import com.pedestriamc.strings.api.collections.ImmutableEnumMap;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Immutable registry to back Settings implementations
 */
@Internal
public final class SettingsRegistry {

    private final Map<Class<?>, ImmutableEnumMap<? extends Key<?>, ?>> master;

    public SettingsRegistry(@NotNull Consumer<Builder> consumer) {
        Builder builder = new Builder();
        consumer.accept(builder);
        master = builder.buildImmutable();
    }

    /**
     * Gets a value from a given Key
     * @param key The key
     * @return The value
     * @param <E> The enum type
     * @param <V> The value type
     */
    @NotNull
    @SuppressWarnings("unchecked")
    public <E extends Enum<E> & Key<V>, V> V get(@NotNull E key) { 
        Map<E, V> map = (Map<E, V>) master.get(key.getDeclaringClass());
        if (map == null) {
            return key.defaultValue();
        } else {
            V val = map.get(key);
            return val != null ? val : key.defaultValue();
        }
    }

    /**
     * Used to construct a SettingsRegistry.
     * Null keys and values are not permitted.
     */
    public static sealed class Builder {

        private final Map<Class<?>, EnumMap<? extends Key<?>, ?>> master = new HashMap<>();

        private Builder() {}

        @NotNull
        private Map<Class<?>, ImmutableEnumMap<? extends Key<?>, ?>> buildImmutable() {
            Map<Class<?>, ImmutableEnumMap<? extends Key<?>, ?>> map = new HashMap<>(master.size());
            for (Map.Entry<Class<?>, EnumMap<? extends Key<?>, ?>> entry : master.entrySet()) {
                map.put(entry.getKey(), ImmutableEnumMap.asImmutable(entry.getValue()));
            }

            return ImmutableMap.copyOf(map);
        }

        /**
         * Creates and adds a new EnumMap to the registry
         * @param clazz The enum class
         * @return this
         * @param <E> The enum type
         * @param <V> The value type
         */
        @SuppressWarnings("unchecked")
        public <E extends Enum<E> & Key<V>, V> Map<E, V> computeMapIfAbsent(Class<E> clazz) {
            return (Map<E, V>) this.master.computeIfAbsent(clazz, v -> new EnumMap<>(clazz));
        }

        /**
         * Puts a key-value entry into the registry.
         * Keys and values must not be null.
         * @param key The key
         * @param value The value
         * @return this
         * @throws NullPointerException If a key or value is null
         * @param <E> The enum type
         * @param <V> The value type
         */
        public <E extends Enum<E> & Key<V>, V> Builder put(@NotNull E key, @NotNull V value) {
            Map<E, V> map = computeMapIfAbsent(key.getDeclaringClass());
            map.put(key, value);

            return this;
        }

        /**
         * Puts a key-value entry into the registry.
         * Keys and values must not be null.
         * @param entry The entry
         * @return this
         * @throws NullPointerException If a key or value is null
         * @param <E> The enum type
         * @param <V> The value type
         */
        public <E extends Enum<E> & Key<V>, V> Builder put(@NotNull Map.Entry<E, V> entry) {
            return put(entry.getKey(), entry.getValue());
        }

        /**
         * Puts a Map of key-value entries into the registry. Keys and values must not be null.
         * @param map The map
         * @return this
         * @throws NullPointerException If a key or value is null
         * @param <E> The enum type
         * @param <V> The value type
         */
        public <E extends Enum<E> & Key<V>, V> Builder putAll(@NotNull Map<E, V> map) {
            if (containsNulls(map)) {
                throw new NullPointerException("Maps used with RegistryBuilder cannot contain null keys or values.");
            }

            Map.Entry<E, V> first;
            if (!map.isEmpty()) {
                first = map.entrySet().iterator().next();
            } else {
                return this;
            }

            Class<E> clazz = first.getKey().getDeclaringClass();
            Map<E, V> internal = computeMapIfAbsent(clazz);
            internal.putAll(map);

            return this;
        }

        private boolean containsNulls(@NotNull Map<?, ?> map) {
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                if (entry.getValue() == null || entry.getKey() == null) {
                    return true;
                }
            }

            return false;
        }
    }

    @Deprecated
    public static final class RegistryBuilder extends Builder {

    }
}
