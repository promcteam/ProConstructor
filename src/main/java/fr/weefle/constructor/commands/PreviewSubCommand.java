package fr.weefle.constructor.commands;

import fr.weefle.constructor.SchematicBuilder;
import fr.weefle.constructor.essentials.BuilderTrait;
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

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class PreviewSubCommand extends AbstractCommand {
    public PreviewSubCommand(@Nullable SchematicBuilderCommand parent) {
        super("preview", parent);
        this.permission = "schematicbuilder.preview";
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
        Player player = (Player) sender;
        Location tmpLoc = npc.getEntity().getLocation();
        //Bukkit.getLogger().warning(tmpLoc.toString());

        if (builder.State == BuilderTrait.BuilderState.idle) {
            if (builder.schematic != null) {
                HashMap<Location, BlockData> blocks = new HashMap<>();
                for (int x = 0; x < builder.schematic.width(); ++x) {
                    for (int y = 0; y < builder.schematic.height(); ++y) {
                        for (int z = 0; z < builder.schematic.length(); ++z) {
                            Location loc = tmpLoc.clone().add(x, y, z);
                            BlockData bdata = builder.schematic.Blocks[x][y][z].getMat();
                            blocks.put(loc, bdata);
                            player.sendBlockChange(loc, bdata);
                        }
                    }
                }

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        for (Location loc : blocks.keySet()) {
                            Objects.requireNonNull(Bukkit.getServer().getWorld(player.getWorld().getName())).getBlockAt(loc).getState().update();
                        }
                    }
                }.runTaskLater(SchematicBuilder.getInstance(), 100);

                        /*double time = 0.1;
                            scheduler = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Constructor.getInstance(), new Runnable() {
                                Iterator<Location> it = blocks.keySet().iterator();
                                @Override
                                public void run() {
                                    if(it.hasNext()) {
                                        Location loc = it.next();
                                        p.sendBlockChange(loc, blocks.get(loc));
                                    }else{
                                        Bukkit.getScheduler().cancelTask(scheduler);
                                        Bukkit.getScheduler().scheduleSyncDelayedTask(Constructor.getInstance(), new Runnable() {
                                            @Override
                                            public void run() {
                                                for (Location loc : blocks.keySet()) {

                                                    Objects.requireNonNull(Bukkit.getServer().getWorld(p.getWorld().getName())).getBlockAt(loc).getState().update();
                                                }
                                            }

                                        }, 100);
                                    }
                                }
                            }, 0, (long) 0.01);*/

                player.sendMessage(ChatColor.GREEN + npc.getName() + " loaded a preview of the current structure");
            }
        }
    }
}
