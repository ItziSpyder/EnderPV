package io.github.itzispyder.enderpv.data;

import io.github.itzispyder.enderpv.util.VaultUtils;
import io.github.itzispyder.pdk.Global;
import io.github.itzispyder.pdk.plugin.builders.ItemBuilder;
import io.github.itzispyder.pdk.utils.FileValidationUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.*;
import java.util.function.Consumer;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import static io.github.itzispyder.enderpv.EnderPV.hiddenPrefix;

public class VaultProfile implements Serializable, ConfigurationSerializable, Global {

    public static final Set<UUID> opened = new HashSet<>();
    private final UUID owner;
    private final List<Vault> vaults;

    private VaultProfile(UUID owner) {
        this.owner = owner;
        this.vaults = new ArrayList<>();

        for (int i = 0; i < 54; i++) vaults.add(Vault.create(i,owner));
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

    @SuppressWarnings("all")
    public Inventory getGui() {
        Inventory inv = Bukkit.createInventory(null,54, hiddenPrefix + player().getName() + "'s Vaults");
        ItemStack x = new ItemBuilder()
                .material(Material.LIGHT_GRAY_STAINED_GLASS_PANE)
                .name(color("&7Inaccessible Vault"))
                .build();

        for (int i = 0; i < VaultUtils.getMaxVaults(player().getPlayer()); i++) {
            Vault v = getVault(i);
            if (v == null) continue;
            inv.setItem(i, new ItemBuilder()
                    .material(v.getIcon())
                    .name(color("&6Vault &7#" + (i + 1)))
                    .lore(v.getStatus())
                    .lore("")
                    .lore(color("&eʀɪɢʜᴛ ᴄʟɪᴄᴋ -ᴄʜᴀɴɢᴇ ɪᴄᴏɴ"))
                    .lore(color("&eʟᴇғᴛ ᴄʟɪᴄᴋ -ᴀᴅᴅ ᴛᴏ ᴠᴀᴜʟᴛ"))
                    .flag(
                            ItemFlag.HIDE_ENCHANTS,
                            ItemFlag.HIDE_ATTRIBUTES,
                            ItemFlag.HIDE_DESTROYS,
                            ItemFlag.HIDE_DYE,
                            ItemFlag.HIDE_UNBREAKABLE,
                            ItemFlag.HIDE_PLACED_ON,
                            ItemFlag.HIDE_POTION_EFFECTS
                    )
                    .build());
        }

        while (inv.firstEmpty() != -1) inv.setItem(inv.firstEmpty(), x);
        return inv;
    }

    public void update(int index, ItemStack[] contents) {
        getVault(index).setContents(contents);
    }

    public UUID getOwner() {
        return owner;
    }

    public static boolean isAlreadyViewing(UUID id) {
        return opened.contains(id);
    }

    public static void setAlreadyViewing(UUID id, boolean viewing) {
        if (id == null) {
            return;
        }

        if (viewing) {
            opened.add(id);
        }
        else {
            opened.remove(id);
        }
    }

    public List<Vault> getVaults() {
        return vaults;
    }

    public void openForOwner() {
        this.ifOwnerOnlineRun(p -> {
            UUID id = Bukkit.getPlayerUniqueId(p.getName());
            if (isAlreadyViewing(id)) {
                error(p, "Cannot view vault profile because the menu is already open for you.");
                return;
            }
            p.openInventory(this.getGui());
        });
    }

    public void closeForOwner() {
        setAlreadyViewing(getOwner(), false);
    }

    /**
     *
     * @param index should be an integer between 0 and 53
     * @return vault
     */
    public Vault getVault(int index) {
        index = validateIndex(index);
        return vaults.get(index);
    }

    public void save() {
        String path = "plugins/EnderPV/vaults/" + owner + ".profile";
        File file = new File(path);
        if (!FileValidationUtils.validate(file)) return;
        try {
            FileOutputStream fos = new FileOutputStream(file);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            GZIPOutputStream gos = new GZIPOutputStream(bos);
            ObjectOutputStream oos = new ObjectOutputStream(gos);
            BukkitObjectOutputStream boos = new BukkitObjectOutputStream(oos);

            boos.writeObject(this);
            boos.close();
        }
        catch (Exception ex) {
            Bukkit.getLogger().warning("cannot save vault file '" + file.getPath() + "'");
        }
    }

    public static VaultProfile load(UUID owner) {
        String path = "plugins/EnderPV/vaults/" + owner + ".profile";
        File file = new File(path);
        if (!FileValidationUtils.validate(file)) return new VaultProfile(owner);
        try {
            FileInputStream fis = new FileInputStream(file);
            BufferedInputStream bis = new BufferedInputStream(fis);
            GZIPInputStream gis = new GZIPInputStream(bis);
            ObjectInputStream ois = new ObjectInputStream(gis);
            BukkitObjectInputStream bois = new BukkitObjectInputStream(ois);

            VaultProfile read = (VaultProfile) bois.readObject();
            bois.close();
            return read;
        }
        catch (Exception ex) {
            return new VaultProfile(owner);
        }
    }

    private int validateIndex(int index) {
        return Math.max(0, Math.min(53, index));
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        return new HashMap<>();
    }
}
