package io.github.itzispyder.enderpv.data;

import io.github.itzispyder.pdk.Global;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.*;
import java.util.function.Consumer;

import static io.github.itzispyder.enderpv.EnderPV.hiddenPrefix;

public class Vault implements Serializable, ConfigurationSerializable, Global {

    private ItemStack[] contents;
    private final UUID owner;
    private final int index;
    private Material icon;

    private Vault(int index, UUID owner) {
        this.icon = Material.LIME_STAINED_GLASS_PANE;
        this.index = index;
        this.owner = owner;
        this.contents = new ItemStack[54];
    }

    public OfflinePlayer player() {
        return Bukkit.getOfflinePlayer(owner);
    }

    public void ifOwnerOnlineRun(Consumer<Player> action) {
        Player p = player().getPlayer();
        if (p == null) return;
        if (!p.isOnline()) return;
        action.accept(p);
    }

    public void setItem(int index, ItemStack item) {
        contents[index] = item;
    }

    public int firstEmpty() {
        for (int i = 0; i < 54; i++) {
            ItemStack item = contents[i];
            if (item == null) return i;
            if (item.getType().isAir()) return i;
        }
        return -1;
    }

    public void addItem(ItemStack... items) {
        Inventory inv = getGui(true);
        inv.addItem(items);
        setContents(inv.getContents());
    }

    public int getSize() {
        return Arrays.stream(contents).filter(Objects::nonNull).toList().size();
    }

    public boolean isEmpty() {
        return getSize() == 0;
    }
    public boolean isFull() {
        return getSize() == 54;
    }

    public String getStatus() {
        return color(isEmpty() ? "&cEmpty" : "&a" + getSize() + "&7/54 items");
    }

    @SuppressWarnings("all")
    public Inventory getGui(boolean self) {
        String name = self ? "" : player().getName() + "'s";
        Inventory inv = Bukkit.createInventory(null,54, hiddenPrefix + name + " Vault #" + (index + 1));

        for (int i = 0; i < contents.length; i++) {
            ItemStack item = contents[i];
            if (item == null) continue;
            inv.setItem(i,item);
        }

        return inv;
    }

    public void openForOwner() {
        this.ifOwnerOnlineRun(p -> {
            UUID id = Bukkit.getPlayerUniqueId(p.getName());
            if (VaultProfile.isAlreadyViewing(id)) {
                error(p, "Cannot view vault profile because the menu is already open for you.");
                return;
            }
            VaultProfile.setAlreadyViewing(id, true);
            p.openInventory(this.getGui(true));
        });
    }

    public void closeForOwner() {
        VaultProfile.setAlreadyViewing(getOwner(), false);
    }

    public ItemStack[] getContents() {
        return contents;
    }

    public UUID getOwner() {
        return owner;
    }

    public int getIndex() {
        return index;
    }

    public Material getIcon() {
        return isEmpty() ? Material.RED_STAINED_GLASS_PANE : icon;
    }

    public void setContents(ItemStack[] contents) {
        this.contents = contents;
    }

    public void setIcon(Material icon) {
        this.icon = icon;
    }

    public Material getDefaultIcon() {
        return Material.LIME_STAINED_GLASS_PANE;
    }

    public void resetIcon() {
        setIcon(getDefaultIcon());
    }

    public static Vault create(int index, UUID owner) {
        return new Vault(index, owner);
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        return new HashMap<>();
    }
}
