package me.dave.itempools.command;

import me.dave.platyutils.command.Command;
import me.dave.platyutils.command.SubCommand;
import me.dave.platyutils.libraries.chatcolor.ChatColorHandler;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ItemPoolsCommand extends Command {

    public ItemPoolsCommand() {
        super("itempools");
        addSubCommand(new CreateCommand());
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return true;
    }

    @Override
    public List<String> tabComplete(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return List.of();
    }

    private static class CreateCommand extends SubCommand {

        public CreateCommand() {
            super("create");
        }

        @Override
        public boolean execute(@NotNull CommandSender sender, @NotNull org.bukkit.command.Command command, @NotNull String label, @NotNull String[] args) {
            ChatColorHandler.sendMessage(sender, "Test message");
            return true;
        }

        @Override
        public List<String> tabComplete(@NotNull CommandSender sender, @NotNull org.bukkit.command.Command command, @NotNull String label, @NotNull String[] args) {
            if (args.length == 1) {
                return List.of("test1", "test2");
            }

            return List.of();
        }
    }
}
