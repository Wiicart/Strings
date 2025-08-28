package com.pedestriamc.strings.api.settings;

import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Immutable registry to back Settings implementations
 */
@Internal
public final class SettingsRegistry {

    private final Map<Class<?>, EnumMap<? extends Key<?>, ?>> master;

    public SettingsRegistry(@NotNull Consumer<RegistryBuilder> consumer) {
        RegistryBuilder builder = new RegistryBuilder();
        consumer.accept(builder);
        master = Collections.unmodifiableMap(builder.master);
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
        EnumMap<E, V> map = (EnumMap<E, V>) master.get(key.getDeclaringClass());
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
    public static class RegistryBuilder {

        private final Map<Class<?>, EnumMap<? extends Key<?>, ?>> master = new HashMap<>();

        private static boolean containsNoNulls(@NotNull Map<?, ?> map) {
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                if (entry.getValue() == null || entry.getKey() == null) {
                    return false;
                }
            }

            return true;
        }

        /**
         * Creates and adds a new EnumMap to the registry
         * @param clazz The enum class
         * @return this
         * @param <E> The enum type
         * @param <V> The value type
         */
        public <E extends Enum<E> & Key<V>, V> RegistryBuilder createEnumMap(Class<E> clazz) {
            this.master.put(clazz, new EnumMap<>(clazz));
            return this;
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
        public <E extends Enum<E> & Key<V>, V> RegistryBuilder put(@NotNull E key, @NotNull V value) {
            return put(Map.entry(key, value));
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
        @SuppressWarnings("unchecked")
        public <E extends Enum<E> & Key<V>, V> RegistryBuilder put(@NotNull Map.Entry<E, V> entry) {
            E key = Objects.requireNonNull(entry.getKey());
            V value = Objects.requireNonNull(entry.getValue());

            if (!this.master.containsKey(key.getDeclaringClass())) {
                createEnumMap(key.getDeclaringClass());
            }

            Map<E, V> map = (Map<E, V>) this.master.get(key.getDeclaringClass());
            map.put(key, value);

            return this;
        }

        /**
         * Puts a Map of key-value entries into the registry. Keys and values must not be null.
         * @param map The map
         * @return this
         * @throws NullPointerException If a key or value is null
         * @param <E> The enum type
         * @param <V> The value type
         */
        @SuppressWarnings("unchecked")
        public <E extends Enum<E> & Key<V>, V> RegistryBuilder putAll(@NotNull Map<E, V> map) {
            if (!containsNoNulls(map)) {
                throw new NullPointerException("Maps used with RegistryBuilder cannot contain null keys or values.");
            }


            Map.Entry<E, V> first;
            if (!map.isEmpty()) {
                first = map.entrySet().iterator().next();
            } else {
                return this;
            }

            Class<E> clazz = first.getKey().getDeclaringClass();
            if (!this.master.containsKey(clazz)) {
                createEnumMap(clazz);
            }

            Map<E, V> internal = (Map<E, V>) this.master.get(clazz);
            internal.putAll(map);

            return this;
        }
    }
}
