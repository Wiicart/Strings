package com.pedestriamc.strings.common.systems;

import com.pedestriamc.strings.api.StringsPlatform;
import com.pedestriamc.strings.api.event.ChannelChatEvent;
import com.pedestriamc.strings.api.event.strings.Listener;
import com.pedestriamc.strings.common.manager.StringsEventManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

/**
 * Tests for {@link StringsEventManager}
 */
class EventManagerTest {

    @Test
    void dispatch_listenerListensToInterface_receivesEvents() {
        StringsPlatform strings = mock(StringsPlatform.class);
        StringsEventManager eventManager = new StringsEventManager(strings);

        class EventListener {
            boolean called = false;

            @Listener
            public void onEvent(ChannelChatEvent event) {
                called = true;
            }
        }

        EventListener listener = new EventListener();

        eventManager.subscribe(listener);
        eventManager.dispatch(mock(ChannelChatEvent.class));

        assertTrue(listener.called);
    }

    @Test
    void subscribe_listenerMethodsInParent_receivesEvents() {
        StringsPlatform strings = mock(StringsPlatform.class);
        StringsEventManager eventManager = new StringsEventManager(strings);

        class Parent {

            boolean called = false;

            @Listener
            void onEvent(ChannelChatEvent event) {
                called = true;
            }
        }

        class Child extends Parent {}

        Child child = new Child();
        eventManager.subscribe(child);
        eventManager.dispatch(mock(ChannelChatEvent.class));

        assertTrue(child.called);
    }

    @Test
    void unsubscribe_twoListenersRegistered_removesOnlyCorrectListener() {
        StringsPlatform strings = mock(StringsPlatform.class);
        StringsEventManager eventManager = new StringsEventManager(strings);

        class EventListener {
            boolean called = false;

            @Listener
            void onEvent(ChannelChatEvent event) {
                called = true;
            }
        }

        EventListener listener1 = new EventListener();
        EventListener listener2 = new EventListener();

        eventManager.subscribe(listener1, listener2);
        eventManager.unsubscribe(listener1);

        eventManager.dispatch(mock(ChannelChatEvent.class));

        assertFalse(listener1.called);
        assertTrue(listener2.called);
    }


}
