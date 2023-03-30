package fr.weefle.constructor.NMS.Version_1_15_R1;

import org.bukkit.block.data.BlockData;

import fr.weefle.constructor.block.DataBuildBlock;
import net.minecraft.server.v1_15_R1.NBTTagCompound;

public class TileBuildBlock_1_15_R1 extends DataBuildBlock{
	
	public TileBuildBlock_1_15_R1(int x, int y, int z, BlockData data) {
		super(x, y, z, data);
	}

	public NBTTagCompound nbt = null;
	
}