package studio.magemonkey.blueprint.schematic;

import com.google.common.base.Preconditions;
import studio.magemonkey.blueprint.hooks.citizens.BuilderTrait;
import studio.magemonkey.blueprint.nbt.*;
import studio.magemonkey.blueprint.nms.NMS;
import studio.magemonkey.blueprint.schematic.blocks.DataBuildBlock;
import studio.magemonkey.blueprint.schematic.blocks.EmptyBuildBlock;
import studio.magemonkey.blueprint.schematic.blocks.EntityMap;
import studio.magemonkey.blueprint.schematic.blocks.TileBuildBlock;
import studio.magemonkey.blueprint.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public class RawSchematic extends Schematic {
    private int width, height, length;
    private Vector                 absolutePosition;
    private EmptyBuildBlock[][][]  blocks;
    private Map<Material, Integer> materials;
    private List<SchematicEntity>  entities;

    public RawSchematic(Path path) {
        super(path);
        load(false);
    }

    @Override
    @Nullable
    public Vector getAbsolutePosition() {return this.absolutePosition == null ? null : this.absolutePosition.clone();}

    @Override
    public int getWidth() {return width;}

    @Override
    public int getHeight() {return height;}

    @Override
    public int getLength() {return length;}

    @Override
    @NotNull
    public EmptyBuildBlock getBlockAt(int x, int y, int z) {
        if (this.blocks == null) {
            throw new IllegalStateException("Schematic not loaded");
        }
        EmptyBuildBlock block = blocks[x][y][z];
        return block == null ? new EmptyBuildBlock(x, y, z) : block;
    }

    private void load(boolean full) {
        File file = new File(getPath());
        if (getPath().endsWith(".schem")) {
            Object data;
            try (FileInputStream in = new FileInputStream(file)) {
                data = NMS.getInstance().getNMSProvider().loadNBTFromInputStream(in);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            this.width = NMS.getInstance().getNMSProvider().nbtTagCompound_getShort(data, "Width");
            this.height = NMS.getInstance().getNMSProvider().nbtTagCompound_getShort(data, "Height");
            this.length = NMS.getInstance().getNMSProvider().nbtTagCompound_getShort(data, "Length");

            int[] offset = NMS.getInstance().getNMSProvider().nbtTagCompound_getIntArray(data, "Offset");
            if (offset.length == 3) {
                absolutePosition = new Vector(offset[0], offset[1], offset[2]);
            }

            if (!full) {
                return;
            }
            this.blocks = new EmptyBuildBlock[width][height][length];

            Object palette =
                    NMS.getInstance().getNMSProvider().nbtTagCompound_getCompound(data, "Palette");
            Map<Integer, BlockData> blockStates = new HashMap<>();
            for (String rawState : NMS.getInstance().getNMSProvider().nbtTagCompound_getAllKeys(palette)) {
                int id = NMS.getInstance().getNMSProvider().nbtTagCompound_getInt(palette, rawState);
                try {
                    blockStates.put(id, Bukkit.createBlockData(rawState));
                } catch (IllegalArgumentException e) {
                    String materialName;
                    if (rawState.contains("[")) {
                        materialName =
                                rawState.substring(0, rawState.indexOf("[")).replace("minecraft:", "").toUpperCase();
                    } else {
                        materialName = rawState.replace("minecraft:", "").toUpperCase();
                    }
                    Material material;
                    material = Material.getMaterial(materialName);
                    if (material == null) {
                        material = Material.getMaterial(materialName, true);
                    }
                    if (material == null) {
                        continue;
                    }
                    blockStates.put(id, Bukkit.createBlockData(material));
                }
            }

            int          version = NMS.getInstance().getNMSProvider().nbtTagCompound_getInt(data, "Version");
            List<Object> tileEntities;
            if (version > 1) {
                tileEntities = NMS.getInstance().getNMSProvider().nbtTagCompound_getList(data, "BlockEntities", 10);
            } else {
                tileEntities = NMS.getInstance().getNMSProvider().nbtTagCompound_getList(data, "TileEntities", 10);
            }
            Map<Vector, Object> tileEntitiesMap = new HashMap<>();
            Class<?>            nbtTagCompound  = NMS.getInstance().getNMSProvider().getNBTTagCompoundClass();
            for (Object tag : tileEntities) {
                if (!nbtTagCompound.isAssignableFrom(tag.getClass())) {
                    continue;
                }
                int[] pos = NMS.getInstance().getNMSProvider().nbtTagCompound_getIntArray(tag, "Pos");
                if (pos.length == 3) {
                    tileEntitiesMap.put(new Vector(pos[0], pos[1], pos[2]), tag);
                }
            }

            byte[] blocks = NMS.getInstance().getNMSProvider().nbtTagCompound_getByteArray(data, "BlockData");
            int    index  = 0;
            int    i      = 0;
            while (i < blocks.length) {
                int value         = 0;
                int varint_length = 0;
                while (true) {
                    value |= (blocks[i] & 127) << (varint_length++ * 7);
                    if (varint_length > 5) {
                        throw new RuntimeException("VarInt too big (probably corrupted data)");
                    }
                    if ((blocks[i] & 128) != 128) {
                        i++;
                        break;
                    }
                    i++;
                }
                int       y          = index / (width * length);
                int       z          = (index % (width * length)) / width;
                int       x          = (index % (width * length)) % width;
                Vector    vector     = new Vector(x, y, z);
                BlockData blockState = blockStates.get(value);
                Object    tileEntity = tileEntitiesMap.remove(vector);
                this.blocks[x][y][z] = tileEntity == null ?
                        new DataBuildBlock(x, y, z, blockState) :
                        new TileBuildBlock(x, y, z, blockState, tileEntity);
                index++;
            }

            this.entities = new ArrayList<>();
            for (Object object : NMS.getInstance().getNMSProvider().nbtTagCompound_getList(data, "Entities", 10)) {
                try {
                    this.entities.add(new SchematicEntity(object, absolutePosition));
                } catch (Exception ignored) {
                }
            }
        } else {
            CompoundTag data;
            try (NBTInputStream inputStream = new NBTInputStream(new FileInputStream(file), true)) {
                data = (CompoundTag) inputStream.readNamedTag().getTag();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            ListTag sizeTag = data.getListTag("size");
            this.width = sizeTag.getInt(0);
            this.height = sizeTag.getInt(1);
            this.length = sizeTag.getInt(2);

            if (!full) {
                return;
            }
            this.blocks = new EmptyBuildBlock[width][height][length];

            Map<Vector, BlockData> blocks       = new HashMap<>();
            Map<Vector, Tag>       tileEntities = new HashMap<>();
            ListTag                blockTags    = data.getListTag("blocks");
            ListTag                paletteTags;
            if (data.containsKey("palette")) {
                paletteTags = data.getListTag("palette");
                readBlocks(paletteTags, blockTags, blocks, tileEntities);
            } else {
                paletteTags = data.getListTag("palettes");
                for (int i = 0; i < paletteTags.getValue().size(); i++) {
                    readBlocks(paletteTags.getListTag(i), blockTags, blocks, tileEntities);
                }
            }

            for (Map.Entry<Vector, BlockData> entry : blocks.entrySet()) {
                Vector vector     = entry.getKey();
                int    x          = vector.getBlockX();
                int    y          = vector.getBlockY();
                int    z          = vector.getBlockZ();
                Tag    tileEntity = tileEntities.remove(vector);
                this.blocks[x][y][z] = tileEntity == null ?
                        new DataBuildBlock(x, y, z, entry.getValue()) :
                        new EntityMap(x, y, z, entry.getValue(), tileEntity);
            }
        }
        if (this.blocks != null) {
            Map<Material, Integer> materials = new TreeMap<>();
            for (EmptyBuildBlock[][] plane : this.blocks) {
                for (EmptyBuildBlock[] row : plane) {
                    for (EmptyBuildBlock emptyBuildBlock : row) {
                        Material material = emptyBuildBlock.getMat().getMaterial();
                        if (material != Material.AIR && material.isItem()) {
                            materials.put(material, materials.getOrDefault(material, 0) + 1);
                        }
                    }
                }
            }
            this.materials = Collections.unmodifiableMap(materials);
        }
    }

    private void unload() {
        this.blocks = null;
    }

    private void readBlocks(ListTag paletteTags,
                            ListTag blockTags,
                            Map<Vector, BlockData> blocks,
                            Map<Vector, Tag> tileEntities) {
        for (int i = 0; i < blockTags.getValue().size(); i++) {
            Tag tag = blockTags.getIfExists(i);
            if (!(tag instanceof CompoundTag)) {
                continue;
            }
            CompoundTag blockTag     = (CompoundTag) tag;
            ListTag     positionTags = blockTag.getListTag("pos");
            Vector position =
                    new Vector(positionTags.getInt(0), positionTags.getInt(1), positionTags.getInt(2));

            tag = paletteTags.getIfExists(blockTag.getInt("state"));
            if (!(tag instanceof CompoundTag)) {
                continue;
            }
            CompoundTag data = (CompoundTag) tag;
            if (!data.containsKey("Name")) {
                continue;
            }
            String   materialName = data.getString("Name").replace("minecraft:", "").toUpperCase();
            Material material     = Material.getMaterial(materialName);
            if (material == null) {
                material = Material.getMaterial(materialName, true);
            }
            if (material == null || material == Material.AIR) {
                continue;
            }
            BlockData blockData;
            if (data.containsKey("Properties")) {
                CompoundTag                      propertyTag   = (CompoundTag) data.getValue().get("Properties");
                StringBuilder                    stringBuilder = new StringBuilder("[");
                Iterator<Map.Entry<String, Tag>> entryIterator = propertyTag.getValue().entrySet().iterator();
                while (entryIterator.hasNext()) {
                    Map.Entry<String, Tag> entry = entryIterator.next();
                    Tag                    value = entry.getValue();
                    if (!(value instanceof StringTag)) {
                        continue;
                    }
                    stringBuilder.append(entry.getKey()).append('=').append(((StringTag) value).getValue());
                    if (entryIterator.hasNext()) {
                        stringBuilder.append(',');
                    }
                }
                String blockDataString = stringBuilder.append(']').toString();
                blockData = material.createBlockData(blockDataString);
            } else {
                blockData = material.createBlockData();
            }

            if (blockTag.containsKey("nbt")) {
                tileEntities.put(position, blockTag.getValue().get("nbt"));
            }
            blocks.put(position, blockData);
        }
    }

    @Override
    public Location offset(Location origin, double x, double y, double z, int emptyLayers, int rotation) {
        return origin.clone().add(Util.rotateVector(new Vector(x, y - emptyLayers, z), rotation));
    }

    @Override
    @NotNull
    public Map<Material, Integer> getMaterials() {
        if (this.materials == null) {
            load(true);
            unload();
        }
        return this.materials;
    }

    @SuppressWarnings("deprecation")
    @Override
    @NotNull
    public Queue<EmptyBuildBlock> buildQueue(BuilderTrait builder) {
        load(true);
        int yLayers = builder.getBuildYLayers();
        Preconditions.checkArgument(yLayers > 0, "yLayers must be positive, but got " + yLayers);

        Queue<EmptyBuildBlock> queue = new LinkedList<>();

        int     emptyLayers = 0; // Clear out empty payers on the bottom.
        boolean ok          = false;
        for (int tmpy = 0; tmpy < this.getHeight(); tmpy++) {
            for (int tmpx = 0; tmpx < this.getWidth(); tmpx++) {
                for (int tmpz = 0; tmpz < this.getLength(); tmpz++) {

                    if (this.getBlockAt(tmpx, tmpy, tmpz).getMat().getMaterial() != Material.AIR) {
                        ok = true;
                    }
                }
            }
            if (ok) {
                break;
            } else {
                emptyLayers++;
            }
        }
        Queue<EmptyBuildBlock> exair     = new LinkedList<>();
        Queue<EmptyBuildBlock> air       = new LinkedList<>();
        Queue<EmptyBuildBlock> base      = new LinkedList<>();
        Queue<EmptyBuildBlock> furniture = new LinkedList<>();
        Queue<EmptyBuildBlock> redstone  = new LinkedList<>();
        Queue<EmptyBuildBlock> liquids   = new LinkedList<>();
        Queue<EmptyBuildBlock> decors    = new LinkedList<>();
        Queue<EmptyBuildBlock> buildQ    = new LinkedList<>();

        Location origin = builder.getOrigin();
        for (int y = emptyLayers; y < getHeight(); y += yLayers) {
            List<EmptyBuildBlock> thisLayer;
            switch (builder.getBuildPatternXZ()) {
                case LINEAR:
                    thisLayer = Util.LinearPrintLayer(y, yLayers, blocks, false);
                    break;
                case REVERSE_LINEAR:
                    thisLayer = Util.LinearPrintLayer(y, yLayers, blocks, true);
                    break;
                case REVERSE_SPIRAL:
                    thisLayer = Util.spiralPrintLayer(y, yLayers, blocks, true);
                    break;
                case SPIRAL:
                default:
                    thisLayer = Util.spiralPrintLayer(y, yLayers, blocks, false);
                    break;
            }

            for (EmptyBuildBlock b : thisLayer) {
                //check if it needs to be placed.
                Block pending = Objects.requireNonNull(origin.getWorld())
                        .getBlockAt(offset(origin, b.X, b.Y, b.Z, emptyLayers, builder.getRotation()));

                if (builder.isExcavate() && !pending.isEmpty()) {
                    exair.add(new EmptyBuildBlock(b.X, b.Y, b.Z)); // TODO remove
                    if (builder.ExcavateMaterials != null) {
                        Material material = pending.getType();
                        if (material != org.bukkit.Material.AIR && material.isItem()) {
                            builder.ExcavateMaterials.put(material,
                                    builder.ExcavateMaterials.getOrDefault(material, 0) + 1);
                        }
                    }
                }

                if (!builder.isExcavate()) {
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
                        if (!builder.isIgnoreAir() && !builder.isExcavate()) air.add(b);
                        break;
                    //<editor-fold defaultstate="collapsed" desc="isLiquid">
                    case WATER:
                    case LEGACY_STATIONARY_WATER:
                    case LAVA:
                    case LEGACY_STATIONARY_LAVA:
                        //</editor-fold>
                        //5th
                        if (!builder.ignoresLiquids()) liquids.add(b);
                        break;
                    case SAND:
                    case GRAVEL:
                        liquids.add(b);
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
                        decors.add(b);
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

            if (builder.GroupByLayer) {
                buildQ.addAll(air);
                buildQ.addAll(base);
                buildQ.addAll(furniture);
                buildQ.addAll(redstone);
                buildQ.addAll(liquids);
                buildQ.addAll(decors);

                air.clear();
                base.clear();
                furniture.clear();
                redstone.clear();
                liquids.clear();
                decors.clear();
            }

        }

        if (!builder.GroupByLayer) {
            buildQ.addAll(air);
            buildQ.addAll(base);
            buildQ.addAll(furniture);
            buildQ.addAll(redstone);
            buildQ.addAll(liquids);
            buildQ.addAll(decors);

            air.clear();
            base.clear();
            furniture.clear();
            redstone.clear();
            liquids.clear();
            decors.clear();
        }

        Collections.reverse((List<?>) exair);

        queue.addAll(exair);
        queue.addAll(buildQ);

        exair.clear();
        buildQ.clear();
        unload();
        return queue;
    }

    @Override
    @NotNull
    public Queue<SchematicEntity> getEntities() {
        return new LinkedList<>(this.entities);
    }
}


