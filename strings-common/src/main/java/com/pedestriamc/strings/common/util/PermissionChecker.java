package com.pedestriamc.strings.common.util;

import com.pedestriamc.strings.api.user.StringsUser;
import org.jetbrains.annotations.NotNull;

public final class PermissionChecker {

    private PermissionChecker() {}

    public static boolean any(@NotNull StringsUser user, @NotNull String... permissions) {
        for (String permission : permissions) {
            if (user.hasPermission(permission)) {
                return true;
            }
        }
        return false;
    }

    public static boolean anyOrOp(@NotNull StringsUser user, @NotNull String... permissions) {
        return user.isOperator() || user.hasPermission("*") || any(user, permissions);
    }
}
