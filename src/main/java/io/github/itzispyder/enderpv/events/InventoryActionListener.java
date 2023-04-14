package io.github.itzispyder.enderpv.events;

import io.github.itzispyder.enderpv.data.Vault;
import io.github.itzispyder.enderpv.data.VaultProfile;
import io.github.itzispyder.enderpv.util.Text;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import static io.github.itzispyder.enderpv.EnderPV.hiddenPrefix;
import static io.github.itzispyder.enderpv.EnderPV.prefix;

public class InventoryActionListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        final Player p = (Player) e.getWhoClicked();
        final Inventory inv = e.getClickedInventory();
        final String title = e.getView().getTitle();
        final ClickType click = e.getClick();

        try {
            final ItemStack picked = e.getCurrentItem();
            final ItemStack hovered = e.getCursor();

            if (inv.getType() == InventoryType.PLAYER) return;
            if (!title.contains(hiddenPrefix)) return;

            String newTitle = title.replaceAll(hiddenPrefix,"");
            if (newTitle.contains(Text.color("'s Vaults"))) {
                e.setCancelled(true);

                String name = newTitle.replaceAll(Text.color("'s Vaults"),"").trim();
                OfflinePlayer owner = name.isBlank() ? p : Bukkit.getOfflinePlayer(name);
                VaultProfile profile = VaultProfile.load(owner.getUniqueId());
                Vault v = profile.getVault(e.getSlot());

                if (click == ClickType.LEFT) {
                    p.openInventory(v.getGui(false));
                    p.playSound(p.getLocation(), Sound.BLOCK_ENDER_CHEST_OPEN,1,0.1F);
                }
                else if (click == ClickType.RIGHT) {
                    if (v.isEmpty()) return;
                    if (hovered == null || hovered.getType().isAir()) {
                        if (picked.getType() == v.getDefaultIcon()) {
                            p.sendMessage(Text.color(prefix + "&cRIGHT-CLICK a vault slot with an item to set the icon!"));
                            return;
                        }
                        else {
                            v.resetIcon();
                            p.sendMessage(Text.color(prefix + "&aReset icon for &7Vault #" + (v.getIndex() + 1)));
                        }
                    }
                    else {
                        v.setIcon(hovered.getType());
                        p.sendMessage(Text.color(prefix + "&aIcon updated for &7Vault #" + (v.getIndex() + 1)));
                    }

                    profile.save();
                    p.openInventory(profile.getGui());
                    p.playSound(p.getLocation(), Sound.ITEM_DYE_USE,1,1);
                }
            }
            else if (newTitle.contains(Text.color(" Vault #"))) {
                String[] titles = newTitle.split(Text.color(" Vault #"));
                String name = titles[0].replaceAll("'s","");
                OfflinePlayer owner = name.isBlank() ? p : Bukkit.getOfflinePlayer(name);
                VaultProfile profile = VaultProfile.load(owner.getUniqueId());
                int index = Integer.parseInt(titles[1]) - 1;

                profile.update(index, inv.getContents());
                profile.save();
            }
        }
        catch (Exception ignore) {
            ignore.printStackTrace();
        }
    }

    @EventHandler
    public void onOpen(InventoryOpenEvent e) {
        final Inventory inv = e.getInventory();
        final Player p = (Player) e.getPlayer();

        try {
            if (inv.getType() == InventoryType.ENDER_CHEST) {
                e.setCancelled(true);

                VaultProfile profile = VaultProfile.load(p.getUniqueId());
                p.openInventory(profile.getGui());
            }
        }
        catch (Exception ignore) {
            ignore.printStackTrace();
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        final Inventory inv = e.getInventory();
        final Player p = (Player) e.getPlayer();
        final String title = e.getView().getTitle();

        try {
            if (inv.getType() == InventoryType.PLAYER) return;
            if (!title.contains(hiddenPrefix)) return;

            String newTitle = title.replaceAll(hiddenPrefix,"");
            if (newTitle.contains(Text.color(" Vault #"))) {
                String[] titles = newTitle.split(Text.color(" Vault #"));
                String name = titles[0].replaceAll("'s","");
                OfflinePlayer owner = name.isBlank() ? p : Bukkit.getOfflinePlayer(name);
                VaultProfile profile = VaultProfile.load(owner.getUniqueId());
                int index = Integer.parseInt(titles[1]) - 1;

                profile.update(index, inv.getContents());
                profile.save();
            }
        }
        catch (Exception ignore) {
            ignore.printStackTrace();
        }
    }
}
