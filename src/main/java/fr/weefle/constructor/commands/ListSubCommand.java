package fr.weefle.constructor.commands;

import fr.weefle.constructor.menu.menus.SchematicMenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ListSubCommand extends AbstractCommand {
    public ListSubCommand(@Nullable SchematicBuilderCommand parent) {
        super("list", "Shows the list of all schematics and structures", parent);
        this.permission = "schematicbuilder.list";
        addAllowedSender(Player.class);
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull List<String> args) {
        if (args.size() != 0) {
            sendUsage(sender);
            return;
        }
        new SchematicMenu((Player) sender).open();
    }
}
