package com.pedestriamc.strings.api.text.format;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.Style;
import net.kyori.examination.ExaminableProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.stream.Stream;

final class StringsTextComponent2 extends StringsTextComponent {

    private final TextComponent backer;

    StringsTextComponent2(@NotNull String content) {
        super(content);
        backer = Component.text(content);
    }

    @Override
    public @NotNull String content() {
        return backer.content();
    }

    @Override
    public @NotNull TextComponent content(@NotNull String content) {
        return backer.content(content);
    }

    @Override
    public @NotNull Builder toBuilder() {
        return backer.toBuilder();
    }

    @Override
    public @NotNull @Unmodifiable List<Component> children() {
        return backer.children();
    }

    @Override
    public @NotNull TextComponent children(@NotNull List<? extends ComponentLike> children) {
        return backer.children(children);
    }

    @Override
    public @NotNull Style style() {
        return backer.style();
    }

    @Override
    public @NotNull TextComponent style(@NotNull Style style) {
        return backer.style(style);
    }

    @Override
    public @NotNull Component asComponent() {
        return backer.asComponent();
    }

    @Override
    public @NotNull String toString() {
        return backer.toString();
    }

    @Override
    public @NotNull Stream<? extends ExaminableProperty> examinableProperties() {
        return backer.examinableProperties();
    }
}
