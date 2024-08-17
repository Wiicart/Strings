package com.pedestriamc.strings.api;

public interface Distributor {

    /**
     * Registers a StringsReceiver.
     * @param receiver The StringsReceiver to be registered.
     */
    void registerReceiver(StringsReceiver receiver);

    /**
     * Unregisters a StringsReceiver.
     * @param receiver The Receiver to be registered.
     */
    void unregisterReceiver(StringsReceiver receiver);
}
