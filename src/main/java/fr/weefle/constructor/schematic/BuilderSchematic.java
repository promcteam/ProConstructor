package fr.weefle.constructor.schematic;

import com.google.common.base.Preconditions;
import fr.weefle.constructor.NMS.NMS;
import fr.weefle.constructor.block.DataBuildBlock;
import fr.weefle.constructor.block.EmptyBuildBlock;
import fr.weefle.constructor.hooks.citizens.BuilderTrait;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class BuilderSchematic {
    private final String                name;
    private final EmptyBuildBlock[][][] blocks;
    private final Vector offset;
    private final Vector schematicOrigin;

    private int yOffset     = 0;
    private int userYOffset = 0;

    public BuilderSchematic(String name, int w, int h, int l, Vector offset, Vector schematicOrigin) {
        this.name = name;
        this.blocks = new EmptyBuildBlock[w][h][l];
        this.offset = offset;
        this.schematicOrigin = schematicOrigin;
    }

    public String getName() {return name;}

    public int getWidth() {return blocks.length;}

    public int getHeight() {return blocks[0].length;}

    public int getLength() {return blocks[0][0].length;}

    public EmptyBuildBlock getBlockAt(int x, int y, int z) {return blocks[x][y][z];}

    public void setBlockAt(int x, int y, int z, EmptyBuildBlock block) { blocks[x][y][z] = block; }

    public Vector getWEOffset() {return offset;}

    public Location getSchematicOrigin(BuilderTrait Builder) {
        if (schematicOrigin == null) {return null;}
        return schematicOrigin.clone().toLocation(Builder.getNPC().getEntity().getWorld()).add(getWidth()/2.0, 0, getLength()/.02);
    }

    public Queue<EmptyBuildBlock> createMarks(Material mat) {
        Queue<EmptyBuildBlock> Q = new LinkedList<>();
        Q.clear();
        Q.add(new DataBuildBlock(0, 0, 0, mat.createBlockData()));
        Q.add(new DataBuildBlock(getWidth() - 1, 0, 0, mat.createBlockData()));
        Q.add(new DataBuildBlock(0, 0, (int) getLength() - 1, mat.createBlockData()));
        Q.add(new DataBuildBlock(getWidth() - 1, 0, getLength() - 1, mat.createBlockData()));
        return Q;
    }


    public Location offset(EmptyBuildBlock block, Location origin) {
        return new Location(origin.getWorld(), block.X - getWidth()/2.0 + origin.getBlockX() + 1, block.Y - yOffset + userYOffset + origin.getBlockY() + .5, block.Z - getLength()/2.0 + origin.getBlockZ() + 1);
    }

    public Queue<EmptyBuildBlock> buildQueue(Location origin,
                                             boolean ignoreLiquids, boolean ignoreAir, boolean excavate,
                                             BuilderTrait.BuildPatternXZ pattern,
                                             boolean GroupByLayer, int ylayers, int useryoffset,
                                             @Nullable Map<Material,Integer> excavated) {
        Preconditions.checkArgument(ylayers > 0, "ylayers must be positive, but got " + ylayers);
        yOffset = 0;
        this.userYOffset = useryoffset;
        Queue<EmptyBuildBlock> Q = new LinkedList<>();

        //clear out empty planes on the bottom.
        boolean ok = false;
        for (int tmpy = 0; tmpy < this.getHeight(); tmpy++) {
            for (int tmpx = 0; tmpx < this.getWidth(); tmpx++) {
                for (int tmpz = 0; tmpz < this.getLength(); tmpz++) {

                    if (this.blocks[tmpx][tmpy][tmpz].getMat().getMaterial() != Material.AIR) {
                        ok = true;
                    }
                }
            }
            if (ok) break;
            else yOffset++;
        }
        Queue<EmptyBuildBlock> exair     = new LinkedList<>();
        Queue<EmptyBuildBlock> air       = new LinkedList<>();
        Queue<EmptyBuildBlock> base      = new LinkedList<>();
        Queue<EmptyBuildBlock> furniture = new LinkedList<>();
        Queue<EmptyBuildBlock> redstone  = new LinkedList<>();
        Queue<EmptyBuildBlock> Liq       = new LinkedList<>();
        Queue<EmptyBuildBlock> Decor     = new LinkedList<>();
        Queue<EmptyBuildBlock> buildQ    = new LinkedList<>();

        for (int y = yOffset; y < getHeight(); y += ylayers) {
            List<EmptyBuildBlock> thisLayer;
            switch (pattern) {
                case LINEAR:
                    thisLayer = NMS.getInstance().getUtil().LinearPrintLayer(y, ylayers, blocks, false);
                    break;
                case REVERSE_LINEAR:
                    thisLayer = NMS.getInstance().getUtil().LinearPrintLayer(y, ylayers, blocks, true);
                    break;
                case REVERSE_SPIRAL:
                    thisLayer = NMS.getInstance().getUtil().spiralPrintLayer(y, ylayers, blocks, true);
                    break;
                case SPIRAL:
                default:
                    thisLayer = NMS.getInstance().getUtil().spiralPrintLayer(y, ylayers, blocks, false);
                    break;
            }


            for (EmptyBuildBlock b : thisLayer) {
                //check if it needs to be placed.
                org.bukkit.block.Block pending = Objects.requireNonNull(origin.getWorld()).getBlockAt(offset(b, origin));

                if (excavate && !pending.isEmpty()) {
                    exair.add(new EmptyBuildBlock(b.X, b.Y, b.Z));
                    if (excavated != null) {
                        Material material = pending.getType();
                        if (material != org.bukkit.Material.AIR && material.isItem()) {
                            excavated.put(material, excavated.getOrDefault(material, 0) + 1);
                        }
                    }
                }

                if (!excavate) {
                    if (pending.getBlockData().getMaterial() == b.getMat().getMaterial()) {
                        continue;
                    }
                }

                org.bukkit.Material m = b.getMat().getMaterial();

                switch (m) {
                    //<editor-fold defaultstate="collapsed" desc="isAir">
                    case AIR:
                    case CAVE_AIR:
                    case VOID_AIR:
                    //</editor-fold>
                        //first
                        if (!ignoreAir && !excavate) air.add(b);
                        break;
                    //<editor-fold defaultstate="collapsed" desc="isLiquid">
                    case WATER:
                    case LEGACY_STATIONARY_WATER:
                    case LAVA:
                    case LEGACY_STATIONARY_LAVA:
                    //</editor-fold>
                        //5th
                        if (!ignoreLiquids) Liq.add(b);
                        break;
                    case SAND:
                    case GRAVEL:
                        Liq.add(b);
                        break;
                    //<editor-fold defaultstate="collapsed" desc="isDecor">
                    case TORCH:
                    case PAINTING:
                    case SNOW:
                    case LEGACY_WATER_LILY:
                    case CACTUS:
                    case LEGACY_SUGAR_CANE_BLOCK:
                    case PUMPKIN:
                    case PUMPKIN_STEM:
                    case LEGACY_PORTAL:
                    case LEGACY_CAKE_BLOCK:
                    case VINE:
                    case LEGACY_NETHER_WARTS:
                    case LEGACY_LEAVES:
                    case LEGACY_SAPLING:
                    case DEAD_BUSH:
                    case LEGACY_WEB:
                    case LEGACY_LONG_GRASS:
                    case LEGACY_RED_ROSE:
                    case LEGACY_YELLOW_FLOWER:
                    case RED_MUSHROOM:
                    case BROWN_MUSHROOM:
                    case FIRE:
                    case LEGACY_CROPS:
                    case LEGACY_MELON_BLOCK:
                    case MELON_STEM:
                    case LEGACY_ENDER_PORTAL:
                    case JACK_O_LANTERN:
                    case CARROT:
                    case POTATO:
                    case LEGACY_SKULL:
                    case LEGACY_CARPET:
                    //</editor-fold>
                        //very last
                        Decor.add(b);
                        break;
                    //<editor-fold defaultstate="collapsed" desc="isRedstone">
                    case LEGACY_REDSTONE_TORCH_ON:
                    case LEGACY_REDSTONE_TORCH_OFF:
                    case REDSTONE_WIRE:
                    case LEGACY_REDSTONE_LAMP_OFF:
                    case LEGACY_REDSTONE_LAMP_ON:
                    case LEVER:
                    case TRIPWIRE_HOOK:
                    case TRIPWIRE:
                    case STONE_BUTTON:
                    case LEGACY_DIODE_BLOCK_OFF:
                    case LEGACY_DIODE_BLOCK_ON:
                    case DAYLIGHT_DETECTOR:
                    case LEGACY_DIODE:
                    case LEGACY_RAILS:
                    case LEGACY_REDSTONE_COMPARATOR_ON:
                    case LEGACY_REDSTONE_COMPARATOR_OFF:
                    case POWERED_RAIL:
                    case DETECTOR_RAIL:
                    case ACTIVATOR_RAIL:
                    case LEGACY_PISTON_BASE:
                    case LEGACY_PISTON_EXTENSION:
                    case LEGACY_PISTON_MOVING_PIECE:
                    case LEGACY_PISTON_STICKY_BASE:
                    case TNT:
                    case LEGACY_STONE_PLATE:
                    case LEGACY_WOOD_PLATE:
                    case GLOWSTONE:
                    case HOPPER:
                    case REDSTONE_BLOCK:
                    case LEGACY_GOLD_PLATE:
                    case LEGACY_IRON_PLATE:
                    case LEGACY_WOOD_BUTTON:
                    //</editor-fold>
                        //4th
                        redstone.add(b);
                        break;
                    //<editor-fold defaultstate="collapsed" desc="isFurniture">
                    case FURNACE:
                    case LEGACY_BURNING_FURNACE:
                    case BREWING_STAND:
                    case CHEST:
                    case JUKEBOX:
                    case CAULDRON:
                    case LEGACY_WOOD_DOOR:
                    case LEGACY_WOODEN_DOOR:
                    case IRON_DOOR:
                    case LEGACY_TRAP_DOOR:
                    case LEGACY_ENCHANTMENT_TABLE:
                    case DISPENSER:
                    case LEGACY_WORKBENCH:
                    case LEGACY_SOIL:
                    case LEGACY_SIGN_POST:
                    case LEGACY_WALL_SIGN:
                    case LADDER:
                    case LEGACY_FENCE:
                    case LEGACY_FENCE_GATE:
                    case LEGACY_IRON_FENCE:
                    case LEGACY_THIN_GLASS:
                    case LEGACY_NETHER_FENCE:
                    case DRAGON_EGG:
                    case LEGACY_BED_BLOCK:
                    case GLASS:
                    case LEGACY_BIRCH_WOOD_STAIRS:
                    case LEGACY_JUNGLE_WOOD_STAIRS:
                    case LEGACY_WOOD_STAIRS:
                    case LEGACY_SPRUCE_WOOD_STAIRS:
                    case QUARTZ_STAIRS:
                    case TRAPPED_CHEST:
                    case ANVIL:
                    case FLOWER_POT:
                    //</editor-fold>
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

            if (GroupByLayer) {
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


        if (!GroupByLayer) {
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

    public String getInfo() {
        return ChatColor.GREEN + "Name: " + ChatColor.WHITE + name + ChatColor.GREEN + " size: " + ChatColor.WHITE + getWidth() + " wide, " + getLength() + " long, " + getHeight() + " tall";
    }
}


