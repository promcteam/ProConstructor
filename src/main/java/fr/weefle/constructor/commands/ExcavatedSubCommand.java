package fr.weefle.constructor.commands;

import fr.weefle.constructor.citizens.BuilderTrait;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class ExcavatedSubCommand extends AbstractCommand {
    public ExcavatedSubCommand(@Nullable SchematicBuilderCommand parent) {
        super("excavated", parent);
        this.permission = "schematicbuilder.excavated";
        addAllowedSender(Player.class);
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

        if (builder.getState() == BuilderTrait.BuilderState.BUILDING && builder.isExcavate()) {
            if (!builder.ExcavateMaterials.isEmpty()) {
                Inventory inventory = ((Player) sender).getInventory();
                for (Map.Entry<Material, Integer> entry : builder.ExcavateMaterials.entrySet()) {
                    int total = entry.getValue();
                    while (total > 0) {
                        int amount = Math.min(total, 64);
                        inventory.addItem(new ItemStack(entry.getKey(), amount));
                        total -= amount;
                    }
                }
                builder.ExcavateMaterials.clear();
                sender.sendMessage(ChatColor.GREEN + npc.getName() + " gave you all excavated blocks");
            } else {
                sender.sendMessage(ChatColor.RED + npc.getName() + " has no excavated blocks");
            }
        } else {
            sender.sendMessage(ChatColor.RED + npc.getName() + " is not currently building");
        }
    }
}