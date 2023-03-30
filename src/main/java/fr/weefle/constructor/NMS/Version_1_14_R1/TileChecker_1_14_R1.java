package fr.weefle.constructor.NMS.Version_1_14_R1;

import fr.weefle.constructor.API.TileChecker;
import fr.weefle.constructor.block.EmptyBuildBlock;
import fr.weefle.constructor.block.EntityMap;
import net.minecraft.server.v1_14_R1.BlockPosition;
import net.minecraft.server.v1_14_R1.NBTTagCompound;
import net.minecraft.server.v1_14_R1.TileEntity;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_14_R1.CraftWorld;

public class TileChecker_1_14_R1 implements TileChecker {

	public void check(EmptyBuildBlock next, Block pending) {
		
		if (next instanceof TileBuildBlock_1_14_R1){			
			CraftWorld cw =(CraftWorld)pending.getWorld();			
			NBTTagCompound nbt = new NBTTagCompound();
			//Bukkit.getLogger().warning(((TileBuildBlock) next).nbt + "");
			nbt.a(((TileBuildBlock_1_14_R1) next).nbt);
			nbt.setInt("x", pending.getX());
			nbt.setInt("y", pending.getY());
			nbt.setInt("z", pending.getZ());
			//nbt.set("Items", ((TileBuildBlock) next).items);
			//nbt.setString("id", ((TileBuildBlock_1_14_R1) next).id);
			BlockPosition bp = new BlockPosition(pending.getX(), pending.getY(), pending.getZ());
			//Bukkit.getLogger().warning(pending.getX()+","+ pending.getY()+"," +pending.getZ());
			TileEntity te = cw.getHandle().getTileEntity(bp);
			//Bukkit.getLogger().warning(te+" : " + bp);
			if(te!=null) {
			te.load(nbt);
			//te.update();
			//pending.getState().update(true, true);
			}
		}
else if (next instanceof EntityMap){			
    		
			CraftWorld cw =(CraftWorld)pending.getWorld();			
			//NBTTagCompound nmsnbt = new NBTTagCompound();
			NBTTagCompound nmsnbt = (NBTTagCompound) Util_1_14_R1.fromNative(((EntityMap) next).nbt);
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
