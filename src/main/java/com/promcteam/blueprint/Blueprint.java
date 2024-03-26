package com.promcteam.blueprint;

import com.denizenscript.denizen.npc.traits.AssignmentTrait;
import com.denizenscript.denizen.objects.NPCTag;
import com.google.common.collect.MapMaker;
import com.promcteam.blueprint.hooks.DenizenSupport;
import com.promcteam.blueprint.hooks.citizens.BuilderTrait;
import com.promcteam.blueprint.listener.SelectionListener;
import com.promcteam.blueprint.listener.TraitListener;
import com.promcteam.blueprint.nms.NMS;
import com.promcteam.blueprint.schematic.RawSchematic;
import com.promcteam.blueprint.schematic.Schematic;
import com.promcteam.blueprint.schematic.YAMLSchematic;
import com.promcteam.codex.manager.api.menu.YAMLMenu;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.file.Path;
import java.util.Map;
import java.util.logging.Level;

public class Blueprint extends JavaPlugin {
    private static       Blueprint              instance;
    private static final Map<String, Schematic> schematics = new MapMaker().weakValues().makeMap();

    private Config           config;
    private BuildingRegistry buildingRegistry;
    private Plugin           denizen;

    @Override
    public void onEnable() {
        instance = this;
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
        reload();

        if (getServer().getPluginManager().getPlugin("Citizens") != null && getServer().getPluginManager()
                .getPlugin("Citizens")
                .isEnabled()) {
            getLogger().log(Level.INFO, "Citizens is now enabled");
        } else {
            getLogger().log(Level.SEVERE, "Citizens not found or not enabled");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        denizen = this.getServer().getPluginManager().getPlugin("Denizen");

        if (denizen == null) {
            getLogger().log(Level.INFO, "Blueprint could not register with Denizen");
        } else {
            getLogger().log(Level.INFO, "Blueprint registered sucessfully with Denizen");
        }

        getServer().getPluginManager().registerEvents(new TraitListener(), this);
        getServer().getPluginManager().registerEvents(new SelectionListener(), this);
    }

    public void reload() {
        this.config = new Config();
        (this.buildingRegistry = new BuildingRegistry()).load();
        if (!new File(config.getSchematicsFolder()).exists()) {
            saveResource("schematics/house.schem", false);
            saveResource("schematics/house.yml", false);
            saveResource("schematics/structure_house.nbt", false);
            saveResource("schematics/villager_house.schem", false);
        }
        YAMLMenu.reloadMenus(this);
    }

    public Config config() {return config;}

    public BuildingRegistry getBuildingRegistry() {return buildingRegistry;}

    public static BuilderTrait getBuilder(Entity ent) {
        if (ent == null) return null;
        NPC npc = net.citizensnpcs.api.CitizensAPI.getNPCRegistry().getNPC(ent);
        if (npc != null && npc.hasTrait(BuilderTrait.class)) {
            return npc.getTrait(BuilderTrait.class);
        }
        return null;
    }

    @Nullable
    public static BuilderTrait getBuilder(NPC npc) {
        if (npc != null && npc.hasTrait(BuilderTrait.class)) {
            return npc.getTrait(BuilderTrait.class);
        }
        return null;
    }

    @Nullable
    public static Schematic getSchematic(Path path) {
        if (!path.toFile().isFile()) {
            return null;
        }
        String    string    = path.toString();
        Schematic schematic = Blueprint.schematics.get(string);
        if (schematic instanceof YAMLSchematic) {
            return ((YAMLSchematic) schematic).copy();
        }
        if (schematic != null) {
            return schematic;
        }
        if (string.endsWith(".schem") || string.endsWith(".nbt")) {
            schematic = new RawSchematic(path);
        } else if (string.endsWith(".yml")) {
            schematic = new YAMLSchematic(path);
        }
        if (schematic == null) {
            return null;
        }
        Blueprint.schematics.put(schematic.getPath(), schematic);
        return schematic;
    }

    @Nullable
    public static Schematic getSchematic(String name) {
        return getSchematic(new File(Blueprint.getInstance().config().getSchematicsFolder(), name).toPath());
    }

    public static String runTask(String taskname, NPC npc) {
        try {
            if (instance.denizen == null) {
                return "Denizen plugin not found!";
            } else if (!DenizenSupport.runTask(taskname, npc)) {
                return "Task: " + taskname + " was not found!";
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return "Error while executing task: " + e.getMessage();
        }
    }

    public static void denizenAction(NPC npc, String action) {
        if (instance.denizen != null) {
            try {
                if (npc.hasTrait(AssignmentTrait.class)) {
                    NPCTag dnpc = new NPCTag(npc);
                    dnpc.action(action, null);
                }
            } catch (Exception e) {
                instance.getLogger().log(Level.WARNING, "Error running action!");
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDisable() {
        this.buildingRegistry.save();
        this.denizen = null;
        this.buildingRegistry = null;

        getLogger().log(Level.INFO, " v" + getDescription().getVersion() + " disabled.");
        Bukkit.getServer().getScheduler().cancelTasks(this);
        HandlerList.unregisterAll(this);
    }

    public static Blueprint getInstance() {return Blueprint.instance;}

    public static String format(String input,
                                NPC npc,
                                Schematic schem,
                                CommandSender player,
                                String item,
                                String amount) {
        input = input.replace("<NPC>", npc.getName());
        input = input.replace("<SCHEMATIC>", schem == null ? "" : schem.getDisplayName());
        input = input.replace("<PLAYER>", player == null ? "" : player.getName());
        input = input.replace("<ITEM>", item == null ? "" : item);
        input = input.replace("<AMOUNT>", amount);
        input = ChatColor.translateAlternateColorCodes('&', input);
        return input;
    }
}
