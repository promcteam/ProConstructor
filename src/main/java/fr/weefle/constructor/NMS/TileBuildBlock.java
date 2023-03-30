package fr.weefle.constructor.NMS;

import fr.weefle.constructor.block.DataBuildBlock;
import org.bukkit.block.data.BlockData;

public class TileBuildBlock extends DataBuildBlock {

    public TileBuildBlock(int x, int y, int z, BlockData data) {
        super(x, y, z, data);
    }

    public Object nbt = null;

}