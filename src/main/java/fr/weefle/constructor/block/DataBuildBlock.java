package fr.weefle.constructor.block;

import org.bukkit.block.data.BlockData;

public class DataBuildBlock extends EmptyBuildBlock{
	private final BlockData data;
	
	public DataBuildBlock(int x, int y, int z, BlockData data){
		this.X = x;
		this.Y = y;
		this.Z = z;
		this.data = data;
	}
	
	@Override
	public BlockData getMat(){
		return data;
	}

	
}
