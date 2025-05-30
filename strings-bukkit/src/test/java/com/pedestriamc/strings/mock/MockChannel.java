package com.pedestriamc.strings.mock;

import com.pedestriamc.strings.api.channel.Membership;
import com.pedestriamc.strings.api.channel.Type;
import com.pedestriamc.strings.channel.base.ProtectedChannel;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class MockChannel extends ProtectedChannel {

    @Override
    public void sendMessage(@NotNull Player player, @NotNull String message) {
        System.out.println("<" + player.getName() + "> " + message);
    }

    public MockChannel(String name) {
        super(name);
    }

    @Override
    public boolean allows(@NotNull Permissible permissible) {
        return true;
    }

    @Override
    public @NotNull Type getType() {
        return Type.NORMAL;
    }

    @Override
    public @NotNull Membership getMembership() {
        return Membership.DEFAULT;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "mockchannel";
    }
}
