package com.pedestriamc.strings.api.utlity;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * Class to convert Components shaded into packages other than the typical adventure package to the normal package
 */
public final class SerialComponent implements ComponentLike, Serializable {

    public final String json;

    private final transient Component component;

    /**
     * Attempts to convert an Object into a SerialComponent.
     * @param object The Object to convert. This should be an adventure Component.
     * @return A new SerialComponent
     * @throws IllegalArgumentException If unable to serialize the object.
     */
    @NotNull
    public static SerialComponent serialize(@NotNull Object object) throws IllegalArgumentException {
        if (object instanceof ComponentLike c) {
            return new SerialComponent(GsonComponentSerializer.gson().serialize(c.asComponent()));
        }

        try {
            Class<?> componentClass = object.getClass();
            String serializerClassName = componentClass.getPackageName() + ".serializer.gson.GsonComponentSerializer";
            Class<?> serializerClass = Class.forName(serializerClassName);

            Object gson = serializerClass.getMethod("gson").invoke(null);
            Method serializerMethod = gson.getClass().getMethod("serialize", componentClass);
            String json = (String) serializerMethod.invoke(gson, object);

            return new SerialComponent(json);
        } catch(Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Constructs a SerialComponent
     * @param json The JSON content of the Component (Serialize using {@link GsonComponentSerializer#serialize(Component)})
     */
    public SerialComponent(String json) {
        this.json = json;
        try {
            component = deserialize();
        } catch(Exception e) {
            throw new IllegalArgumentException("Failed to handle JSON. Message: " + e.getMessage(), e);
        }
    }


    @Override
    @NotNull
    public Component asComponent() {
        return component;
    }

    public String json() {
        return json;
    }

    @NotNull
    private Component deserialize() {
        return GsonComponentSerializer.gson().deserialize(json);
    }

}
