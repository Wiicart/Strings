package com.pedestriamc.strings.api.text.format;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.Style;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

public final class StringsTextComponent implements TextComponent, Element<TextComponent> {

    private final @NotNull String content;

    @Contract(value = "_ -> new", pure = true)
    public static @NotNull StringsTextComponent of(@NotNull String content) {
        return new StringsTextComponent(content);
    }

    private StringsTextComponent(@NotNull String content) {
        this.content = content;
    }

    @Override
    public @NotNull String content() {
        return content;
    }

    @Override
    public @NotNull TextComponent content(@NotNull String content) {
        return of(content);
    }

    @Override
    public @NotNull Builder toBuilder() {
        return Component.text().content(content());
    }

    @Override
    public @NotNull @Unmodifiable List<Component> children() {
        return List.of();
    }

    @Override
    public @NotNull TextComponent children(@NotNull List<? extends ComponentLike> children) {
        return this;
    }

    @Override
    public @NotNull Style style() {
        return Style.empty();
    }

    @Override
    public @NotNull TextComponent style(@NotNull Style style) {
        return Component.text(content()).style(style);
    }

    @Override
    public @NotNull TextComponent toAdventure() {
        return this;
    }

    @Override
    public @NotNull Type getType() {
        return Type.TEXT;
    }

    @Override
    public @NotNull String toString() {
        return content();
    }
}
