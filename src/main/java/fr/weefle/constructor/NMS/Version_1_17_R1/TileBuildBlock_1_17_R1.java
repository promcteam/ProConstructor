package fr.weefle.constructor.NMS.Version_1_17_R1;

import fr.weefle.constructor.block.DataBuildBlock;
import net.minecraft.nbt.NBTTagCompound;
import org.bukkit.block.data.BlockData;

public class TileBuildBlock_1_17_R1 extends DataBuildBlock{
	
	public TileBuildBlock_1_17_R1(int x, int y, int z, BlockData data) {
		super(x, y, z, data);
	}

	public NBTTagCompound nbt = null;
	
}