package io.github.itzispyder.enderpv.util;

import org.bukkit.entity.Player;

public abstract class VaultUtils {

    public static int getMaxVaults(Player p) {
        if (p == null) return 54;
        if (!p.isOnline()) return 54;

        int max = -1;
        for (int i = 0; i < 54; i++) {
            if (p.hasPermission("enderpv.commands.enderpv." + (i + 1))) max = (i + 1);
        }
        return max == -1 ? 54 : max;
    }
}
