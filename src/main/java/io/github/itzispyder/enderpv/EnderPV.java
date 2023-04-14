package io.github.itzispyder.enderpv;

import io.github.itzispyder.enderpv.commands.command.PlayerVaultCommand;
import io.github.itzispyder.enderpv.events.InventoryActionListener;
import io.github.itzispyder.enderpv.util.Text;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.logging.Logger;

public final class EnderPV extends JavaPlugin {

    public static final PluginManager pm = Bukkit.getPluginManager();
    public static final BukkitScheduler sch = Bukkit.getScheduler();
    public static final Logger log = Bukkit.getLogger();
    public static final String prefix = Text.color("&7[&6EnderPV&7] &r");
    public static final String hiddenPrefix = Text.color("&e&n&d&e&r&v&a&u&l&t&s&r");
    public static EnderPV instance;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        this.init();
        this.initConfig();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public void init() {
        // Events
        pm.registerEvents(new InventoryActionListener(),this);

        // Commands
        getCommand("enderpv").setExecutor(new PlayerVaultCommand());
        getCommand("enderpv").setTabCompleter(new PlayerVaultCommand());
    }

    public void initConfig() {
        this.getConfig().options().copyDefaults();
        this.saveDefaultConfig();
    }
}
