package com.pedestriamc.strings.commands.ignore;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.commands.AbstractCommand;
import com.pedestriamc.strings.user.User;
import net.kyori.adventure.text.Component;
import net.wiicart.commands.command.CartCommandExecutor;
import net.wiicart.commands.command.CommandData;
import org.jetbrains.annotations.NotNull;

class IgnoreListCommand extends AbstractCommand implements CartCommandExecutor {

    public IgnoreListCommand(@NotNull Strings strings) {
        super(strings);
    }

    @Override
    public void onCommand(@NotNull CommandData data) {
        // WIP
    }

    @NotNull
    private Component generateListElement(@NotNull User user) {
        return Component.text().build();
    }
}
