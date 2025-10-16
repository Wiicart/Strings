package com.pedestriamc.strings.mock;

import com.pedestriamc.strings.api.channel.Membership;
import com.pedestriamc.strings.api.channel.Type;
import com.pedestriamc.strings.api.user.StringsUser;
import com.pedestriamc.strings.channel.base.ProtectedChannel;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class MockChannel extends ProtectedChannel {

    @Override
    public void sendMessage(@NotNull StringsUser user, @NotNull String message) {
        System.out.println("<" + user.getName() + "> " + message);
    }

    public MockChannel(String name) {
        super(name);
    }

    @Override
    public boolean allows(@NotNull StringsUser user) {
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

}
