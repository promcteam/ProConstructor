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
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public abstract class Schematic {
    protected final String path;
    protected String displayName;
    private Runnable   hidePreviewRunnable;
    private BukkitTask hidePreviewTask;

    public Schematic(Path path) {this.path = path.toString();}

    @NotNull
    public String getPath() {return path;}

    @NotNull
    public String getDisplayName() {return displayName == null ? new File(path).getName() : displayName;}

    @Nullable
    public abstract Vector getAbsolutePosition();

    public abstract int getWidth();

    public abstract int getHeight();

    public abstract int getLength();

    @NotNull
    public abstract EmptyBuildBlock getBlockAt(int x, int y, int z);

    public abstract Location offset(Location origin, int x, int y, int z, int emptyLayers, int rotation);

    @NotNull
    public abstract Map<Material, Integer> getMaterials();

    public void hidePreview() {
        if (this.hidePreviewRunnable != null) {
            this.hidePreviewRunnable.run();
            this.hidePreviewRunnable = null;

            if (!this.hidePreviewTask.isCancelled()) {
                this.hidePreviewTask.cancel();
                this.hidePreviewTask = null;
            }
        }
    }

    public void preview(BuilderTrait builder, Player player, int ticks) {
        this.hidePreview();

        Location               origin = builder.getOrigin();
        Queue<EmptyBuildBlock> queue  = buildQueue(builder);
        for (EmptyBuildBlock block : queue) {
            player.sendBlockChange(offset(origin, block.X, block.Y, block.Z, 0, builder.getRotation()), block.getMat());
        }

        this.hidePreviewRunnable = () -> {
            World world = builder.getNPC().getEntity().getWorld();
            for (EmptyBuildBlock block : queue) {
                Location location = offset(origin, block.X, block.Y, block.Z, 0, builder.getRotation());
                player.sendBlockChange(location, world.getBlockData(location));
                //world.getBlockAt(offset(origin, block.X, block.Y, block.Z, 0, builder.getRotation())).getState().update();
            }
        };

        this.hidePreviewTask = new BukkitRunnable() {
            @Override
            public void run() {hidePreview();}
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
