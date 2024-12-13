package studio.magemonkey.blueprint.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import studio.magemonkey.blueprint.menus.BuilderListMenu;

import java.util.List;

public class NPCSubCommand extends AbstractCommand {
    public NPCSubCommand(@Nullable SchematicBuilderCommand parent) {
        super("npc", "Select the NPC which will build something for you", parent);
        this.permission = "schematicbuilder.npc";
        addAllowedSender(Player.class);
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
        new BuilderListMenu((Player) sender).open();
    }
}
