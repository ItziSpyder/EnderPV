package io.github.itzispyder.enderpv.data;

import io.github.itzispyder.pdk.Global;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import static io.github.itzispyder.enderpv.EnderPV.prefix;

public final class VaultManager {

    public static void gui_openPlayerVault(Player p, VaultProfile profile, Vault v, InventoryClickEvent e, ItemStack input) {
        if (!input.getType().isAir()) {
            if (v.isFull()) return;
            v.addItem(input);
            profile.save();
            e.getView().setCursor(null);
            profile.openForOwner();
            p.playSound(p.getLocation(), Sound.ENTITY_ITEM_PICKUP,1,10);
            return;
        }
        v.openForOwner();
        p.playSound(p.getLocation(), Sound.BLOCK_ENDER_CHEST_OPEN,1,1);
    }

    public static void gui_changeIcon(Player p, VaultProfile profile, Vault v, ItemStack from, ItemStack to) {
        if (v.isEmpty()) return;
        if (to.getType().isAir()) {
            if (from != null && from.getType() == v.getDefaultIcon()) {
                Global.instance.info(p, prefix + "&cRIGHT-CLICK a vault slot with an item to set the icon!");
                return;
            }
            else {
                v.resetIcon();
                Global.instance.info(p, prefix + "&aReset icon for &7Vault #" + (v.getIndex() + 1));
            }
        }
        else {
            v.setIcon(to.getType());
            Global.instance.info(p, prefix + "&aIcon updated for &7Vault #" + (v.getIndex() + 1));
        }

        profile.save();
        profile.openForOwner();
        p.playSound(p.getLocation(), Sound.ITEM_DYE_USE,1,1);
    }
}
