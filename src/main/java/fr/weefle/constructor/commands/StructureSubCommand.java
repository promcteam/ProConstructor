package fr.weefle.constructor.commands;

import fr.weefle.constructor.API.StructureUtil;
import fr.weefle.constructor.extra.SelectionListener;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class StructureSubCommand extends AbstractCommand {
    public StructureSubCommand(@Nullable SchematicBuilderCommand parent) {
        super("structure", "Save named structure with WorldEdit region selection", parent);
        this.permission = "schematicbuilder.structure";
        addAllowedSender(Player.class);
    }

    @Override
    public List<String> getUsages(CommandSender sender) {
        List<String> list = new ArrayList<>();
        list.add('/'+getFullCommand()+" <name>");
        return list;
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull List<String> args) { // TODO bring back WorldEdit
        if (args.size() != 1) {
            sendUsage(sender);
            return;
        }
        Player player = (Player) sender;
        String name = args.get(0);
        Vector vec;

        try {
            // vec = WorldEditManager.getWorldEditSelection(p);
            // Bukkit.getLogger().warning(SelectionListener.vector.get(p).toString());
            vec = SelectionListener.vector.get(player);
            //Bukkit.getLogger().warning(vec.toString());
        } catch (NullPointerException e) {
            player.sendMessage(ChatColor.RED + "You have not selected any region!");
            return;
        }

        try {
            //NMS.getInstance().getStructure().save(WorldEditManager.getWorldEditLocation(p), vec, name);
            // Bukkit.getLogger().warning(SelectionListener.location.get(p).toString());
            StructureUtil.save(SelectionListener.location.get(player), vec, name);
        } catch (NullPointerException e) {
            player.sendMessage(ChatColor.RED + "You have not selected any region!");
            return;
        }
        player.sendMessage(ChatColor.GREEN + "Successfully saved structure " + ChatColor.WHITE + name + ChatColor.GREEN + " in schematics folder.");
    }
}
