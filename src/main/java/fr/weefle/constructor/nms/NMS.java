package fr.weefle.constructor.nms;

import fr.weefle.constructor.api.StructureUtil;
import fr.weefle.constructor.nbt.*;
import fr.weefle.constructor.nms.providers.*;
import org.bukkit.Bukkit;

import java.util.AbstractList;
import java.util.Map;

public class NMS {

    private static NMS                                        instance;
    public         String                                     version;
    private        NMSProvider                                nmsProvider;
    private        fr.weefle.constructor.api.TileChecker      checker;
    private        StructureUtil                              structure;

    public boolean setInstance() {
        String[] packageArray = Bukkit.getServer().getClass().getPackage().getName().split("\\.");
        version = packageArray[packageArray.length - 1];

        Bukkit.getLogger().info("Your server is running version " + version);

        if (version.compareTo("v1_19_R1") >= 0) {
            nmsProvider = new NMSProvider_1_19();
        } else if (version.compareTo("v1_18_R1") >= 0) {
            nmsProvider = new NMSProvider_1_18();
        } else if (version.compareTo("v1_17_R1") >= 0) {
            nmsProvider = new NMSProvider_1_17();
        } else if (version.compareTo("v1_16_R1") >= 0) {
            nmsProvider = new NMSProvider_1_16(version);
        } else {
            return false;
        }
        setChecker(new TileChecker());
        NMS.instance = this;
        return true;
    }

    public static NMS getInstance() {
        return instance;
    }

    public NMSProvider getNMSProvider() {return nmsProvider;}

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public fr.weefle.constructor.api.TileChecker getChecker() {
        return checker;
    }

    public void setChecker(fr.weefle.constructor.api.TileChecker checker) {
        this.checker = checker;
    }

    public static Class<?> getNMSClass(String classname) {
        String   version  = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + ".";
        String   name     = "org.bukkit.craftbukkit." + version + classname;
        Class<?> nmsClass = null;
        try {
            nmsClass = Class.forName(name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return nmsClass;
    }

    public static Object fromNative(Tag foreign) {
        if (foreign == null) {
            return null;
        }
        if (foreign instanceof CompoundTag) {
            Object tag = getInstance().getNMSProvider().newNBTTagCompound();
            for (Map.Entry<String, Tag> entry : ((CompoundTag) foreign).getValue().entrySet()) {
                getInstance().getNMSProvider().nbtTagCompound_put(tag, entry.getKey(), fromNative(entry.getValue()));
            }
            return tag;
        } else if (foreign instanceof ByteTag) {
            return getInstance().getNMSProvider().nbtTagByte_valueOf(((ByteTag) foreign).getValue());
        } else if (foreign instanceof ByteArrayTag) {
            return getInstance().getNMSProvider().newNBTTagByteArray(((ByteArrayTag) foreign).getValue());
        } else if (foreign instanceof DoubleTag) {
            return getInstance().getNMSProvider().nbtTagDouble_valueOf(((DoubleTag) foreign).getValue());
        } else if (foreign instanceof FloatTag) {
            return getInstance().getNMSProvider().nbtTagFloat_valueOf(((FloatTag) foreign).getValue());
        } else if (foreign instanceof IntTag) {
            return getInstance().getNMSProvider().nbtTagInt_valueOf(((IntTag) foreign).getValue());
        } else if (foreign instanceof IntArrayTag) {
            return getInstance().getNMSProvider().newNBTTagIntArray(((IntArrayTag) foreign).getValue());
        } else if (foreign instanceof ListTag) {
            AbstractList<Object> tag         = getInstance().getNMSProvider().newNBTTagList();
            ListTag              foreignList = (ListTag) foreign;
            for (Tag t : foreignList.getValue()) {
                tag.add(fromNative(t));
            }
            return tag;
        } else if (foreign instanceof LongTag) {
            return getInstance().getNMSProvider().nbtTagLong_valueOf(((LongTag) foreign).getValue());
        } else if (foreign instanceof ShortTag) {
            return getInstance().getNMSProvider().nbtTagShort_valueOf(((ShortTag) foreign).getValue());
        } else if (foreign instanceof StringTag) {
            return getInstance().getNMSProvider().nbtTagString_valueOf(((StringTag) foreign).getValue());
        } else if (foreign instanceof EndTag) {
            throw new IllegalArgumentException("Cant make EndTag: "
                    + foreign.getValue().toString());
        } else {
            throw new IllegalArgumentException("Don't know how to make NMS "
                    + foreign.getClass().getCanonicalName());
        }
    }
}
