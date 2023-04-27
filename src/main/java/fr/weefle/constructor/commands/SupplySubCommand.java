package fr.weefle.constructor.commands;

import fr.weefle.constructor.citizens.BuilderTrait;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SupplySubCommand extends AbstractCommand {
    public SupplySubCommand(@Nullable SchematicBuilderCommand parent) {
        super("supply", "Set whether the constructor needs to be supplied with materials before building", parent);
        this.permission = "schematicbuilder.supply";
    }

    @Override
    public List<String> getArguments(CommandSender sender) {
        List<String> list = new ArrayList<>();
        list.add("true");
        list.add("false");
        return list;
    }

    @Override
    public List<String> getUsages(CommandSender sender) {
        List<String> list = new ArrayList<>();
        list.add('/'+getFullCommand());
        list.add('/'+getFullCommand()+" true");
        list.add('/'+getFullCommand()+" false");
        return list;
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull List<String> args) {
        BuilderTrait builder = getSelectedBuilder(sender);
        if (builder == null) {return;}
        NPC npc = builder.getNPC();

        if (args.size() == 0) {
            sender.sendMessage(ChatColor.GOLD + npc.getName() + " currently does" + (builder.isRequireMaterials() ?
                    "" :
                    " NOT") + " need to be supplied with materials.");
            return;
        } else if (args.size() != 1) {
            sendUsage(sender);
            return;
        }

        boolean requireMaterials;
        try {
            requireMaterials = Boolean.parseBoolean(args.get(0));
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + args.get(0) + " is not a valid boolean");
            return;
        }
        builder.setRequireMaterials(requireMaterials);
        sender.sendMessage(ChatColor.GOLD + npc.getName() + " now does" + (requireMaterials ?
                "" :
                " NOT") + " need to be supplied with materials.");
    }
}
