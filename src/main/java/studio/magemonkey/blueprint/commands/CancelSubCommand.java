package studio.magemonkey.blueprint.commands;

import net.citizensnpcs.api.npc.NPC;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import studio.magemonkey.blueprint.Blueprint;
import studio.magemonkey.blueprint.hooks.citizens.BuilderTrait;

import java.util.List;

public class CancelSubCommand extends AbstractCommand {
    public CancelSubCommand(@Nullable SchematicBuilderCommand parent) {
        super("cancel", "Cancel building", parent);
        this.permission = "schematicbuilder.cancel";
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
        if (args.size() != 0) {
            sendUsage(sender);
            return;
        }
        NPC npc = builder.getNPC();
        if (builder.getState() != BuilderTrait.BuilderState.IDLE) {
            sender.sendMessage(Blueprint.format(Blueprint.getInstance().config().getCancelMessage(), npc,
                    builder.getSchematic(), sender, null, "0"));
        } else {
            sender.sendMessage(ChatColor.RED + npc.getName() + " is not building.");
        }

        builder.CancelBuild();
    }
}
