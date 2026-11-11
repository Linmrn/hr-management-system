package net.xju.common.utils;

import net.xju.entity.Hr;

public class HrUtils {
    private static ThreadLocal<Hr> currentHr = new ThreadLocal<>();

    public static void setCurrentHr(Hr hr) {
        currentHr.set(hr);
    }

    public static Hr getCurrentHr() {
        return currentHr.get();
    }

    public static boolean isAdmin(Hr hr) {
        return hr != null && hr.getRoles() != null && hr.getRoles().stream()
            .anyMatch(role -> "ROLE_admin".equals(role.getName()));
    }

    public static void clear() {
        currentHr.remove();
    }
}