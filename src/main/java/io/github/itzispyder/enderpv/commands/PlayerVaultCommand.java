package io.github.itzispyder.enderpv.commands;

import io.github.itzispyder.enderpv.data.Vault;
import io.github.itzispyder.enderpv.data.VaultProfile;
import io.github.itzispyder.enderpv.util.VaultUtils;
import io.github.itzispyder.pdk.commands.Args;
import io.github.itzispyder.pdk.commands.CommandRegistry;
import io.github.itzispyder.pdk.commands.CustomCommand;
import io.github.itzispyder.pdk.commands.completions.CompletionBuilder;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

import static io.github.itzispyder.enderpv.EnderPV.prefix;

@CommandRegistry(value = "enderpv", usage = "/enderpv <index: integer>", playersOnly = true, printStackTrace = true)
public class PlayerVaultCommand implements CustomCommand {

    @Override
    public void dispatchCommand(CommandSender sender, Args args) {
        Player p = (Player) sender;
        UUID id = Bukkit.getPlayerUniqueId(p.getName());
        VaultProfile profile = VaultProfile.load(id);

        if (args.getSize() == 0) {
            profile.openForOwner();
            return;
        }

        if (args.getSize() == 1) {
            int index = Math.min(args.get(0).toInt(), VaultUtils.getMaxVaults(p)) - 1;
            Vault v = profile.getVault(index);
            v.openForOwner();
            return;
        }
        if (!p.hasPermission("enderpv.commands.enderpv.viewall")) {
            error(sender, prefix + "&cInsufficient permissions!");
            return;
        }

        int index = args.get(0).toInt() - 1;
        profile = VaultProfile.load(Bukkit.getPlayerUniqueId(args.get(1).toString()));
        Vault v = profile.getVault(index);
        p.openInventory(v.getGui(false));
    }

    @Override
    public void dispatchCompletions(CompletionBuilder b) {
        String[] indexes = new String[54];
        for (int i = 0; i < 54; i++)
            indexes[i] = String.valueOf(i + 1);

        b.then(b.arg(indexes)
                .then(b.arg(Bukkit.getOnlinePlayers(), Player::getName)));
    }
}
