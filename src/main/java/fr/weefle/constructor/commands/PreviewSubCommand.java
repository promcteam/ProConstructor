package fr.weefle.constructor.commands;

import fr.weefle.constructor.SchematicBuilder;
import fr.weefle.constructor.hooks.citizens.BuilderTrait;
import fr.weefle.constructor.schematic.Schematic;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class PreviewSubCommand extends AbstractCommand { // TODO take schematic origin into account
    public PreviewSubCommand(@Nullable SchematicBuilderCommand parent) {
        super("preview", "Shows the preview of the current structure", parent);
        this.permission = "schematicbuilder.preview";
        addAllowedSender(Player.class);
    }

    @Override
    public List<String> getUsages(CommandSender sender) {
        List<String> list = new ArrayList<>();
        list.add('/'+getFullCommand()+" [ticks]");
        return list;
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull List<String> args) {
        BuilderTrait builder = getSelectedBuilder(sender);
        if (builder == null) {return;}
        int ticks;
        if (args.size() == 1) {
            try {
                ticks = Integer.parseInt(args.get(0));
            } catch (NumberFormatException e) {
                sender.sendMessage(args.get(0)+ChatColor.RED + " is not a valid number");
                return;
            }
        } else if (args.size() == 0) {
            ticks = 1000;
        } else {
            sendUsage(sender);
            return;
        }
        NPC      npc    = builder.getNPC();
        Player   player = (Player) sender;
        Location tmpLoc = npc.getEntity().getLocation();
        //Bukkit.getLogger().warning(tmpLoc.toString());

        switch (builder.getState()) {
            case IDLE: case COLLECTING: {
                Schematic schematic = builder.getSchematic();
                if (schematic == null) {
                    sender.sendMessage(ChatColor.RED + "No Schematic Loaded");
                    return;
                }
                schematic.preview(builder, (Player) sender, ticks);
                player.sendMessage(npc.getName() + ChatColor.GREEN + " loaded a preview of the current structure");
                break;
            }
            default: {
                player.sendMessage(npc.getName() + ChatColor.RED + " can't load a preview right now");
                break;
            }
        }
    }
}
