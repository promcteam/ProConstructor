package fr.weefle.constructor.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import fr.weefle.constructor.block.DataBuildBlock;
import fr.weefle.constructor.block.EmptyBuildBlock;
import fr.weefle.constructor.block.EntityMap;
import fr.weefle.constructor.essentials.BuilderSchematic;
import fr.weefle.constructor.nbt.*;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.EntityType;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.FileInputStream;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Structure {
    private       int[]                  dimensions;
    private       short                  width        = 0;
    private       short                  height       = 0;
    private       short                  length       = 0;
    private final Map<Vector, BlockData> blockMap     = Maps.newHashMap();
    private final List<EntityInfo>       entities     = Lists.newArrayList();
    private final Map<Vector, Tag>       tileEntities = Maps.newHashMap();

    private final NBTDataExtractor dataExtractor;

    public Structure(File path, String filename) throws Exception {
        this();
        load(path, filename);
    }

    public Structure() {
        this.dataExtractor = new NBTDataExtractor();
    }

    public void place(Location base) {
        for (Map.Entry<Vector, BlockData> blockEntry : blockMap.entrySet()) {
            Vector    position = blockEntry.getKey();
            BlockData data     = blockEntry.getValue();

            base.clone().add(position).getBlock().setBlockData(data);
        }

        for (EntityInfo entityInfo : entities) {
            String     entityID   = entityInfo.getNBT().getString("id");
            EntityType entityType = EntityType.valueOf(entityID.replace("minecraft:", "").toUpperCase());

            base.getWorld().spawnEntity(base.clone().add(entityInfo.getBlockPosition()), entityType);
        }
    }

    public void populateData(CompoundTag data) {

        ListTag sizeTag = data.getListTag("size");
        this.dimensions = new int[]{sizeTag.getInt(0), sizeTag.getInt(1), sizeTag.getInt(2)};

        ListTag blockTags = data.getListTag("blocks");
        ListTag paletteTags;

        if (data.containsKey("palette")) {
            paletteTags = data.getListTag("palette");

            populateBlockStates(paletteTags, blockTags);
        } else {
            paletteTags = data.getListTag("palettes");

            for (int i = 0; i < paletteTags.getValue().size(); i++) {
                populateBlockStates(paletteTags.getListTag(i), blockTags);
            }
        }

        ListTag entities = data.getListTag("entities");
        populateEntities(entities);
    }

    private void populateBlockStates(ListTag paletteTags, ListTag blockTags) {
        for (int i = 0; i < blockTags.getValue().size(); i++) {
            CompoundTag blockTag     = (CompoundTag) blockTags.getIfExists(i);
            ListTag     positionTags = blockTag.getListTag("pos");
            CompoundTag stateTag     = (CompoundTag) paletteTags.getIfExists(blockTag.getInt("state"));


            Vector                     position  = new Vector(positionTags.getInt(0), positionTags.getInt(1), positionTags.getInt(2));
            NBTDataExtractor.BlockInfo blockInfo = dataExtractor.getBlockInfo(stateTag);
            
           /* if (blockTag.containsKey("nbt"))
	          {
	            CompoundTag nbtTag = (CompoundTag) blockTag.getValue().get("nbt");
          ListTag itemTags = (ListTag) nbtTag.getListTag("Items");
          //Bukkit.getLogger().warning(nbtTag.asString());
          
          for (Tag tag : itemTags.getValue()) {
  			if (!(tag instanceof CompoundTag)) continue;
  			CompoundTag t = (CompoundTag) tag;
  			
  			tileEntities.put(position, t.getValue());
          }
	          }*/

            if (blockTag.containsKey("nbt")) {
                CompoundTag nbtTag = (CompoundTag) blockTag.getValue().get("nbt");
                //Bukkit.getLogger().warning(nbtTag.asString());


                tileEntities.put(position, nbtTag);

            }


            this.blockMap.put(position, blockInfo.getData());
        }
    }

    private void populateEntities(ListTag entities) {
        for (int i = 0; i < entities.getValue().size(); i++) {
            CompoundTag entity = (CompoundTag) entities.getValue().get(i);

            ListTag positionTags = entity.getListTag("pos");
            Vector  position     = new Vector(positionTags.getDouble(0), positionTags.getDouble(1), positionTags.getDouble(2));

            ListTag blockPosTags  = entity.getListTag("blockPos");
            Vector  blockPosition = new Vector(blockPosTags.getInt(0), blockPosTags.getInt(1), blockPosTags.getInt(2));

            if (entity.containsKey("nbt")) {
                CompoundTag nbt = (CompoundTag) entity.getValue().get("nbt");
                this.entities.add(new EntityInfo(position, blockPosition, nbt));
            }
        }
    }


    public BuilderSchematic load(File path, String filename) throws Exception {
        File            file            = new File(path, filename + ".nbt");
        Vector          origin          = new Vector(0, 0, 0);
        FileInputStream fileInputStream = new FileInputStream(file);

        NBTInputStream inputStream = new NBTInputStream(fileInputStream, true);
        CompoundTag    data        = (CompoundTag) inputStream.readNamedTag().getTag();
        // data = NBTUpdater.updateData(data);
        populateData(data);


        BuilderSchematic out = new BuilderSchematic(dimensions[0], dimensions[1], dimensions[2]);
        width = (short) dimensions[0];
        height = (short) dimensions[1];
        length = (short) dimensions[2];

        for (Map.Entry<Vector, BlockData> blockEntry : blockMap.entrySet()) {
            Vector    position = blockEntry.getKey();
            BlockData bdata    = blockEntry.getValue();

            //Bukkit.getLogger().warning("Premier: " + position.toString());


            Vector v = null;
            for (Vector victor : tileEntities.keySet()) {
                //Bukkit.getLogger().warning("Second: " + victor.toString());
                if (victor.getBlockX() == position.getBlockX() && victor.getBlockY() == position.getBlockY() && victor.getBlockZ() == position.getBlockZ()) {
                    v = victor;
                    break;
                }
            }

            EmptyBuildBlock M;

            if (v != null) {
                M = new EntityMap(position.getBlockX(), position.getBlockY(), position.getBlockZ(), bdata);
                //Bukkit.getLogger().warning(tileEntities.get(v).toString());
                ((EntityMap) M).nbt = tileEntities.get(v);
                tileEntities.remove(v);
            } else {


                M = new DataBuildBlock(position.getBlockX(), position.getBlockY(), position.getBlockZ(), bdata);
            }


            out.Blocks[position.getBlockX()][position.getBlockY()][position.getBlockZ()] = M;
            //Bukkit.getLogger().warning(position.getBlockX()+","+position.getBlockY()+","+position.getBlockZ());


            //base.clone().add(position).getBlock().setBlockData(data);
        }

        //TODO trier les tableaux

        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                for (int z = 0; z < length; ++z) {

                    if (out.Blocks[x][y][z] == null) {
                        out.Blocks[x][y][z] = new EmptyBuildBlock(x, y, z);
                    }

                }
            }
        }


        out.Name = filename;
        out.SchematicOrigin = origin;
        inputStream.close();
        return out;

        //Bukkit.getLogger().warning(out.Blocks[10][11][6].getMat().getAsString());


    }

    /*public void rotate(int angle)
    {
        Map<Vector, BlockData> blockMapCopy = Maps.newHashMap();

        for (Map.Entry<Vector, BlockData> blockEntry : blockMap.entrySet())
        {
            Vector vector = blockEntry.getKey();
            BlockData blockData = blockEntry.getValue();

            if (blockData instanceof Directional)
            {
                Directional directional = (Directional) blockData;
                Vector directionVector = VectorUtil.toVector(directional.getFacing());
                directionVector = VectorUtil.rotateVector(directionVector, angle);
                ((Directional) blockData).setFacing(VectorUtil.fromVector(directionVector));
            }

            Vector offset = VectorUtil.rotateVector(vector, angle);

            blockMapCopy.put(offset, blockData);
        }

        this.blockMap = blockMapCopy;

//        Iterator<Vector> blockEntryIterator = blockMap.keySet().iterator();
//        while (blockEntryIterator.hasNext())
//        {
//            Vector position = blockEntryIterator.next();
//            BlockData blockData = blockMap.get(position);
//
//            if (blockData instanceof Directional)
//            {
//                Directional directional = (Directional) blockData;
//                BlockFace facing = directional.getFacing();
//
//                Vector directionVector = VectorUtil.toVector(facing);
//                directionVector = VectorUtil.rotateVector(directionVector, angle);
//                ((Directional) blockData).setFacing(VectorUtil.fromVector(directionVector));
//            }
//
//            Vector rotated = VectorUtil.rotateVector(position, angle);
//
//            this.blockMap.put(rotated, blockData);
//
//            blockEntryIterator.remove();
//        }
    }*/

    private class EntityInfo {

        private final Vector      blockPosition;
        private final CompoundTag nbt;

        public EntityInfo(Vector position, Vector blockPosition, CompoundTag nbt) {

            this.blockPosition = blockPosition;
            this.nbt = nbt;
        }


        public Vector getBlockPosition() {
            return blockPosition;
        }

        public CompoundTag getNBT() {
            return nbt;
        }
    }

    private class NBTDataExtractor {
        public BlockInfo getBlockInfo(CompoundTag data) {
            BlockInfo blockInfo = null;

            if (!data.containsKey("Name")) {
                blockInfo = new BlockInfo(Material.AIR, Material.AIR.createBlockData());
            } else {
                String materialName = data.getString("Name").replace("minecraft:", "").toUpperCase();


                if (EnumSet.allOf(Material.class).contains(Material.getMaterial(materialName))) {
                    Material material = Material.getMaterial(materialName);

                    try {
                        blockInfo = new BlockInfo(material, material.createBlockData());

                        if (data.containsKey("Properties")) {
                            CompoundTag propertyTag = (CompoundTag) data.getValue().get("Properties");

                            String blockDataString = toBlockData(propertyTag.getValue());

                            blockInfo = new BlockInfo(material, material.createBlockData(blockDataString));
                        }
                    } catch (Exception e) {


                    }

                } else {

                    Material material = Material.getMaterial(materialName, true);
                    try {

                        blockInfo = new BlockInfo(material, material.createBlockData());

                        if (data.containsKey("Properties")) {
                            CompoundTag propertyTag = (CompoundTag) data.getValue().get("Properties");

                            String blockDataString = toBlockData(propertyTag.getValue());

                            blockInfo = new BlockInfo(material, material.createBlockData(blockDataString));
                        }

                    } catch (Exception e) {


                    }
                }
            }

            return blockInfo;
        }

        private String toBlockData(Map<String, Tag> properties) {
            StringBuilder stringBuilder = new StringBuilder("[");

            Iterator<Map.Entry<String, Tag>> entryIterator = properties.entrySet().iterator();
            while (entryIterator.hasNext()) {
                Map.Entry<String, Tag> entry = entryIterator.next();
                String                 key   = entry.getKey();
                StringTag              value = (StringTag) entry.getValue();

                stringBuilder.append(key).append("=").append(value.asString());

                if (entryIterator.hasNext()) {
                    stringBuilder.append(",");
                }
            }

            return stringBuilder.append("]").toString();
        }


//    public static BlockInfo getBlockInfo(NBTTagCompound data)
//    {
//        BlockInfo blockInfo;
//
//        if (!data.contains("Name", 8))
//        {
//            blockInfo = new BlockInfo(Material.AIR, Material.AIR.createBlockData());
//        }
//        else
//        {
//            String materialName = data.getString("Name").replace("minecraft:", "").toUpperCase();
//            Material material = Material.getMaterial(materialName);
//
//            blockInfo = new BlockInfo(material, material.createBlockData());
//
//            if (data.contains("Properties", 10))
//            {
//                NBTTagCompound propertyTag = data.getCompound("Properties");
//
//                String blockDataString = toBlockData(propertyTag.getTagMap());
//
//                blockInfo = new BlockInfo(material, material.createBlockData(blockDataString));
//            }
//        }
//
//        return blockInfo;
//    }
//
//    private static String toBlockData(Map<String, INBTBase> properties)
//    {
//        StringBuilder stringBuilder = new StringBuilder("[");
//
//        Iterator<Map.Entry<String, INBTBase>> entryIterator = properties.entrySet().iterator();
//        while (entryIterator.hasNext())
//        {
//            Map.Entry<String, INBTBase> entry = entryIterator.next();
//            String key = entry.getKey();
//            NBTTagString value = (NBTTagString) entry.getValue();
//
//            stringBuilder.append(key).append("=").append(value.asString());
//
//            if (entryIterator.hasNext())
//            {
//                stringBuilder.append(",");
//            }
//        }
//
//        return stringBuilder.append("]").toString();
//    }

        public class BlockInfo {

            private final BlockData data;

            public BlockInfo(Material material, BlockData data) {

                this.data = data;
            }


            public BlockData getData() {
                return data;
            }
        }
    }
}