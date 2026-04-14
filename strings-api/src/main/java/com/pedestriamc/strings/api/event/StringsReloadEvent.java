package com.pedestriamc.strings.api.event;

import com.pedestriamc.strings.api.event.strings.StringsEvent;
import org.jetbrains.annotations.ApiStatus;

/**
 * Called when the Strings plugin reloads.
 */
@ApiStatus.Internal
public final class StringsReloadEvent implements StringsEvent {

    @ApiStatus.Internal
    StringsReloadEvent() {}

}
