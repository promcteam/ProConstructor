package fr.weefle.constructor.NMS.Version_1_17_R1;

import fr.weefle.constructor.block.DataBuildBlock;
import fr.weefle.constructor.block.EmptyBuildBlock;
import fr.weefle.constructor.essentials.ConstructorSchematic;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTCompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.FileInputStream;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;


public class MCEditSchematicFormat_1_17_R1 {
	
	
	
	private byte[] blockData;
	private final Map<Integer, BlockData> blocks = new HashMap<>();
	private short width = 0;
	private short height = 0;
	private short length = 0;
	public int dataVersion;
	//private ArrayList<EntityMap> entitieslist = new ArrayList<>();


	/*public ArrayList<EntityMap> getEntitieslist() {
		return entitieslist;
	}


	public void setEntitieslist(ArrayList<EntityMap> entitieslist) {
		this.entitieslist = entitieslist;
	}*/


	public ConstructorSchematic load(File path, String filename) throws Exception {

		File file = new File(path,filename+".schem");

		if(!file.exists()) throw(new java.io.FileNotFoundException("File not found"));
		
		FileInputStream fis = new FileInputStream(file);
		NBTTagCompound nbt = NBTCompressedStreamTools.a(fis);
		
		//Bukkit.getLogger().warning(nbt.toString());
		
		dataVersion = nbt.getInt("DataVersion");

		Vector origin = new Vector();
		Vector offsetvec = new Vector();
		Vector offsetWE = new Vector();
		
		width = nbt.getShort("Width");
		height = nbt.getShort("Height");
		length = nbt.getShort("Length");
		
		int xoff = 0;
		int yoff = 0;
		int zoff = 0;
		
		int[] offset = nbt.getIntArray("Offset");
		if(offset.length == 3) {
			xoff = offset[0];
			yoff = offset[1];
			zoff = offset[2];
			offsetvec = new Vector(xoff,yoff,zoff);
		}

		NBTTagCompound meta = nbt.getCompound("Metadata");

		if (meta != null) {
			
			int offsetX = meta.getInt("WEOffsetX");
			int offsetY = meta.getInt("WEOffsetY");
			int offsetZ = meta.getInt("WEOffsetZ");
			offsetWE = new Vector(offsetX, offsetY, offsetZ);
			//Bukkit.getLogger().warning(originX + "," + originY + "," + originZ);
			origin = offsetvec.subtract(offsetWE);
		}


		blockData = nbt.getByteArray("BlockData");
		NBTTagCompound palette = nbt.getCompound("Palette");
		//Bukkit.getLogger().info(palette.getKeys().toString());
		//try {
			for(String rawState : palette.getKeys()) {
				
				int id = palette.getInt(rawState);
				if(getRawState(rawState)!=null){
					blocks.put(id, getRawState(rawState));
				}else{
					String material;
					if(rawState.contains("[")) {
						material = rawState.substring(0, rawState.indexOf("[")).replace("minecraft:", "").toUpperCase();
					}else{
						material = rawState.replace("minecraft:", "").toUpperCase();
					}
					//Bukkit.getLogger().warning(material);
					Material mat;
					if (EnumSet.allOf(Material.class).contains(Material.getMaterial(material)))
					{
						mat = Material.getMaterial(material);
					}else{
						mat = Material.getMaterial(material, true);
					}
					blocks.put(id, Bukkit.createBlockData(mat));
				}

				}

			/*	} catch (Exception e) {

			//TODO get rawState minecraft:id and remove [***] to convert into Material and create empty BlockData
					//Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Schematic was made in an other Minecraft version (" + dataVersion + ") Data is incompatible!");
					//Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "You need to convert your schematic in the right Data Version: https://minecraft.gamepedia.com/Data_version#List_of_data_versions");
			for(String rawState2 : palette.getKeys()) {

				int id2 = palette.getInt(rawState2);
				String material;
				if(rawState2.contains("[")) {
					material = rawState2.substring(0, rawState2.indexOf("[")).replace("minecraft:", "").toUpperCase();
				}else{
					material = rawState2.replace("minecraft:", "").toUpperCase();
				}
				//Bukkit.getLogger().warning(material);
				Material mat;
				if (EnumSet.allOf(Material.class).contains(Material.getMaterial(material)))
				{
					mat = Material.getMaterial(material);
				}else{
					mat = Material.getMaterial(material, true);
				}

				BlockData blockData3 = Bukkit.createBlockData(mat);
				blocks.put(id2, blockData3);

			}
					//return null;
				}*/
		
		int version = nbt.getInt("Version");
		
		NBTTagList tileEntities = null;
		//NBTTagList entities = null;
		//Bukkit.getLogger().warning(nbt+"");
		if(version>1) {
			//ArrayList<NBTTagDouble> listd = new ArrayList<NBTTagDouble>();;
		tileEntities = nbt.getList("BlockEntities", 10);
		/*entities = nbt.getList("Entities", 10);
		for (NBTBase tag : entities) {
			if (!(tag instanceof NBTTagCompound)) continue;
			NBTTagCompound t = (NBTTagCompound) tag;
			
			//Bukkit.getLogger().warning(t+"");

			double x = 0;
			double y = 0;
			double z = 0;
			
			String id = t.getString("Id");
			
			NBTTagList pos = t.getList("Pos", 6);
			//Bukkit.getLogger().warning(pos+"");

			for(NBTBase posi: pos) {
				if (!(posi instanceof NBTTagDouble)) continue;
				 NBTTagDouble tdouble = (NBTTagDouble) posi;
				 listd.add(tdouble);
			}
			//Bukkit.getLogger().warning(listd.size()+" avant");
			x = listd.get(0).asDouble();
			y = listd.get(1).asDouble();
			z = listd.get(2).asDouble();
			listd.removeAll(listd);
			//Bukkit.getLogger().warning(listd.size()+" apres");
				
				//Bukkit.getLogger().warning(t+"");
			id = id.replace("minecraft:", "");
			x = xoff - x;
			y = yoff - y;
			z = zoff - z;
			//Bukkit.getLogger().warning("Position: " + x + "," + y + "," + z);
			EntityMap ent = new EntityMap(x,y,z,t,id);
			entitieslist.add(ent);
				
	            
		}*/
		}else {
			tileEntities = nbt.getList("TileEntities", 10);
		}
		//Bukkit.getLogger().warning(tileEntities+"");
		Map<Vector, NBTBase> tileEntitiesMap = new HashMap<Vector, NBTBase>();
		
		
		for (NBTBase tag : tileEntities) {
			if (!(tag instanceof NBTTagCompound)) continue;
			NBTTagCompound t = (NBTTagCompound) tag;

			int x = 0;
			int y = 0;
			int z = 0;
			
			int[] pos = t.getIntArray("Pos");
			if(pos.length == 3) {
				x = pos[0];
				y = pos[1];
				z = pos[2];
				
				
			Vector vec = new Vector(x, y, z);
			tileEntitiesMap.put(vec, t);
			}		
	            
		}


		

		ConstructorSchematic out = new ConstructorSchematic(width, height,length);
		
		  int index = 0;
	        int i = 0;
	        int value = 0;
	        int varint_length = 0;
	        while (i < blockData.length) {
	            value = 0;
	            varint_length = 0;

	            while (true) {
	                value |= (blockData[i] & 127) << (varint_length++ * 7);
	                if (varint_length > 5) {
	                    throw new RuntimeException("VarInt too big (probably corrupted data)");
	                }
	                if ((blockData[i] & 128) != 128) {
	                    i++;
	                    break;
	                }
	                i++;
	            }
	            int y = index / (width * length);
	            int z = (index % (width * length)) / width;
	            int x = (index % (width * length)) % width;
	            BlockData data = blocks.get(value);
	            
	            EmptyBuildBlock M = null;
	            
	           Vector v = null;
				for (Vector victor : tileEntitiesMap.keySet()) {
					if(victor.getBlockX() == x && victor.getBlockY() == y && victor.getBlockZ() == z){					
						v = victor;
						break;
					}
				}

				if(v!=null) {

					M = new TileBuildBlock_1_17_R1(x,y,z, data);
					((TileBuildBlock_1_17_R1)M).nbt = (NBTTagCompound) tileEntitiesMap.get(v);
					//((TileBuildBlock_1_15_R1)M).id = ((NBTTagCompound) tileEntitiesMap.get(v)).getString("Id");
					tileEntitiesMap.remove(v);

				}else if(data==null) {
					

					
					M = new EmptyBuildBlock(x,y,z);
					
				}else {
					M = new DataBuildBlock(x,y,z, data);
				}

				out.Blocks[x][y][z] = M;
				//Bukkit.getLogger().warning(x+","+y+","+z);

	            index++;
	        }
	        //Bukkit.getLogger().warning(out.Blocks[10][11][6].getMat().getAsString()); 
		

		out.Name = filename;
		out.SchematicOrigin = origin;
		out.offset = offsetWE;
		fis.close();
		return out;
		
	}

	public BlockData getRawState(String rawState){

		BlockData bData;
		try {
			bData = Bukkit.createBlockData(rawState);
		}catch (Exception e){
			return null;
		}

		return bData;

	}

}
	
