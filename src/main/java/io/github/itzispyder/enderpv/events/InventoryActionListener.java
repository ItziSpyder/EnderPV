package io.github.itzispyder.enderpv.events;

import io.github.itzispyder.enderpv.data.Config;
import io.github.itzispyder.enderpv.data.Vault;
import io.github.itzispyder.enderpv.data.VaultManager;
import io.github.itzispyder.enderpv.data.VaultProfile;
import io.github.itzispyder.enderpv.util.VaultUtils;
import io.github.itzispyder.pdk.events.CustomListener;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

import static io.github.itzispyder.enderpv.EnderPV.hiddenPrefix;

public class InventoryActionListener implements CustomListener {

    private static final String SPECIFIED_VAULT_DISCRIMINATOR = " Vault #";
    private static final String OVERVIEW_VAULT_DISCRIMINATOR = "'s Vaults";

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        try {
            this.handleGuiInteraction(e);
        }
        catch (Exception ignore) {}
    }

    @EventHandler
    public void onOpen(InventoryOpenEvent e) {
        try {
            this.handleEnderChestOverride(e);
        }
        catch (Exception ignore) {}
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        try {
            this.handleSaveAndAntiDupe(e);
        }
        catch (Exception ignore) {}
    }

    @SuppressWarnings("all")
    private void handleSaveAndAntiDupe(InventoryCloseEvent e) {
        Inventory inv = e.getInventory();
        Player p = (Player) e.getPlayer();
        String title = e.getView().getTitle();

        if (inv.getType() == InventoryType.PLAYER || !title.contains(hiddenPrefix)) {
            return;
        }

        String newTitle = title.replace(hiddenPrefix,"");

        if (newTitle.contains(SPECIFIED_VAULT_DISCRIMINATOR)) {
            String[] titles = newTitle.split(color(SPECIFIED_VAULT_DISCRIMINATOR));
            String name = titles[0].replace("'s","");
            OfflinePlayer owner = Bukkit.getOfflinePlayer(name.isEmpty() ? p.getName() : name);
            VaultProfile profile = VaultProfile.load(owner.getUniqueId());

            int index = Integer.parseInt(titles[1]) - 1;

            profile.update(index, inv.getContents());
            profile.closeForOwner();
            profile.save();
        }
        else if (newTitle.contains(OVERVIEW_VAULT_DISCRIMINATOR)) {
            String name = newTitle.replace(OVERVIEW_VAULT_DISCRIMINATOR, "");
            OfflinePlayer owner = Bukkit.getOfflinePlayer(name);
            VaultProfile profile = VaultProfile.load(owner.getUniqueId());

            profile.closeForOwner();
            profile.save();
        }
    }

    private void handleEnderChestOverride(InventoryOpenEvent e) {
        Inventory inv = e.getInventory();
        Player p = (Player) e.getPlayer();

        if (inv.getType() != InventoryType.ENDER_CHEST || !Config.Plugin.overrideEnderChests()) {
            return;
        }

        e.setCancelled(true);
        VaultProfile profile = VaultProfile.load(p.getUniqueId());
        profile.openForOwner();
    }

    @SuppressWarnings("all")
    private void handleGuiInteraction(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        Inventory inv = e.getClickedInventory();
        String title = e.getView().getTitle();
        ClickType click = e.getClick();
        ItemStack picked = e.getCurrentItem();
        ItemStack hovered = e.getCursor();

        if (inv == null || inv.getType() == InventoryType.PLAYER || !title.contains(hiddenPrefix)) {
            return;
        }

        String newTitle = title.replace(hiddenPrefix,"");

        if (newTitle.contains(OVERVIEW_VAULT_DISCRIMINATOR)) {
            e.setCancelled(true);

            if (VaultUtils.getMaxVaults(p) < (e.getSlot() + 1)) {
                return;
            }

            String name = newTitle.replace(OVERVIEW_VAULT_DISCRIMINATOR, "").trim();
            UUID id = Bukkit.getPlayerUniqueId(name);
            VaultProfile profile = VaultProfile.load(id);
            Vault v = profile.getVault(e.getSlot());

            switch (click) {
                case LEFT -> VaultManager.gui_openPlayerVault(p, profile, v, e, hovered);
                case RIGHT -> VaultManager.gui_changeIcon(p, profile, v, picked, hovered);
            }
        }
        else if (newTitle.contains(SPECIFIED_VAULT_DISCRIMINATOR)) {
            String[] titles = newTitle.split(SPECIFIED_VAULT_DISCRIMINATOR);
            String name = titles[0].replace("'s","");
            UUID id = Bukkit.getPlayerUniqueId(name);
            VaultProfile profile = VaultProfile.load(id);

            int index = Integer.parseInt(titles[1]) - 1;

            profile.update(index, inv.getContents());
            profile.save();
        }
    }
}
