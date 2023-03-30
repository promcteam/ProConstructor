package fr.weefle.constructor.NMS.Version_1_13_R2;

import fr.weefle.constructor.API.TileChecker;
import fr.weefle.constructor.block.EmptyBuildBlock;
import fr.weefle.constructor.block.EntityMap;
import net.minecraft.server.v1_13_R2.BlockPosition;
import net.minecraft.server.v1_13_R2.NBTTagCompound;
import net.minecraft.server.v1_13_R2.TileEntity;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_13_R2.CraftWorld;

public class TileChecker_1_13_R2 implements TileChecker {

	public void check(EmptyBuildBlock next, Block pending) {
		
		if (next instanceof TileBuildBlock_1_13_R2){			
			CraftWorld cw =(CraftWorld)pending.getWorld();			
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.a(((TileBuildBlock_1_13_R2) next).nbt);
			nbt.setInt("x", pending.getX());
			nbt.setInt("y", pending.getY());
			nbt.setInt("z", pending.getZ());
			//Bukkit.getLogger().warning(nbt.asString());
			BlockPosition bp = new BlockPosition(pending.getX(), pending.getY(), pending.getZ());
			TileEntity te = cw.getHandle().getTileEntity(bp);
			if(te!=null) {
			te.load(nbt);

			}
			
		}
		else if (next instanceof EntityMap){			
    		
			CraftWorld cw =(CraftWorld)pending.getWorld();			
			//NBTTagCompound nmsnbt = new NBTTagCompound();
			NBTTagCompound nmsnbt = (NBTTagCompound) Util_1_13_R2.fromNative(((EntityMap) next).nbt);
			//nmsnbt.a((NBTTagCompound) NMS.getInstance().getUtil().fromNative(nbt));
			//Bukkit.getLogger().warning(nbt.asString());
			//nmsnbt.set("Items", items);
			nmsnbt.setInt("x", pending.getX());
			nmsnbt.setInt("y", pending.getY());
			nmsnbt.setInt("z", pending.getZ());
			//Bukkit.getLogger().warning(nmsnbt.asString());
			BlockPosition bp = new BlockPosition(pending.getX(), pending.getY(), pending.getZ());
			TileEntity te = cw.getHandle().getTileEntity(bp);	
			if(te!=null) {
				te.load(nmsnbt);
			}
		}
		
	}
	
	

}
