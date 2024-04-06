package studio.magemonkey.blueprint.schematic.blocks;

import org.bukkit.block.data.BlockData;

public class TileBuildBlock extends DataBuildBlock {
    private final Object nbt;

    public TileBuildBlock(int x, int y, int z, BlockData data, Object nbt) {
        super(x, y, z, data);
        this.nbt = nbt;
    }

    public Object getNBT() {return nbt;}
}