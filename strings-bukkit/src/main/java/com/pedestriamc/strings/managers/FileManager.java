package com.pedestriamc.strings.managers;

import com.pedestriamc.strings.Strings;

@SuppressWarnings("unused")
public class FileManager {

    private final Strings strings;

    public FileManager(Strings strings) {
        this.strings = strings;
        load();
    }

    private void load() {
        //
    }
}
