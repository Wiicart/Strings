package com.pedestriamc.strings.api.files;

import org.jetbrains.annotations.NotNull;

import java.io.File;

public interface FileManager {

    /**
     * Provides the "emojis.json" file.
     * @return A {@link File}
     */
    @NotNull File getEmojisFile();


}
