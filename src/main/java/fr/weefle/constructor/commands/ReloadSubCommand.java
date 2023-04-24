package fr.weefle.constructor.commands;

import fr.weefle.constructor.SchematicBuilder;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ReloadSubCommand extends AbstractCommand {
    public ReloadSubCommand(@Nullable SchematicBuilderCommand parent) {
        super("reload", parent);
        this.permission = "schematicbuilder.reload";
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull List<String> args) {
        if (args.size() != 0) {
            sendUsage(sender);
            return;
        }
        SchematicBuilder.getInstance().config().reload();
        sender.sendMessage(ChatColor.GREEN + "Reloaded ProSchematicBuilder/config.yml");
    }
}
