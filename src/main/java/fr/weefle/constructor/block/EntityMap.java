package fr.weefle.constructor.block;

import org.bukkit.block.data.BlockData;

import fr.weefle.constructor.nbt.Tag;

public class EntityMap extends DataBuildBlock{
	
	public EntityMap(int x, int y, int z, BlockData data) {
		super(x, y, z, data);
	}

	public  Tag nbt = null;
	
}
