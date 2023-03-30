package fr.weefle.constructor.NMS;

import fr.weefle.constructor.API.SchematicChooser;
import fr.weefle.constructor.API.StructureUtil;
import fr.weefle.constructor.API.TileChecker;
import fr.weefle.constructor.API.Util;
import fr.weefle.constructor.NMS.Version_1_13_R2.SchematicChooser_1_13_R2;
import fr.weefle.constructor.NMS.Version_1_13_R2.TileChecker_1_13_R2;
import fr.weefle.constructor.NMS.Version_1_13_R2.Util_1_13_R2;
import fr.weefle.constructor.NMS.Version_1_14_R1.SchematicChooser_1_14_R1;
import fr.weefle.constructor.NMS.Version_1_14_R1.TileChecker_1_14_R1;
import fr.weefle.constructor.NMS.Version_1_14_R1.Util_1_14_R1;
import fr.weefle.constructor.NMS.Version_1_15_R1.SchematicChooser_1_15_R1;
import fr.weefle.constructor.NMS.Version_1_15_R1.TileChecker_1_15_R1;
import fr.weefle.constructor.NMS.Version_1_15_R1.Util_1_15_R1;
import fr.weefle.constructor.NMS.Version_1_16_R3.SchematicChooser_1_16_R3;
import fr.weefle.constructor.NMS.Version_1_16_R3.TileChecker_1_16_R3;
import fr.weefle.constructor.NMS.Version_1_16_R3.Util_1_16_R3;
import fr.weefle.constructor.NMS.Version_1_17_R1.SchematicChooser_1_17_R1;
import fr.weefle.constructor.NMS.Version_1_17_R1.TileChecker_1_17_R1;
import fr.weefle.constructor.NMS.Version_1_17_R1.Util_1_17_R1;
import fr.weefle.constructor.NMS.Version_1_18_R1.SchematicChooser_1_18_R1;
import fr.weefle.constructor.NMS.Version_1_18_R1.TileChecker_1_18_R1;
import fr.weefle.constructor.NMS.Version_1_18_R1.Util_1_18_R1;
import org.bukkit.Bukkit;

public class NMS {
	
	private static NMS instance;
	public String version;
	private Util util;
	private TileChecker checker;
	private SchematicChooser chooser;
	private StructureUtil structure;

	public boolean isSet() {
		setInstance(this);

	try {

		version = Bukkit.getServer().getClass().getPackage().getName().replace(".",  ",").split(",")[3];

	} catch (ArrayIndexOutOfBoundsException exception) {
		return false;
	}

	Bukkit.getLogger().info("Your server is running version " + version);

		switch (version) {
			case "v1_18_R1": case "v1_18_R2": case "v1_19_R1":
				setUtil(new Util_1_18_R1());
				setChecker(new TileChecker_1_18_R1());
				setChooser(new SchematicChooser_1_18_R1());
				break;
			case "v1_17_R1":
				setUtil(new Util_1_17_R1());
				setChecker(new TileChecker_1_17_R1());
				setChooser(new SchematicChooser_1_17_R1());
				break;
			case "v1_16_R3":
				setUtil(new Util_1_16_R3());
				setChecker(new TileChecker_1_16_R3());
				setChooser(new SchematicChooser_1_16_R3());
				break;
			case "v1_15_R1":
				setUtil(new Util_1_15_R1());
				setChecker(new TileChecker_1_15_R1());
				setChooser(new SchematicChooser_1_15_R1());
				break;
			case "v1_14_R1":
				setUtil(new Util_1_14_R1());
				setChecker(new TileChecker_1_14_R1());
				setChooser(new SchematicChooser_1_14_R1());
				break;
			case "v1_13_R2":
				setUtil(new Util_1_13_R2());
				setChecker(new TileChecker_1_13_R2());
				setChooser(new SchematicChooser_1_13_R2());

				break;
			default:
				return false;
		}

	return true;
	}

	public static NMS getInstance() {
		return instance;
	}

	public static void setInstance(NMS instance) {
		NMS.instance = instance;
	}

	public Util getUtil() {
		return util;
	}

	public void setUtil(Util util) {
		this.util = util;
	}
	
	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
	
	public TileChecker getChecker() {
		return checker;
	}

	public void setChecker(TileChecker checker) {
		this.checker = checker;
	}
	
	public SchematicChooser getChooser() {
		return chooser;
	}

	public void setChooser(SchematicChooser chooser) {
		this.chooser = chooser;
	}

	public static Class< ? > getNMSClass ( String classname )
	{
		String version = Bukkit.getServer ( ).getClass ( ).getPackage ( ).getName ( ).replace ( ".", "," ).split ( "," )[ 3 ] + ".";
		String name = "org.bukkit.craftbukkit." + version + classname;
		Class< ? > nmsClass = null;
		try
		{
			nmsClass = Class.forName ( name );
		} catch ( ClassNotFoundException e )
		{
			e.printStackTrace ( );
		}
		return nmsClass;
	}

}
