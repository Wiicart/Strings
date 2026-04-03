package com.pedestriamc.strings.common.systems;

import com.pedestriamc.strings.api.StringsPlatform;
import com.pedestriamc.strings.api.event.ChannelChatEvent;
import com.pedestriamc.strings.api.event.strings.Listener;
import com.pedestriamc.strings.common.event.StringsEventManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class EventManagerTest {

    @Test
    void dispatch_listenerListensToInterface_receivesEvents() {
        StringsPlatform strings = mock(StringsPlatform.class);

        StringsEventManager eventManager = new StringsEventManager(strings);
        EventListener listener = new EventListener();
        eventManager.subscribe(listener);
        eventManager.dispatch(mock(ChannelChatEvent.class));

        assertTrue(listener.called);
    }

    private static class EventListener {

        boolean called = false;

        @Listener
        public void onEvent(ChannelChatEvent event) {
            called = true;
        }
    }
}
