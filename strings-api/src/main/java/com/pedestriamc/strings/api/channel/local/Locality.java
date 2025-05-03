package com.pedestriamc.strings.api.channel.local;

import com.pedestriamc.strings.api.platform.Platform;
import com.pedestriamc.strings.api.user.StringsUser;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

@SuppressWarnings("unused")
public interface Locality {

    boolean contains(@NotNull StringsUser user);

    Set<StringsUser> getUsers();

    Platform getPlatform();

}
