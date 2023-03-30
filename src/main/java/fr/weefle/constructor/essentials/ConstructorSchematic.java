package fr.weefle.constructor.essentials;

import java.util.*;

import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.util.Vector;

import fr.weefle.constructor.NMS.NMS;
import fr.weefle.constructor.block.DataBuildBlock;
import fr.weefle.constructor.block.EmptyBuildBlock;

@SuppressWarnings("deprecation")
public class ConstructorSchematic {
	public Vector offset;
	public EmptyBuildBlock[][][] Blocks = new EmptyBuildBlock[1][1][1];
	public Queue<BlockData> excludedMaterials = new LinkedList<>();

	public String Name = ""; 
	public Vector SchematicOrigin = null;

	public Location getSchematicOrigin(ConstructorTrait Builder){	
		if (SchematicOrigin == null)return null;


		World W = Builder.getNPC().getEntity().getWorld();

		return	SchematicOrigin.clone().toLocation(W).add(dwidth/2,0,dlength/2);
	}

	//TODO change int mat by BlockData mat
	public Queue<EmptyBuildBlock> CreateMarks(double i, double j, double k, Material mat){
		dwidth = i;
		dlength = k;
		Queue<EmptyBuildBlock> Q = new LinkedList<EmptyBuildBlock>();
		Q.clear();
		Q.add(new DataBuildBlock(0,0,0, mat.createBlockData()));
		Q.add(new DataBuildBlock((int) (i-1),0,0, mat.createBlockData()));
		Q.add(new DataBuildBlock(0,0,(int)k-1, mat.createBlockData()));
		Q.add(new DataBuildBlock((int)i-1,0,(int)k-1, mat.createBlockData()));
		return Q;
	}


	public Location offset(EmptyBuildBlock block, Location origin){

		
		return new Location(origin.getWorld(),block.X - this.dwidth/2 + origin.getBlockX() + 1,block.Y - yoffset +useryoffset + origin.getBlockY()+.5,block.Z - this.dlength/2 + origin.getBlockZ() + 1 );
	}


	int yoffset = 0;
	int useryoffset = 0;

