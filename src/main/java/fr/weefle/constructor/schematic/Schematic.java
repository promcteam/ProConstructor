package fr.weefle.constructor.schematic;

import fr.weefle.constructor.SchematicBuilder;
import fr.weefle.constructor.hooks.citizens.BuilderTrait;
import fr.weefle.constructor.schematic.blocks.DataBuildBlock;
import fr.weefle.constructor.schematic.blocks.EmptyBuildBlock;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public abstract class Schematic {
    private final String path;
    private final String displayName;

    public Schematic(@NotNull String path, @Nullable String displayName) {
        this.path = path;
        this.displayName = displayName;
    }

    @NotNull
    public String getPath() {return path;}

    @NotNull
    public String getDisplayName() {return displayName == null ? path : displayName;}

    @Nullable
    public abstract Vector getAbsolutePosition();

    public abstract int getWidth();

    public abstract int getHeight();

    public abstract int getLength();

    @NotNull
    public abstract EmptyBuildBlock getBlockAt(int x, int y, int z);

    public abstract Location offset(Location origin, int x, int y, int z, int emptyLayers);

    public Location offset(Location origin, int x, int y, int z) {return offset(origin, x, y, z, 0);}

    @NotNull
    public abstract Map<Material, Integer> getMaterials();

    public Queue<EmptyBuildBlock> createMarks(Material mat) {
        Queue<EmptyBuildBlock> queue = new LinkedList<>();
        queue.add(new DataBuildBlock(0, 0, 0, mat.createBlockData()));
        queue.add(new DataBuildBlock(getWidth() - 1, 0, 0, mat.createBlockData()));
        queue.add(new DataBuildBlock(0, 0, (int) getLength() - 1, mat.createBlockData()));
        queue.add(new DataBuildBlock(getWidth() - 1, 0, getLength() - 1, mat.createBlockData()));
        return queue;
    }

    public void preview(BuilderTrait builder, Player player, int ticks) {
        Location               origin = builder.getOrigin();
        Queue<EmptyBuildBlock> queue  = buildQueue(builder);
        for (EmptyBuildBlock block : queue) {
            player.sendBlockChange(offset(origin, block.X, block.Y, block.Z), block.getMat());
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                World world = builder.getNPC().getEntity().getWorld();
                for (EmptyBuildBlock block : queue) {
                    world.getBlockAt(offset(origin, block.X, block.Y, block.Z)).getState().update();
                }
            }
        }.runTaskLater(SchematicBuilder.getInstance(), ticks);
    }

    @NotNull
    public abstract Queue<EmptyBuildBlock> buildQueue(BuilderTrait builder);

    public String getInfo() {
        return ChatColor.GREEN + "Path: " + ChatColor.WHITE + getPath() +
                ChatColor.GREEN + ", Name: " + ChatColor.WHITE + getDisplayName() +
                ChatColor.GREEN + ", Size: " + ChatColor.WHITE + getWidth() + " wide, " + getLength() + " long, " + getHeight() + " tall";
    }
}
