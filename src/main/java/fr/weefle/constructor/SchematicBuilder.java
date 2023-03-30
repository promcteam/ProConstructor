package fr.weefle.constructor;

import com.denizenscript.denizen.npc.traits.AssignmentTrait;
import com.denizenscript.denizen.objects.NPCTag;
import fr.weefle.constructor.NMS.NMS;
import fr.weefle.constructor.essentials.BuilderSchematic;
import fr.weefle.constructor.essentials.BuilderTrait;
import fr.weefle.constructor.extra.DenizenSupport;
import fr.weefle.constructor.extra.SelectionListener;
import fr.weefle.constructor.extra.TraitListener;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;


public class SchematicBuilder extends JavaPlugin {
	
	public static SchematicBuilder instance;

	public static String schematicsFolder = "";
	public static List<Material> MarkMats = new ArrayList<>();
	public String StartedMessage = "";
	public String  CompleteMessage = "";
	public static String  CancelMessage = "";
	public static String  MarkMessage = "";
	public static String SurveyMessage = "";
	public String SupplyListMessage = "";
	public String SupplyNeedMessage = "";
	public String SupplyDontNeedMessage = "";
	public String SupplyTakenMessage = "";
	public String CollectingMessage = "";


	@Override
	public void onEnable() {
		instance=this;
		NMS nms = new NMS();
		if (nms.setInstance()) {
            getLogger().info("NMS setup was successful!");
            getLogger().info("The plugin setup process is complete!");

        } else {

            getLogger().severe("Failed to setup NMS!");
            getLogger().severe("Your server version is not compatible with this plugin!");

            Bukkit.getPluginManager().disablePlugin(this);
        }
		
		/*try {
			new Metrics(this);
			getLogger().info("Metrics setup was successful");
			new Updater(this, 79683);
			getLogger().info("Updater setup was successful");
		} catch (IOException e) {
			getLogger().severe("Failed to setup Updater");
			getLogger().severe("Verify the resource's link");
			e.printStackTrace();
		}*/
		
		if(getServer().getPluginManager().getPlugin("Citizens") != null || getServer().getPluginManager().getPlugin("Citizens").isEnabled()) {
			getLogger().log(Level.INFO, "Citizens is now enabled");
		}else {
			getLogger().log(Level.SEVERE, "Citizens not found or not enabled");
			getServer().getPluginManager().disablePlugin(this);	
			return;
        }


        try {
            setupDenizenHook();
        } catch (Exception e) {

        }

        if (denizen != null) {
            getLogger().log(Level.INFO, "ProSchematicBuilder registered sucessfully with Denizen");
        } else { getLogger().log(Level.INFO, "ProSchematicBuilder could not register with Denizen"); }

        getServer().getPluginManager().registerEvents(new TraitListener(), this);
        getServer().getPluginManager().registerEvents(new SelectionListener(), this);
        reloadMyConfig();
    }



	public BuilderTrait getBuilder(Entity ent){
		if( ent == null) return null;
		NPC npc = net.citizensnpcs.api.CitizensAPI.getNPCRegistry().getNPC(ent);
		if (npc !=null && npc.hasTrait(BuilderTrait.class)){
			return npc.getTrait(BuilderTrait.class);
		}

		return null;
	}

	public BuilderTrait getBuilder(NPC npc){

		if (npc !=null && npc.hasTrait(BuilderTrait.class)){
			return npc.getTrait(BuilderTrait.class);
		}

		return null;

	}

	//***Denizen Hook
	private Plugin denizen = null;

	private void setupDenizenHook() throws Exception {
		denizen = this.getServer().getPluginManager().getPlugin("Denizen");
	}


	public String runTask(String taskname, NPC npc){
		return runTaskv9(taskname, npc);
	}

	private String runTaskv9(String taskname, NPC npc) {
        try {
            if (denizen == null) {
                return "Denizen plugin not found!";
            }
            else if (!DenizenSupport.runTask(taskname, npc)) {
                return "Task: " + taskname + " was not found!";
            }
            return null;
        }
        catch (Exception e) {
            e.printStackTrace();
            return "Error while executing task: " + e.getMessage();
        }
    }

	public void DenizenAction(NPC npc, String action){
		if(denizen!=null){
			try {
				if(npc.hasTrait(AssignmentTrait.class)){
					NPCTag dnpc = new NPCTag(npc);
					dnpc.action(action, null);			
				}
			} catch (Exception e) {
				getLogger().log(Level.WARNING, "Error running action!");
				e.printStackTrace();
			}		
		}
	}

	@Override
	public void onDisable() {
		getLogger().log(Level.INFO, " v" + getDescription().getVersion() + " disabled.");
		Bukkit.getServer().getScheduler().cancelTasks(this);
	}






	public void reloadMyConfig(){
		this.saveDefaultConfig();
		this.reloadConfig();
		schematicsFolder = getConfig().getString("SchematicsFolder",this.getDataFolder() + File.separator + "schematics" + File.separator);
		CompleteMessage = getConfig().getString("DefaultTexts.BuildComplete","");
		CancelMessage = getConfig().getString("DefaultTexts.BuildCanceled","");
		StartedMessage = getConfig().getString("DefaultTexts.BuildStarted","");
		CollectingMessage =  getConfig().getString("DefaultTexts.BuildCollecting","");
		MarkMessage = getConfig().getString("DefaultTexts.Mark","");
		SurveyMessage = getConfig().getString("DefaultTexts.Survey","");
		SupplyListMessage = getConfig().getString("DefaultTexts.Supply_List","");
		SupplyNeedMessage = getConfig().getString("DefaultTexts.Supply_Need_Item","");
		SupplyDontNeedMessage = getConfig().getString("DefaultTexts.Supply_Dont_Need_Item","");
		SupplyTakenMessage = getConfig().getString("DefaultTexts.Supply_Item_Taken","");
		for (String M:getConfig().getStringList("MarkMaterials")){
			if (M!=null) MarkMats.add(Material.valueOf(M));
		}

		if (MarkMats.isEmpty()) MarkMats.add(Material.GLASS);

		loadSchematic();

	}


	
	public void loadSchematic() {
        if (!(new File(this.getDataFolder()+File.separator+"schematics/house.schem").exists())) {
            saveResource("schematics/house.schem", false);
        }

        //if(!(new File(this.getDataFolder() + File.separator + "schematics/structure_house.nbt").exists()))
        //	saveResource("schematics/structure_house.nbt", false);
    }



	public static SchematicBuilder getInstance() {
		return SchematicBuilder.instance;
	}

	public String format(String input, NPC npc, BuilderSchematic schem, CommandSender player, String item, String amount){
		input = input.replace("<NPC>",npc.getName());
		input = input.replace("<SCHEMATIC>", schem == null ? "" : schem.Name);
		input = input.replace("<PLAYER>", player == null ? "" : player.getName());
		input = input.replace("<ITEM>", item == null ? "" : item);
		input = input.replace("<AMOUNT>", amount);
		input =	ChatColor.translateAlternateColorCodes('&', input);
		return input;
	}



}
