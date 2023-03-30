package fr.weefle.constructor.NMS.Version_1_18_R1;

import fr.weefle.constructor.API.TileChecker;
import fr.weefle.constructor.NMS.NMS;
import fr.weefle.constructor.block.EmptyBuildBlock;
import fr.weefle.constructor.block.EntityMap;
import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.level.block.entity.TileEntity;
import org.bukkit.block.Block;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class TileChecker_1_18_R1 implements TileChecker {


	public void check(EmptyBuildBlock next, Block pending) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		
		if (next instanceof TileBuildBlock_1_18_R1){
			NBTTagCompound nbt = new NBTTagCompound();
			//Bukkit.getLogger().warning(((TileBuildBlock) next).nbt + "");
			nbt.a(((TileBuildBlock_1_18_R1) next).nbt);
			Class[] partypes = new Class[2];
			partypes[0] = String.class;
			partypes[1] = Integer.TYPE;
			nbt.getClass().getMethod("a", partypes).invoke(nbt, "x", pending.getX());
			nbt.getClass().getMethod("a", partypes).invoke(nbt, "y", pending.getY());
			nbt.getClass().getMethod("a", partypes).invoke(nbt, "z", pending.getZ());
			//nbt.set("Items", ((TileBuildBlock) next).items);
			//nbt.setString("id", ((TileBuildBlock_1_15_R1) next).id);
			BlockPosition bp = new BlockPosition(pending.getX(), pending.getY(), pending.getZ());
			//Bukkit.getLogger().warning(pending.getX()+","+ pending.getY()+"," +pending.getZ());
			Class<?> craftWorldClass = NMS.getNMSClass("CraftWorld");
			Object craftWorldObject = craftWorldClass.cast(pending.getWorld());
			Method getHandleMethod = craftWorldObject.getClass().getMethod("getHandle");
			Object object = getHandleMethod.invoke(craftWorldObject);
			TileEntity te = (TileEntity) object.getClass().getMethod("c_", BlockPosition.class).invoke(object, bp);
			//Bukkit.getLogger().warning(te.getBlock().toString() + " : " + bp);
			if(te!=null) {
			//te.a(nbt);
				te.getClass().getMethod("a", NBTTagCompound.class).invoke(te, nbt);
			//te.update();
			//pending.getState().update(true, true);
			}
		}
else if (next instanceof EntityMap){
			//NBTTagCompound nmsnbt = new NBTTagCompound();
			NBTTagCompound nmsnbt = (NBTTagCompound) Util_1_18_R1.fromNative(((EntityMap) next).nbt);
			//nmsnbt.a((NBTTagCompound) NMS.getInstance().getUtil().fromNative(nbt));
			//Bukkit.getLogger().warning(nbt.asString());
			//nmsnbt.set("Items", items);
			Class[] partypes = new Class[2];
			partypes[0] = String.class;
			partypes[1] = Integer.TYPE;
			nmsnbt.getClass().getMethod("a", partypes).invoke(nmsnbt, "x", pending.getX());
			nmsnbt.getClass().getMethod("a", partypes).invoke(nmsnbt, "y", pending.getY());
			nmsnbt.getClass().getMethod("a", partypes).invoke(nmsnbt, "z", pending.getZ());
			//Bukkit.getLogger().warning(nmsnbt.asString());
			BlockPosition bp = new BlockPosition(pending.getX(), pending.getY(), pending.getZ());
			Class<?> craftWorldClass = NMS.getNMSClass("CraftWorld");
			Object craftWorldObject = craftWorldClass.cast(pending.getWorld());
			Method getHandleMethod = craftWorldObject.getClass().getMethod("getHandle");
			Object object = getHandleMethod.invoke(craftWorldObject);
			TileEntity te = (TileEntity) object.getClass().getMethod("c_", BlockPosition.class).invoke(object, bp);

			if(te!=null) {
				//te.a(nmsnbt);
				te.getClass().getMethod("a", NBTTagCompound.class).invoke(te, nmsnbt);
			}
		}
		
	}
	
	

}
