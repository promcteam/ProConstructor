package fr.weefle.constructor.nms;

import fr.weefle.constructor.api.StructureUtil;
import fr.weefle.constructor.nms.providers.*;
import org.bukkit.Bukkit;

public class NMS {

    private static NMS                                        instance;
    public         String                                     version;
    private        NMSProvider                                nmsProvider;
    private        fr.weefle.constructor.api.Util             util;
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
        setUtil(new NMSUtil());
        setChecker(new TileChecker());
        NMS.instance = this;
        return true;
    }

    public static NMS getInstance() {
        return instance;
    }

    public NMSProvider getNMSProvider() {return nmsProvider;}

    public fr.weefle.constructor.api.Util getUtil() {
        return util;
    }

    public void setUtil(fr.weefle.constructor.api.Util util) {
        this.util = util;
    }

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

}
