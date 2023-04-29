package fr.weefle.constructor.nms.providers;

public class NMSProvider_1_16 extends NMSProvider {
    private final String version;

    public NMSProvider_1_16(String version) {
        this.version = version;
    }

    @Override
    public String[] getNBTCompressedStreamToolsNames() {
        return new String[]{"net.minecraft.server." + version + ".NBTCompressedStreamTools"};
    }

    @Override
    public String[] getNBTTagByteClassNames() {
        return new String[]{"net.minecraft.server." + version + ".NBTTagByte"};
    }

    @Override
    public String[] getNBTTagByte_valueOfMethodNames() {
        return new String[]{"a", "valueOf"};
    }

    @Override
    public String[] getNBTTagByteArrayClassNames() {
        return new String[]{"net.minecraft.server." + version + ".NBTTagByteArray"};
    }

    @Override
    public String[] getNBTTagDoubleClassNames() {
        return new String[]{"net.minecraft.server." + version + ".NBTTagDouble"};
    }

    @Override
    public String[] getNBTTagFloatClassNames() {
        return new String[]{"net.minecraft.server." + version + ".NBTTagFloat"};
    }

    @Override
    public String[] getNBTTagIntClassNames() {
        return new String[]{"net.minecraft.server." + version + ".NBTTagInt"};
    }

    @Override
    public String[] getNBTTagIntArrayClassNames() {
        return new String[]{"net.minecraft.server." + version + ".NBTTagIntArray"};
    }

    @Override
    public String[] getNBTTagListClassNames() {
        return new String[]{"net.minecraft.server." + version + ".NBTTagList"};
    }

    @Override
    public String[] getNBTTagLongClassNames() {
        return new String[]{"net.minecraft.server." + version + ".NBTTagLong"};
    }

    @Override
    public String[] getNBTTagShortClassNames() {
        return new String[]{"net.minecraft.server." + version + ".NBTTagShort"};
    }

    @Override
    public String[] getNBTTagStringClassNames() {
        return new String[]{"net.minecraft.server." + version + ".NBTTagString"};
    }

    @Override
    public String[] getNBTTagCompoundClassNames() {
        return new String[]{"net.minecraft.server." + version + ".NBTTagCompound"};
    }

    @Override
    public String[] getNBTTagCompound_getAllKeysMethodNames() {
        return new String[]{"getKeys"};
    }

    @Override
    public String[] getNBTTagCompound_putMethodNames() {
        return new String[]{"set"};
    }

    @Override
    public String[] getNBTBaseClassNames() {
        return new String[]{"net.minecraft.server." + version + ".NBTBase"};
    }

    @Override
    public String[] getNBTTagCompound_putIntMethodNames() {
        return new String[]{"setInt"};
    }

    @Override
    public String[] getBlockPositionClassNames() {
        return new String[]{"net.minecraft.server." + version + ".BlockPosition"};
    }

    @Override
    public String[] getWorld_getBlockEntityMethodNames() {
        return new String[]{"getTileEntity"};
    }

    @Override
    public String[] getBlockEntityClassNames() {
        return new String[]{"net.minecraft.server." + version + ".TileEntity"};
    }

    @Override
    public String[] getIBlockDataClassNames() {
        return new String[]{"net.minecraft.server." + version + ".IBlockData"};
    }
}
