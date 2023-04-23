package fr.weefle.constructor.commands;

import fr.weefle.constructor.essentials.BuilderTrait;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class HoldSubCommand extends AbstractCommand {
    public HoldSubCommand(@Nullable SchematicBuilderCommand parent) {
        super("hold", parent);
        this.permission = "schematicbuilder.hold";
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull List<String> args) {
        BuilderTrait builder = getSelectedBuilder(sender);
        if (builder == null) {return;}
        NPC npc = builder.getNPC();

        if (args.size() == 0) {
            sender.sendMessage(ChatColor.GOLD + npc.getName() + " currently does" + (builder.HoldItems ?
                    "" :
                    " NOT") + " hold blocks.");
            return;
        } else if (args.size() != 1) {
            sendUsage(sender);
            return;
        }

        boolean holdItems;
        try {
            holdItems = Boolean.parseBoolean(args.get(0));
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + args.get(0) + " is not a valid boolean");
            return;
        }
        builder.HoldItems = holdItems;
        sender.sendMessage(ChatColor.GOLD + npc.getName() + " now does" + (holdItems ?
                "" :
                " NOT") + " hold blocks.");
    }
}
