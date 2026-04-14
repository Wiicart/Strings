package com.pedestriamc.strings.api.event.strings.user;

import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.user.StringsUser;

/**
 * Triggered when a {@link StringsUser} updates their active {@link Channel}
 */
public interface ActiveChannelUpdateEvent extends UserChannelEvent {}
