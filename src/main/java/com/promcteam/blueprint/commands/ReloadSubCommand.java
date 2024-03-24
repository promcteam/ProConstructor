package com.promcteam.blueprint.commands;

import com.promcteam.blueprint.Blueprint;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ReloadSubCommand extends AbstractCommand {
    public ReloadSubCommand(@Nullable SchematicBuilderCommand parent) {
        super("reload", "Reload the config.yml", parent);
        this.permission = "schematicbuilder.reload";
    }

    @Override
    public void execute(@NotNull CommandSender sender,
                        @NotNull Command command,
                        @NotNull String label,
                        @NotNull List<String> args) {
        if (args.size() != 0) {
            sendUsage(sender);
            return;
        }
        Blueprint.getInstance().reload();
        sender.sendMessage(ChatColor.GREEN + "Reloaded Blueprint");
    }
}
