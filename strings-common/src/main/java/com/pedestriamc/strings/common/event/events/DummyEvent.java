package com.pedestriamc.strings.common.event.events;

import com.pedestriamc.strings.api.event.server.ServerEvent;
import com.pedestriamc.strings.api.event.strings.Cancellable;
import com.pedestriamc.strings.api.event.strings.StringsEvent;

@SuppressWarnings("unused")
public class DummyEvent implements StringsEvent, Cancellable, ServerEvent {

    public DummyEvent() {
       // no init necessary
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        // No behavior expected
    }

}
