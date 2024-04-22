package me.dave.itempools.command;

import me.dave.itempools.ItemPools;
import me.dave.itempools.config.ItemPoolConfigManager;
import me.dave.itempools.pool.ItemPool;
import me.dave.itempools.pool.ItemPoolManager;
import me.dave.lushlib.command.Command;
import me.dave.lushlib.command.SubCommand;
import me.dave.lushlib.libraries.chatcolor.ChatColorHandler;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class ItemPoolsCommand extends Command {

    public ItemPoolsCommand() {
        super("itempools");
        addSubCommand(new CreateSubCommand());
        addSubCommand(new EditSubCommand());
        addSubCommand(new ReloadSubCommand());
        addSubCommand(new ResetSubCommand());
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return true;
    }

    @Override
    public List<String> tabComplete(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return List.of();
    }

    private static class CreateSubCommand extends SubCommand {

        public CreateSubCommand() {
            super("create");
        }

        @Override
        public boolean execute(@NotNull CommandSender sender, @NotNull org.bukkit.command.Command command, @NotNull String label, @NotNull String[] args) {
            if (!sender.hasPermission("itempools.create")) {
                ChatColorHandler.sendMessage(sender, "&cIncorrect permissions.");
                return true;
            }

            ChatColorHandler.sendMessage(sender, "Test message");
            return true;
        }
    }

    private static class EditSubCommand extends SubCommand {

        public EditSubCommand() {
            super("edit");
        }

        @Override
        public boolean execute(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command command, @NotNull String label, @NotNull String[] args) {
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
        public boolean execute(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command command, @NotNull String label, @NotNull String[] args) {
            if (!sender.hasPermission("itempools.reload")) {
                ChatColorHandler.sendMessage(sender, "&cIncorrect permissions.");
                return true;
            }

            ItemPools.getInstance().getManager(ItemPoolConfigManager.class).ifPresent(ItemPoolConfigManager::reload);
            ChatColorHandler.sendMessage(sender, "&aReloaded ItemPools");
            return true;
        }
    }

    private static class ResetSubCommand extends SubCommand {

        public ResetSubCommand() {
            super("reset");
        }

        @Override
        public boolean execute(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command command, @NotNull String label, @NotNull String[] args) {
            if (!sender.hasPermission("itempools.reset")) {
                ChatColorHandler.sendMessage(sender, "&cIncorrect permissions.");
                return true;
            }

            if (args.length != 1) {
                ChatColorHandler.sendMessage(sender, "&cIncorrect usage, try: /itempools reset <pool>");
                return true;
            }

            String poolId = args[0];
            Optional<ItemPoolManager> optionalManager = ItemPools.getInstance().getManager(ItemPoolManager.class);
            if (optionalManager.isEmpty()) {
                ChatColorHandler.sendMessage(sender, "&cIncorrect usage, try: /itempools reset <pool>");
                return  true;
            }

            ItemPoolManager itemPoolManager = optionalManager.get();
            ItemPool itemPool = itemPoolManager.getItemPool(poolId);
            if (itemPool == null) {
                ChatColorHandler.sendMessage(sender, "&cThat is not a valid item pool");
                return  true;
            }

            itemPool.reset();
            ChatColorHandler.sendMessage(sender, "&aReset pool '" + poolId + "'");
            return true;
        }

        @Override
        public @Nullable List<String> tabComplete(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command command, @NotNull String label, @NotNull String[] args) {
            Optional<ItemPoolManager> optionalManager = ItemPools.getInstance().getManager(ItemPoolManager.class);
            if (optionalManager.isPresent()) {
                ItemPoolManager itemPoolManager = optionalManager.get();
                return itemPoolManager.getItemPools().stream().map(ItemPool::getId).toList();
            }

            return null;
        }
    }
}
