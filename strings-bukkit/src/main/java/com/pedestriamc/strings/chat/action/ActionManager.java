package com.pedestriamc.strings.chat.action;

import com.pedestriamc.strings.api.chat.action.ClickableAction;

import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("unused")
public class ActionManager {

    private final Set<ClickableAction> set;

    public ActionManager() {
        set = new HashSet<>();
    }

    public void register(ClickableAction action) {
        set.add(action);
    }

    public Set<ClickableAction> getSet() {
        return new HashSet<>(set);
    }

}
