package fr.weefle.constructor.commands;

import fr.weefle.constructor.hooks.citizens.BuilderTrait;
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
        super("excavated", "Get all excavated blocks from current build", parent);
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
        execute(builder, (Player) sender);
    }

    public static void execute(BuilderTrait builder, Player player) {
        NPC npc = builder.getNPC();

        if (!builder.ExcavateMaterials.isEmpty()) {
            Inventory inventory = player.getInventory();
            for (Map.Entry<Material, Integer> entry : builder.ExcavateMaterials.entrySet()) {
                int total = entry.getValue();
                while (total > 0) {
                    int amount = Math.min(total, 64);
                    inventory.addItem(new ItemStack(entry.getKey(), amount));
                    total -= amount;
                }
            }
            builder.ExcavateMaterials.clear();
            player.sendMessage(ChatColor.GREEN + npc.getName() + " gave you all excavated blocks");
        } else {
            player.sendMessage(ChatColor.RED + npc.getName() + " has no excavated blocks");
        }
    }
}