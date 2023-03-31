package fr.weefle.constructor.block;

import fr.weefle.constructor.nbt.Tag;
import org.bukkit.block.data.BlockData;

public class EntityMap extends DataBuildBlock {

    public EntityMap(int x, int y, int z, BlockData data) {
        super(x, y, z, data);
    }

    public Tag nbt = null;

}
