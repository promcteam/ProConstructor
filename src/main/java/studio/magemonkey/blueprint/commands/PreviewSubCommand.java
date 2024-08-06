package studio.magemonkey.blueprint.commands;

import studio.magemonkey.blueprint.hooks.citizens.BuilderTrait;
import studio.magemonkey.blueprint.schematic.Schematic;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class PreviewSubCommand extends AbstractCommand {
    public PreviewSubCommand(@Nullable SchematicBuilderCommand parent) {
        super("preview", "Shows the preview of the current structure", parent);
        this.permission = "schematicbuilder.preview";
        addAllowedSender(Player.class);
    }

    @Override
    public List<String> getUsages(CommandSender sender) {
        List<String> list = new ArrayList<>();
        list.add('/' + getFullCommand() + " [ticks]");
        return list;
    }

    @Override
    public void execute(@NotNull CommandSender sender,
                        @NotNull Command command,
                        @NotNull String label,
                        @NotNull List<String> args) {
        BuilderTrait builder = getSelectedBuilder(sender);
        if (builder == null) {
            return;
        }
        int ticks;
        if (args.size() == 1) {
            try {
                ticks = Integer.parseInt(args.get(0));
            } catch (NumberFormatException e) {
                sender.sendMessage(args.get(0) + ChatColor.RED + " is not a valid number");
                return;
            }
        } else if (args.size() == 0) {
            ticks = 1000;
        } else {
            sendUsage(sender);
            return;
        }
        execute(builder, sender, ticks);
    }

    public static boolean execute(BuilderTrait builder, CommandSender sender, int ticks) {
        switch (builder.getState()) {
            case IDLE:
            case COLLECTING: {
                Schematic schematic = builder.getSchematic();
                if (schematic == null) {
                    sender.sendMessage(ChatColor.RED + "No Schematic Loaded");
                    return false;
                }
                schematic.preview(builder, (Player) sender, ticks);
                sender.sendMessage(
                        builder.getNPC().getName() + ChatColor.GREEN + " loaded a preview of the current structure");
                return true;
            }
            default: {
                sender.sendMessage(builder.getNPC().getName() + ChatColor.RED + " can't load a preview right now");
                return false;
            }
        }
    }

    public static boolean execute(BuilderTrait builder, CommandSender sender) {
        return execute(builder, sender, 1000);
    }
}