	public Queue<EmptyBuildBlock> BuildQueue(Location origin, boolean ignoreLiquids, boolean ignoreAir, boolean excavate, fr.weefle.constructor.essentials.ConstructorTrait.BuildPatternsXZ pattern, boolean GroupByLayer, int ylayers, int useryoffset){
		dwidth = width();
		dlength = length();
		yoffset = 0;
		this.useryoffset = useryoffset;
		Queue<EmptyBuildBlock> Q = new LinkedList<EmptyBuildBlock>();

		//clear out empty planes on the bottom.
		boolean ok =false;
		for (int tmpy = 0;tmpy< this.height();tmpy++){
			for (int tmpx = 0;tmpx< this.width();tmpx++){
				for (int tmpz = 0;tmpz< this.length();tmpz++){

					if (this.Blocks[tmpx][tmpy][tmpz].getMat().getMaterial() != Material.AIR) {
						ok = true;
					}
				}
			}		
			if (ok) break;
			else yoffset++;
		}
		Queue<EmptyBuildBlock> exair = new LinkedList<EmptyBuildBlock>();
		Queue<EmptyBuildBlock> air = new LinkedList<EmptyBuildBlock>();
		Queue<EmptyBuildBlock> base = new LinkedList<EmptyBuildBlock>();
		Queue<EmptyBuildBlock> furniture = new LinkedList<EmptyBuildBlock>();
		Queue<EmptyBuildBlock> redstone = new LinkedList<EmptyBuildBlock>();
		Queue<EmptyBuildBlock> Liq = new LinkedList<EmptyBuildBlock>();
		Queue<EmptyBuildBlock> Decor = new LinkedList<EmptyBuildBlock>();
		Queue<EmptyBuildBlock> buildQ = new LinkedList<EmptyBuildBlock>();



		for(int y = yoffset;y<height();y+=ylayers){

			List<EmptyBuildBlock> thisLayer;
			switch (pattern){
			case linear:
				thisLayer = NMS.getInstance().getUtil().LinearPrintLayer(y,ylayers, Blocks, false);
				break;
			case reverselinear:
				thisLayer = NMS.getInstance().getUtil().LinearPrintLayer(y,ylayers, Blocks, true);
				break;
			case reversespiral:
				thisLayer = NMS.getInstance().getUtil().spiralPrintLayer(y,ylayers, Blocks, true);
				break;
				case spiral:
				default:
				thisLayer = NMS.getInstance().getUtil().spiralPrintLayer(y,ylayers, Blocks, false);
				break;
			}



			for(EmptyBuildBlock b:thisLayer){
				//check if it needs to be placed.
				org.bukkit.block.Block pending = Objects.requireNonNull(origin.getWorld()).getBlockAt(offset(b,origin));

				if (excavate && !pending.isEmpty()){
					exair.add(new EmptyBuildBlock(b.X, b.Y, b.Z));
					excludedMaterials.add(pending.getBlockData());
				}

				if(!excavate) {
					if (pending.getBlockData().getMaterial() == b.getMat().getMaterial()) {
						continue;
					}
				}

				org.bukkit.Material m = b.getMat().getMaterial();

				switch (m) {
					case AIR: case CAVE_AIR: case VOID_AIR:
					//first
					if (!ignoreAir && !excavate) air.add(b);
					break;
				case WATER:	case LEGACY_STATIONARY_WATER:	case LAVA:	case LEGACY_STATIONARY_LAVA:
					//5th
					if (!ignoreLiquids) Liq.add(b);
					break;	
				case SAND: case GRAVEL:
					Liq.add(b);
					break;
				case TORCH:	case PAINTING:	case SNOW: 	case LEGACY_WATER_LILY: case CACTUS: case LEGACY_SUGAR_CANE_BLOCK: case PUMPKIN: case PUMPKIN_STEM: case LEGACY_PORTAL: case LEGACY_CAKE_BLOCK: case VINE: case LEGACY_NETHER_WARTS: case LEGACY_LEAVES:
				case LEGACY_SAPLING :case DEAD_BUSH: case LEGACY_WEB: case LEGACY_LONG_GRASS: case LEGACY_RED_ROSE: case LEGACY_YELLOW_FLOWER: case RED_MUSHROOM: case BROWN_MUSHROOM: case FIRE: case LEGACY_CROPS: case LEGACY_MELON_BLOCK: case MELON_STEM: case LEGACY_ENDER_PORTAL:
				case JACK_O_LANTERN: case CARROT: case POTATO: case LEGACY_SKULL: case LEGACY_CARPET:
					//very last
					Decor.add(b);
					break;
				case LEGACY_REDSTONE_TORCH_ON:	case LEGACY_REDSTONE_TORCH_OFF: case REDSTONE_WIRE: case LEGACY_REDSTONE_LAMP_OFF: case LEGACY_REDSTONE_LAMP_ON: case LEVER: case TRIPWIRE_HOOK: case TRIPWIRE: case STONE_BUTTON: case LEGACY_DIODE_BLOCK_OFF:
				case LEGACY_DIODE_BLOCK_ON: case DAYLIGHT_DETECTOR: case LEGACY_DIODE: case LEGACY_RAILS: case LEGACY_REDSTONE_COMPARATOR_ON: case LEGACY_REDSTONE_COMPARATOR_OFF: case POWERED_RAIL: case DETECTOR_RAIL: case ACTIVATOR_RAIL: case LEGACY_PISTON_BASE: 
				case LEGACY_PISTON_EXTENSION: case LEGACY_PISTON_MOVING_PIECE: case LEGACY_PISTON_STICKY_BASE: case TNT: case LEGACY_STONE_PLATE: case LEGACY_WOOD_PLATE: case GLOWSTONE:	case HOPPER: case REDSTONE_BLOCK:  case LEGACY_GOLD_PLATE: case LEGACY_IRON_PLATE:
				case LEGACY_WOOD_BUTTON: 
					//4th
					redstone.add(b);
					break;
				case FURNACE:case LEGACY_BURNING_FURNACE:	case BREWING_STAND: case CHEST: case JUKEBOX: case CAULDRON: case LEGACY_WOOD_DOOR: case LEGACY_WOODEN_DOOR: case IRON_DOOR: case LEGACY_TRAP_DOOR: case LEGACY_ENCHANTMENT_TABLE:
				case DISPENSER: case LEGACY_WORKBENCH: case LEGACY_SOIL: case LEGACY_SIGN_POST: case LEGACY_WALL_SIGN: case LADDER: case LEGACY_FENCE: case LEGACY_FENCE_GATE: case LEGACY_IRON_FENCE: case LEGACY_THIN_GLASS: case LEGACY_NETHER_FENCE: case DRAGON_EGG: case LEGACY_BED_BLOCK: case GLASS:
				case LEGACY_BIRCH_WOOD_STAIRS: case LEGACY_JUNGLE_WOOD_STAIRS: case LEGACY_WOOD_STAIRS: case LEGACY_SPRUCE_WOOD_STAIRS: case QUARTZ_STAIRS: case TRAPPED_CHEST: case ANVIL: case FLOWER_POT: 
					//3rd
					furniture.add(b);
					break;
				default:
					//second
					base.add(b);
					break;
				} 	

			}

			thisLayer.clear();

			if(GroupByLayer){
				buildQ.addAll(air);
				buildQ.addAll(base);
				buildQ.addAll(furniture);
				buildQ.addAll(redstone);
				buildQ.addAll(Liq);
				buildQ.addAll(Decor);

				air.clear();
				base.clear();
				furniture.clear();
				redstone.clear();
				Liq.clear();
				Decor.clear();		
			}	

		}


		if(!GroupByLayer){
			buildQ.addAll(air);
			buildQ.addAll(base);
			buildQ.addAll(furniture);
			buildQ.addAll(redstone);
			buildQ.addAll(Liq);
			buildQ.addAll(Decor);

			air.clear();
			base.clear();
			furniture.clear();
			redstone.clear();
			Liq.clear();
			Decor.clear();		
		}	

		java.util.Collections.reverse((List<?>) exair);

		Q.addAll(exair);
		Q.addAll(buildQ);

		exair.clear();
		buildQ.clear();

		return Q;
	}

	public ConstructorSchematic(int w, int h, int l){
		Blocks = new EmptyBuildBlock[w][h][l]; 
		dwidth = w;
		dlength = l;
	}

	public ConstructorSchematic() {

	}

	public double dwidth, dlength;

	public int width(){
		return Blocks.length;
	}

	public int height(){
		return Blocks[0].length;
	}

	public int length(){
		return Blocks[0][0].length;
	}

	public String GetInfo(){
		return ChatColor.GREEN + "Name: "+ ChatColor.WHITE + Name + ChatColor.GREEN + " size: " + ChatColor.WHITE + width() + " wide, " + length() +  " long, " + height() + " tall"; 
	}


}


