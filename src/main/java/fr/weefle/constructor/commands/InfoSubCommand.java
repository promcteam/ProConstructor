package fr.weefle.constructor.commands;

import fr.weefle.constructor.essentials.BuilderTrait;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class InfoSubCommand extends AbstractCommand {
    public InfoSubCommand(@Nullable SchematicBuilderCommand parent) {
        super("info", parent);
        this.permission = "schematicbuilder.info";
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull List<String> args) {
        BuilderTrait builder = getSelectedBuilder(sender);
        if (builder == null) {return;}
        NPC npc = builder.getNPC();

        if (args.size() != 0) {
            sendUsage(sender);
            return;
        }

        sender.sendMessage(ChatColor.GOLD + "----- Builder Info for " + npc.getName() + '<' + npc.getId() + "> ------");

        //	DecimalFormat df=  new DecimalFormat("#");

        if (builder.schematic != null) {
            sender.sendMessage(ChatColor.GREEN + "Schematic: " + builder.schematic.GetInfo());
        } else {
            sender.sendMessage(ChatColor.YELLOW + "No schematic loaded.");
        }

        if (builder.Origin == null) {
            sender.sendMessage(ChatColor.GREEN + "Origin: " + ChatColor.WHITE + "My Location");
        } else {
            sender.sendMessage(ChatColor.GREEN + "Origin: " + ChatColor.WHITE + " x:" + builder.Origin.getBlockX() + " y:" + builder.Origin.getBlockY() + " z:" + builder.Origin.getBlockZ());
        }

        sender.sendMessage(ChatColor.GREEN + "Status: " + ChatColor.WHITE + builder.State + " Timeout: " + builder.MoveTimeout);
        sender.sendMessage(ChatColor.GREEN + "Require Mats: " + ChatColor.WHITE + builder.RequireMaterials + " Hold Items: " + builder.HoldItems);

        if (builder.State == BuilderTrait.BuilderState.building) {
            sender.sendMessage(ChatColor.BLUE + "Location: " + ChatColor.WHITE + " x:" + builder.ContinueLoc.getBlockX() + " y:" + builder.ContinueLoc.getBlockY() + " z:" + builder.ContinueLoc.getBlockZ());
            sender.sendMessage(ChatColor.BLUE + "Build Pattern XZ: " + ChatColor.WHITE + builder.BuildPatternXY + ChatColor.BLUE + " Build Y Layers: " + ChatColor.WHITE + builder.BuildYLayers);
            sender.sendMessage(ChatColor.BLUE + "Ignore Air: " + ChatColor.WHITE + builder.IgnoreAir + ChatColor.BLUE + "  Ignore Liquid: " + ChatColor.WHITE + builder.IgnoreLiquid);
            sender.sendMessage(ChatColor.BLUE + "Hold Items: " + ChatColor.WHITE + builder.HoldItems + ChatColor.BLUE + "  Excavate: " + ChatColor.WHITE + builder.Excavate);
            sender.sendMessage(ChatColor.BLUE + "On Complete: " + ChatColor.WHITE + builder.oncomplete + ChatColor.BLUE + "  On Cancel: " + ChatColor.WHITE + builder.oncancel);
            long c = builder.startingcount;
            sender.sendMessage(ChatColor.BLUE + "Blocks: Total: " + ChatColor.WHITE + c + ChatColor.BLUE + "  Remaining: " + ChatColor.WHITE + builder.Q.size());
            double percent = ((double) (c - builder.Q.size()) / (double) c) * 100;
            sender.sendMessage(ChatColor.BLUE + "Complete: " + ChatColor.WHITE + String.format("%1$.1f", percent) + "%");
        }
    }

}
