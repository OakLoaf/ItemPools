package org.lushplugins.itempools.command;

import org.lushplugins.itempools.ItemPools;
import org.lushplugins.itempools.pool.ItemPool;
import org.lushplugins.lushlib.command.Command;
import org.lushplugins.lushlib.command.SubCommand;
import org.lushplugins.lushlib.libraries.chatcolor.ChatColorHandler;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemPoolsCommand extends Command {

    public ItemPoolsCommand() {
        super("itempools");
        addSubCommand(new CreateSubCommand());
        addSubCommand(new DeleteSubCommand());
        addSubCommand(new EditSubCommand());
        addSubCommand(new ReloadSubCommand());
        addSubCommand(new ResetSubCommand());
        addSubCommand(new SaveSubCommand());
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull org.bukkit.command.Command command, @NotNull String label, @NotNull String[] args, @NotNull String[] fullArgs) {
        return true;
    }

    @Override
    public List<String> tabComplete(@NotNull CommandSender sender, @NotNull org.bukkit.command.Command command, @NotNull String label, @NotNull String[] args, @NotNull String[] fullArgs) {
        return List.of();
    }

    private static class CreateSubCommand extends SubCommand {

        public CreateSubCommand() {
            super("create");
        }

        @Override
        public boolean execute(@NotNull CommandSender sender, @NotNull org.bukkit.command.Command command, @NotNull String label, @NotNull String[] args, @NotNull String[] fullArgs) {
            if (!sender.hasPermission("itempools.create")) {
                ChatColorHandler.sendMessage(sender, "&cIncorrect permissions.");
                return true;
            }

            ChatColorHandler.sendMessage(sender, "Test message");
            return true;
        }
    }

    private static class DeleteSubCommand extends SubCommand {

        public DeleteSubCommand() {
            super("delete");
        }

        @Override
        public boolean execute(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command command, @NotNull String label, @NotNull String[] args, @NotNull String[] fullArgs) {
            if (!sender.hasPermission("itempools.delete")) {
                ChatColorHandler.sendMessage(sender, "&cIncorrect permissions.");
                return true;
            }

            if (args.length < 1) {
                ChatColorHandler.sendMessage(sender, "&cIncorrect arguments, try /itempools delete <pool_id>");
                return true;
            }

            ItemPools.getInstance().getItemPoolDataManager().deletePoolData(args[0]);

            return true;
        }
    }


    private static class EditSubCommand extends SubCommand {

        public EditSubCommand() {
            super("edit");
        }

        @Override
        public boolean execute(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command command, @NotNull String label, @NotNull String[] args, @NotNull String[] fullArgs) {
            if (!sender.hasPermission("itempools.edit")) {
                ChatColorHandler.sendMessage(sender, "&cIncorrect permissions.");
                return true;
            }

            return false;
        }
    }

    private static class ReloadSubCommand extends SubCommand {

        public ReloadSubCommand() {
            super("reload");
        }

        @Override
        public boolean execute(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command command, @NotNull String label, @NotNull String[] args, @NotNull String[] fullArgs) {
            if (!sender.hasPermission("itempools.reload")) {
                ChatColorHandler.sendMessage(sender, "&cIncorrect permissions.");
                return true;
            }

            ItemPools.getInstance().getConfigManager().reload();
            ItemPools.getInstance().getItemPoolConfigManager().reload();
            ChatColorHandler.sendMessage(sender, "&aReloaded ItemPools");
            return true;
        }
    }

    private static class ResetSubCommand extends SubCommand {

        public ResetSubCommand() {
            super("reset");
        }

        @Override
        public boolean execute(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command command, @NotNull String label, @NotNull String[] args, @NotNull String[] fullArgs) {
            if (!sender.hasPermission("itempools.reset")) {
                ChatColorHandler.sendMessage(sender, "&cIncorrect permissions.");
                return true;
            }

            if (args.length != 1) {
                ChatColorHandler.sendMessage(sender, "&cIncorrect usage, try: /itempools reset <pool>");
                return true;
            }

            String poolId = args[0];
            ItemPool itemPool = ItemPools.getInstance().getItemPoolManager().getItemPool(poolId);
            if (itemPool == null) {
                ChatColorHandler.sendMessage(sender, "&cThat is not a valid item pool");
                return  true;
            }

            itemPool.reset();
            ChatColorHandler.sendMessage(sender, "&aReset pool '" + poolId + "'");
            return true;
        }

        @Override
        public @Nullable List<String> tabComplete(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command command, @NotNull String label, @NotNull String[] args, @NotNull String[] fullArgs) {
            return ItemPools.getInstance().getItemPoolManager().getItemPools().stream().map(ItemPool::getId).toList();
        }
    }

    private static class SaveSubCommand extends SubCommand {

        public SaveSubCommand() {
            super("save");
        }

        @Override
        public boolean execute(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command command, @NotNull String label, @NotNull String[] args, @NotNull String[] fullArgs) {
            if (!sender.hasPermission("itempools.save")) {
                ChatColorHandler.sendMessage(sender, "&cIncorrect permissions.");
                return true;
            }

            if (args.length != 1) {
                ChatColorHandler.sendMessage(sender, "&cIncorrect usage, try: /itempools save <pool>");
                return true;
            }

            String poolId = args[0];
            ItemPool itemPool = ItemPools.getInstance().getItemPoolManager().getItemPool(poolId);
            if (itemPool == null) {
                ChatColorHandler.sendMessage(sender, "&cThat is not a valid item pool");
                return  true;
            }

            ItemPools.getInstance().getItemPoolDataManager().savePoolData(itemPool);
            ChatColorHandler.sendMessage(sender, "&aSaved pool '" + poolId + "'");
            return true;
        }

        @Override
        public @Nullable List<String> tabComplete(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command command, @NotNull String label, @NotNull String[] args, @NotNull String[] fullArgs) {
            return ItemPools.getInstance().getItemPoolManager().getItemPools().stream().map(ItemPool::getId).toList();
        }
    }
}
