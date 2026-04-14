package com.pedestriamc.strings.common;

import com.pedestriamc.strings.api.StringsPlatform;
import com.pedestriamc.strings.common.manager.DirectMessageManager;
import org.jetbrains.annotations.NotNull;

public interface CommonStrings extends StringsPlatform {

    @NotNull DirectMessageManager getDirectMessageManager();

}
