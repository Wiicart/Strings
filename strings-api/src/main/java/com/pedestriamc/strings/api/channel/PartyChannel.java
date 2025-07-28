package com.pedestriamc.strings.api.channel;

import com.pedestriamc.strings.api.user.StringsUser;
import org.jetbrains.annotations.NotNull;

public interface PartyChannel extends Channel {

    /**
     * Provides the Channel's leader
     * @return the leader
     */
    @NotNull
    StringsUser getLeader();

    /**
     * Sets the Channel's leader
     * @param user The new leader
     */
    void setLeader(@NotNull StringsUser user);

}
