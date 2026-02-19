package org.lushplugins.itempools.command;

import org.bukkit.command.CommandSender;
import org.lushplugins.itempools.ItemPools;
import org.lushplugins.itempools.pool.ItemPool;
import org.lushplugins.lushlib.libraries.chatcolor.ChatColorHandler;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@Command("itempools")
@SuppressWarnings("unused")
public class ItemPoolsCommand {

    @Subcommand("reload")
    @CommandPermission("itempools.reload")
    public void reload(CommandSender sender) {
        ItemPools.getInstance().getConfigManager().reload();
        ItemPools.getInstance().getItemPoolConfigManager().reload();
        ChatColorHandler.sendMessage(sender, "&aReloaded ItemPools");
    }

    @Subcommand("create")
    @CommandPermission("itempools.modify")
    public void create() {}

    @Subcommand("edit")
    @CommandPermission("itempools.modify")
    public void edit() {}

    @Subcommand("delete")
    @CommandPermission("itempools.modify")
    public void delete() {}

    @Subcommand("reset")
    @CommandPermission("itempools.reset")
    public void reset(CommandSender sender, ItemPool itemPool) {
        itemPool.reset();
        ChatColorHandler.sendMessage(sender, "&aReset pool '" + itemPool.getId() + "'");
    }

    @Subcommand("save")
    @CommandPermission("itempools.save")
    public void save(CommandSender sender, ItemPool itemPool) {
        ItemPools.getInstance().getItemPoolDataManager().savePoolData(itemPool);
        ChatColorHandler.sendMessage(sender, "&aSaved pool '" + itemPool.getId() + "'");
    }
}
