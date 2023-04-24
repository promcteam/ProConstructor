package fr.weefle.constructor.commands;

import fr.weefle.constructor.SchematicBuilder;
import fr.weefle.constructor.essentials.BuilderTrait;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CancelSubCommand extends AbstractCommand {
    public CancelSubCommand(@Nullable SchematicBuilderCommand parent) {
        super("cancel", parent);
        this.permission = "schematicbuilder.cancel";
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull List<String> args) {
        BuilderTrait builder = getSelectedBuilder(sender);
        if (builder == null) {return;}
        if (args.size() != 0) {
            sendUsage(sender);
            return;
        }
        NPC npc = builder.getNPC();
        if (builder.State != BuilderTrait.BuilderState.idle) {
            sender.sendMessage(SchematicBuilder.format(SchematicBuilder.getInstance().config().getCancelMessage(), npc,
                    builder.schematic, sender, null, "0"));
        } else {
            sender.sendMessage(ChatColor.RED + npc.getName() + " is not building.");
        }

        builder.CancelBuild();
    }
}
