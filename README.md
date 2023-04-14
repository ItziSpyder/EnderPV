<h1>
  <image src="/assets/image/icon.png" style="width: 15%;" />
  EnderPV
</h1>

##### Ender chests are now expanded by a lot! 
##### With large storage 54 vaults and each of them being capable of saving up to 54 items, you can now venture of without worrying about your tiny inventory!

- Expands your ender chest by having them store as vaults.
- 54 vaults per player
- 54 items per vault
- Admins can do `/pv <index> <player>` to manage other players' vaults
- Members can do `/pv <index>` to open up their own vaults!

# Commands
| Command  | Usage                        | Description                                | Permission                       | Aliases                        |
|----------|------------------------------|--------------------------------------------|----------------------------------|--------------------------------|
| /enderpv | /enderpv                     | Opens up player vault panel                | default                          | /pv, /endervault, /playervault |
| /enderpv | /enderpv `<index>`           | Opens up player vault from index           | default                          | /pv, /endervault, /playervault |
| /enderpv | /enderpv `<index>` `<player>`| Opens up another player's vault from index | enderpv.commands.enderpv.viewall | /pv, /endervault, /playervault |

# Side-Features
- If you use a permissions plugin like [LuckPerms](https://luckperms.net/download), you would be able to limit a player's access to vault indexes by creating a permission and adding it to the player with the following format: `enderpv.commands.enderpv.<max-index-number>`.
