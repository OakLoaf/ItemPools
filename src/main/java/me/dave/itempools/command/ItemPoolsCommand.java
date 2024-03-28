package me.dave.itempools.command;

import me.dave.lushlib.command.Command;
import me.dave.lushlib.command.SubCommand;
import me.dave.lushlib.libraries.chatcolor.ChatColorHandler;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class ItemPoolsCommand extends Command {

    public ItemPoolsCommand() {
        super("itempools");
        addSubCommand(new CreateSubCommand());
        addSubCommand(new EditSubCommand());
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
            ChatColorHandler.sendMessage(sender, "Test message");
            return true;
        }

        @Override
        public List<String> tabComplete(@NotNull CommandSender sender, @NotNull org.bukkit.command.Command command, @NotNull String label, @NotNull String[] args) {
            if (args.length == 1) {
                return List.of("test1", "test2");
            }

            return Collections.emptyList();
        }
    }

    private static class EditSubCommand extends SubCommand {

        public EditSubCommand() {
            super("edit");
        }

        @Override
        public boolean execute(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command command, @NotNull String label, @NotNull String[] args) {
            return false;
        }
    }
}
