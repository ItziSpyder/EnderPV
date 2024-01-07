package io.github.itzispyder.enderpv;

import io.github.itzispyder.enderpv.commands.PlayerVaultCommand;
import io.github.itzispyder.enderpv.events.InventoryActionListener;
import io.github.itzispyder.pdk.Global;
import io.github.itzispyder.pdk.PDK;
import org.bukkit.plugin.java.JavaPlugin;

public final class EnderPV extends JavaPlugin {

    public static final String prefix = Global.instance.color("&7[&6EnderPV&7] &r");
    public static final String hiddenPrefix = Global.instance.color("&e&n&d&e&r&v&a&u&l&t&s&r");
    public static EnderPV instance;

    @Override
    public void onEnable() {
        PDK.init(this);
        instance = this;
        this.init();
        this.initConfig();
    }

    @Override
    public void onDisable() {

    }

    public void init() {
        // Events
        new InventoryActionListener().register();

        // Commands
        new PlayerVaultCommand().register();
    }

    public void initConfig() {
        this.getConfig().options().copyDefaults();
        this.saveDefaultConfig();
    }
}
