package com.pedestriamc.strings.api.event.strings;

public interface Cancellable extends StringsEvent {

    boolean isCancelled();

    void setCancelled(boolean cancelled);

}
