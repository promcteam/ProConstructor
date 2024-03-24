package com.promcteam.blueprint.schematic.blocks;

import org.bukkit.Material;
import org.bukkit.block.data.BlockData;

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
        return Material.AIR.createBlockData();
    }
}