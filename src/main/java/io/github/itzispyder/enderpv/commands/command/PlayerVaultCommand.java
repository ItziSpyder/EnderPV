package io.github.itzispyder.enderpv.commands.command;

import io.github.itzispyder.enderpv.commands.CmdExHandler;
import io.github.itzispyder.enderpv.commands.TabComplBuilder;
import io.github.itzispyder.enderpv.data.Vault;
import io.github.itzispyder.enderpv.data.VaultProfile;
import io.github.itzispyder.enderpv.util.ArrayUtils;
import io.github.itzispyder.enderpv.util.Text;
import io.github.itzispyder.enderpv.util.VaultUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.List;

import static io.github.itzispyder.enderpv.EnderPV.prefix;

public class PlayerVaultCommand implements TabExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        try {
            Player p = (Player) sender;
            VaultProfile profile = VaultProfile.load(p.getUniqueId());

            if (args.length == 0) {
                p.openInventory(profile.getGui());
                return true;
            }

            if (args.length == 1) {
                int index = Math.min(Integer.parseInt(args[0]), VaultUtils.getMaxVaults(p)) - 1;
                Vault v = profile.getVault(index);
                p.openInventory(v.getGui(true));
                return true;
            }
            if (!p.hasPermission("enderpv.commands.enderpv.viewall")) {
                p.sendMessage(Text.color(prefix + "&cInsufficient permissions!"));
                return true;
            }

            int index = Integer.parseInt(args[0]) - 1;
            profile = VaultProfile.load(Bukkit.getOfflinePlayer(args[1]).getUniqueId());
            Vault v = profile.getVault(index);
            p.openInventory(v.getGui(false));
        }
        catch (Exception ex) {
            CmdExHandler handler = new CmdExHandler(ex,command);
            sender.sendMessage(handler.getHelp());
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return new TabComplBuilder(sender,command,label,args)
                .add(1, new String[]{
                        "<index>"
                })
                .add(2, ArrayUtils.toNewList(Bukkit.getOnlinePlayers(),Player::getName), sender.hasPermission("enderpv.commands.enderpv.viewall"))
                .build();
    }
}
