package fr.weefle.constructor.NMS.Version_1_13_R2;

import org.bukkit.block.data.BlockData;

import fr.weefle.constructor.block.DataBuildBlock;
import net.minecraft.server.v1_13_R2.NBTTagCompound;

public class TileBuildBlock_1_13_R2 extends DataBuildBlock{
	
	public TileBuildBlock_1_13_R2(int x, int y, int z, BlockData data) {
		super(x, y, z, data);
	}

	public NBTTagCompound nbt = null;
	
}