package fr.weefle.constructor.schematic.blocks;

import fr.weefle.constructor.nbt.Tag;
import org.bukkit.block.data.BlockData;

public class EntityMap extends DataBuildBlock {
    private final Tag nbt;

    public EntityMap(int x, int y, int z, BlockData data, Tag nbt) {
        super(x, y, z, data);
        this.nbt = nbt;
    }

    public Tag getNBT() {return nbt;}
}
