package fr.weefle.constructor.block;

import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.material.MaterialData;

public class EmptyBuildBlock {
    public int X, Y, Z;

    public EmptyBuildBlock() {
    }

    public EmptyBuildBlock(int x, int y, int z) {
        this.X = x;
        this.Y = y;
        this.Z = z;
    }

    public BlockData getMat() {
        return new MaterialData(Material.AIR).getItemType().createBlockData();
    }
}