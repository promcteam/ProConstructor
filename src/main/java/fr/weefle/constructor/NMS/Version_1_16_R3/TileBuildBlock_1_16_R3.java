package fr.weefle.constructor.NMS.Version_1_16_R3;

import fr.weefle.constructor.block.DataBuildBlock;
import net.minecraft.server.v1_16_R3.NBTTagCompound;
import org.bukkit.block.data.BlockData;

public class TileBuildBlock_1_16_R3 extends DataBuildBlock{
	
	public TileBuildBlock_1_16_R3(int x, int y, int z, BlockData data) {
		super(x, y, z, data);
	}

	public NBTTagCompound nbt = null;
	
}