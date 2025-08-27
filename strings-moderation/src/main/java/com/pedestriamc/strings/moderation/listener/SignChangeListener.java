package com.pedestriamc.strings.moderation.listener;

import com.pedestriamc.strings.api.event.moderation.SignTextFilterEvent;
import com.pedestriamc.strings.moderation.StringsModeration;
import com.pedestriamc.strings.moderation.manager.ChatFilter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SignChangeListener implements Listener {

    private final StringsModeration strings;
    private final ChatFilter chatFilter;

    public SignChangeListener(@NotNull StringsModeration strings) {
        this.strings = strings;
        chatFilter = strings.getChatFilter();
    }

    @EventHandler
    void onEvent(@NotNull SignChangeEvent event) {
        String[] lines = event.getLines();
        List<Map.Entry<String, String>> lineList = new ArrayList<>(lines.length);

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            String filteredLine = chatFilter.filter(lines[i]).message();
            lineList.add(Map.entry(line, filteredLine));
            event.setLine(i, filteredLine);
        }

        strings.synchronous(() ->
                strings.getServer()
                .getPluginManager()
                .callEvent(new SignTextFilterEvent(event, lineList)));
    }
}
