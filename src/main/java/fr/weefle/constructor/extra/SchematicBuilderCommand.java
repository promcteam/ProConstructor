package fr.weefle.constructor.extra;

import fr.weefle.constructor.API.StructureUtil;
import fr.weefle.constructor.NMS.NMS;
import fr.weefle.constructor.SchematicBuilder;
import fr.weefle.constructor.essentials.BuilderListener;
import fr.weefle.constructor.essentials.BuilderTrait;
import fr.weefle.constructor.util.Structure;
import mc.promcteam.engine.utils.ItemUT;
import net.citizensnpcs.Citizens;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Owner;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.util.Vector;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Objects;
import java.util.logging.Level;

public class SchematicBuilderCommand implements CommandExecutor {

    private       int scheduler;
    public static NPC ThisNPC;

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] inargs) {

        if (inargs.length < 1) {
            sender.sendMessage(ChatColor.RED + "Use /schematicbuilder help for command reference.");
            return true;
        }

        CommandSender player = sender;

        int npcid = -1;
        int i     = 0;

        //did player specify a id?
        if (tryParseInt(inargs[0])) {
            npcid = Integer.parseInt(inargs[0]);
            i = 1;
        }

        String[] args = new String[inargs.length - i];

        for (int j = i; j < inargs.length; j++) {
            args[j - i] = inargs[j];
        }


        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Use /schematicbuilder help for command reference.");
            return true;
        }


        if (args[0].equalsIgnoreCase("help")) {
            player.sendMessage("");
            player.sendMessage(ChatColor.GOLD + "------- ProSchematicBuilder Commands -------");
            player.sendMessage("");
            player.sendMessage(ChatColor.GREEN + "You can use /schematicbuilder (id) [command] [args] to perform any of these commands on a builder without having it selected.");
            player.sendMessage("");
            player.sendMessage(ChatColor.BLUE + "/schematicbuilder reload");
            player.sendMessage(ChatColor.AQUA + "  Reload the config.yml");
            player.sendMessage(ChatColor.BLUE + "/schematicbuilder load [schematic]");
            player.sendMessage(ChatColor.AQUA + "  Loads a schematic file");
            player.sendMessage(ChatColor.BLUE + "/schematicbuilder origin");
            player.sendMessage(ChatColor.AQUA + "  Sets the build origin to the builder's current location");
            player.sendMessage(ChatColor.BLUE + "/schematicbuilder origin clear");
            player.sendMessage(ChatColor.AQUA + "  Clears the build origin.");
            player.sendMessage(ChatColor.BLUE + "/schematicbuilder origin schematic");
            player.sendMessage(ChatColor.AQUA + "  Sets the build origin to the loaded schematic's original position");
            player.sendMessage(ChatColor.BLUE + "/schematicbuilder origin me");
            player.sendMessage(ChatColor.AQUA + "  Sets the build origin to your current location");
            player.sendMessage(ChatColor.BLUE + "/schematicbuilder origin current");
            player.sendMessage(ChatColor.AQUA + "  If the builder is currently building, sets the origin to the starting position of the current project.");
            player.sendMessage(ChatColor.BLUE + "/schematicbuilder origin x,y,z");
            player.sendMessage(ChatColor.AQUA + "  Sets the builder's origin to x,y,z of the current world.");
            player.sendMessage(ChatColor.BLUE + "/schematicbuilder mark (item)");
            player.sendMessage(ChatColor.AQUA + "  marks the 4 corners of the footprint. Optionally specify the material name or id.");
            player.sendMessage(ChatColor.BLUE + "/schematicbuilder build (ignoreair) (ignorewater) (excavate) (layers:#) (groupall) (reversespiral) (linear) (reverselinear) (yoffset:#) (offset)");
            player.sendMessage(ChatColor.AQUA + "  Begin building with the selected options.");
            player.sendMessage(ChatColor.BLUE + "/schematicbuilder cancel");
            player.sendMessage(ChatColor.AQUA + "  Cancel building");
            player.sendMessage(ChatColor.BLUE + "/schematicbuilder survey (excavate)");
            player.sendMessage(ChatColor.AQUA + "  View the list of materials required to build the loaded schematic at the current origin with the specified options.");
            player.sendMessage(ChatColor.BLUE + "/schematicbuilder timeout [0.1 - 1.0]");
            player.sendMessage(ChatColor.AQUA + "  Sets the maximum number of seconds between blocks");
            player.sendMessage(ChatColor.BLUE + "/schematicbuilder supply [true/false]");
            player.sendMessage(ChatColor.AQUA + "  set whether the builder needs to be supplied with materials before building.");
            player.sendMessage(ChatColor.BLUE + "/schematicbuilder hold [true/false]");
            player.sendMessage(ChatColor.AQUA + "  Set whether the builder holds blocks while building.");
            player.sendMessage(ChatColor.BLUE + "/schematicbuilder structure [name]");
            player.sendMessage(ChatColor.AQUA + "  Save named structure with WorldEdit region selection.");
            player.sendMessage(ChatColor.BLUE + "/schematicbuilder preview");
            player.sendMessage(ChatColor.AQUA + "  Shows the preview of the current structure.");
            player.sendMessage(ChatColor.BLUE + "/schematicbuilder list");
            player.sendMessage(ChatColor.AQUA + "  Shows the list of all schematics and structures.");
            player.sendMessage(ChatColor.BLUE + "/schematicbuilder npc");
            player.sendMessage(ChatColor.AQUA + "  Select the NPC which will build something for you.");
            player.sendMessage(ChatColor.BLUE + "/schematicbuilder excavated");
            player.sendMessage(ChatColor.AQUA + "  Get all excavated blocks from current build.");
            player.sendMessage("");
            return true;
        } else if (args[0].equalsIgnoreCase("reload")) {
            if (!player.hasPermission("schematicbuilder.reload")) {
                player.sendMessage(ChatColor.RED + "You do not have permissions for that command.");
                return true;
            }

            SchematicBuilder.getInstance().reloadMyConfig();
            player.sendMessage(ChatColor.GREEN + "reloaded ProSchematicBuilder/config.yml");
            return true;
        } else if (args[0].equalsIgnoreCase("npc")) {
            if (!player.hasPermission("schematicbuilder.npc")) {
                player.sendMessage(ChatColor.RED + "You do not have permissions for that command.");
                return true;
            }

            if (player instanceof Player) {
                Player    p   = (Player) player;
                Inventory inv = Bukkit.createInventory(p, 54, "ProSchematicBuilder - NPCs");
                BuilderListener.materials = new HashMap<>();
                for (NPC npc : CitizensAPI.getNPCRegistry().sorted()) {
                    BuilderListener.materials.put(npc.getName(), 1);
                }
                int nb = 0;
                for (String npcName : BuilderListener.materials.keySet()) {
                    int count = BuilderListener.materials.get(npcName);

                    if (nb < 53) {
                        if (count > 0) {
                            ItemStack is = getHead(npcName);
                            ItemMeta  im = is.getItemMeta();
                            assert im != null;
                            im.setDisplayName(npcName);
                            ArrayList<String> Lore = new ArrayList<String>();
                            for (NPC npcc : CitizensAPI.getNPCRegistry().sorted()) {
                                if (npcc.getName().equals(npcName)) {
                                    if (npcc.hasTrait(BuilderTrait.class)) {
                                        Lore.add("This NPC can build.");
                                        Lore.add("Right-click to remove builder's trait");
                                        Lore.add("Left-click to enter parameters");
                                    } else {
                                        Lore.add("This NPC can't build!");
                                        Lore.add("Right-click to add builder's trait");
                                    }
                                }
                            }
                            im.setLore(Lore);
                            is.setItemMeta(im);
                            inv.setItem(nb, is);
                            nb++;
                        }
                    }
                    BuilderListener.materials.put(npcName, 0);
                    if (nb == 53) {
                        ItemStack ims = getItem("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGVmMzU2YWQyYWE3YjE2NzhhZWNiODgyOTBlNWZhNWEzNDI3ZTVlNDU2ZmY0MmZiNTE1NjkwYzY3NTE3YjgifX19");
                        inv.setItem(nb, ims);
                        break;
                    }

                }
                p.openInventory(inv);

            }


            return true;
        } else if (args[0].equalsIgnoreCase("list")) {
            if (!player.hasPermission("schematicbuilder.list")) {
                player.sendMessage(ChatColor.RED + "You do not have permissions for that command.");
                return true;
            }

            // Directory path here
            String path = SchematicBuilder.schematicsFolder;

            File   folder      = new File(path);
            File[] listOfFiles = folder.listFiles();

            assert listOfFiles != null;
            if (listOfFiles.length == 0) {
                player.sendMessage(ChatColor.RED + "No schematics found.");
                return true;
            }

            if (player instanceof Player) {
                Player p = (Player) player;

                Inventory inv = Bukkit.createInventory(p, 54, "ProSchematicBuilder - Schematics");
                BuilderListener.materials = new HashMap<>();
                for (File file : listOfFiles) {
                    BuilderListener.materials.put(file.getName(), 1);
                }
                int nb = 0;
                for (String fileName : BuilderListener.materials.keySet()) {
                    int count = BuilderListener.materials.get(fileName);
                    if (nb < 53) {
                        if (count > 0) {
                            ItemStack is = new ItemStack(Material.MAP);
                            ItemMeta  im = is.getItemMeta();
                            assert im != null;
                            im.setDisplayName(fileName);
                            is.setItemMeta(im);
                            inv.setItem(nb, is);
                            nb++;
                        }
                    }
                    BuilderListener.materials.put(fileName, 0);
                    if (nb == 53) {
                        ItemStack ims = getItem("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGVmMzU2YWQyYWE3YjE2NzhhZWNiODgyOTBlNWZhNWEzNDI3ZTVlNDU2ZmY0MmZiNTE1NjkwYzY3NTE3YjgifX19");
                        inv.setItem(nb, ims);
                        break;
                    }

                }
                p.openInventory(inv);
            }

            return true;
        } else if (args[0].equalsIgnoreCase("structure")) {
            if (!player.hasPermission("schematicbuilder.structure")) {
                player.sendMessage(ChatColor.RED + "You do not have permissions for that command.");
                return true;
            }

            /*Plugin we = Bukkit.getPluginManager().getPlugin("WorldEdit");
            if(we instanceof WorldEditPlugin) {*/

            if (args.length == 2) {

                String name = args[1];


                Vector vec = null;

                if (sender instanceof Player) {
                    Player p = (Player) sender;
                    try {
                        // vec = WorldEditManager.getWorldEditSelection(p);
                        // Bukkit.getLogger().warning(SelectionListener.vector.get(p).toString());
                        vec = SelectionListener.vector.get(p);
                        //Bukkit.getLogger().warning(vec.toString());
                    } catch (NullPointerException e) {
                        player.sendMessage(ChatColor.RED + "You have not selected any region!");
                        return true;
                    }

                } else {
                    return false;
                }


                Player p = Bukkit.getPlayer(player.getName());
                try {
                    //NMS.getInstance().getStructure().save(WorldEditManager.getWorldEditLocation(p), vec, name);
                    // Bukkit.getLogger().warning(SelectionListener.location.get(p).toString());
                    StructureUtil.save(SelectionListener.location.get(p), vec, name);
                } catch (NullPointerException e) {
                    player.sendMessage(ChatColor.RED + "You have not selected any region!");
                    return true;
                }
                player.sendMessage(ChatColor.GREEN + "Successfully saved structure " + ChatColor.WHITE + name + ChatColor.GREEN + " in schematics folder.");
            } else {
                player.sendMessage(ChatColor.RED + "Too less arguments! Use this command: /schematicbuilder structure [name]");
                return true;
            }/*}else{
                player.sendMessage(ChatColor.RED + "You need WorldEdit to use this feature!");
                return true;
            }*/

            return true;

        }

        if (npcid == -1) {

            ThisNPC = ((Citizens) Bukkit.getServer().getPluginManager().getPlugin("Citizens")).getNPCSelector().getSelected(sender);

            if (ThisNPC != null) {
                // Gets NPC Selected
                npcid = ThisNPC.getId();
            } else {
                player.sendMessage(ChatColor.RED + "You must have a NPC selected to use this command");
                return true;
            }
        }

        ThisNPC = CitizensAPI.getNPCRegistry().getById(npcid);

        if (ThisNPC == null) {
            player.sendMessage(ChatColor.RED + "NPC with id " + npcid + " not found");
            return true;
        }

        if (!ThisNPC.hasTrait(BuilderTrait.class)) {
            player.sendMessage(ChatColor.RED + "That command must be performed on a builder!");
            return true;
        }


        if (sender instanceof ConsoleCommandSender && !CitizensAPI.getNPCRegistry().isNPC((Entity) sender)) {
            if (ThisNPC.getTrait(Owner.class).getOwner().equalsIgnoreCase(player.getName())) {
                //OK!
            } else {
                //not player is owner
                if (!sender.hasPermission("citizens.admin")) {
                    //no c2 admin.
                    player.sendMessage(ChatColor.RED + "You must be the owner of this Sentry to execute commands.");
                    return true;
                } else {
                    //has citizens.admin
                    if (!ThisNPC.getTrait(Owner.class).getOwner().equalsIgnoreCase("SERVER")) {
                        //not server-owned NPC
                        player.sendMessage(ChatColor.RED + "You, or the server, must be the owner of this NPC to execute commands.");
                        return true;
                    }
                }
            }
        }

        BuilderTrait inst = SchematicBuilder.getInstance().getBuilder(ThisNPC);

        // Commands
        if (args[0].equalsIgnoreCase("build")) {
            if (!player.hasPermission("schematicbuilder.build")) {
                player.sendMessage(ChatColor.RED + "You do not have permissions for that command.");
                return true;
            }

            if (inst.State == BuilderTrait.BuilderState.building) {
                if (!inst.Silent)
                    player.sendMessage(ChatColor.RED + ThisNPC.getName() + " is already building!"); // Talk to the player.
                return true;
            }

            inst.oncancel = null;
            inst.oncomplete = null;
            inst.onStart = null;
            inst.ContinueLoc = null;
            inst.IgnoreAir = false;
            inst.IgnoreLiquid = false;
            inst.Excavate = false;
            inst.GroupByLayer = true;
            inst.BuildYLayers = 1;
            inst.Silent = false;
            inst.BuildPatternXY = BuilderTrait.BuildPatternsXZ.spiral;

            for (int a = 0; a < args.length; a++) {
                if (args[a].equalsIgnoreCase("silent")) {
                    if (args[a].equalsIgnoreCase("silent")) {
                        inst.Silent = true;
                    }
                }
            }

            for (int a = 0; a < args.length; a++) {
                if (args[a].toLowerCase().contains("oncomplete:")) {
                    inst.oncomplete = args[a].split(":")[1];
                    if (!inst.Silent)
                        player.sendMessage(ChatColor.GREEN + ThisNPC.getName() + " will run task " + inst.oncomplete + " on build completion");
                } else if (args[a].toLowerCase().contains("oncancel:")) {
                    inst.oncancel = args[a].split(":")[1];
                    if (!inst.Silent)
                        player.sendMessage(ChatColor.GREEN + ThisNPC.getName() + " will run task " + inst.oncancel + " on build cancelation");
                } else if (args[a].toLowerCase().contains("onstart:")) {
                    inst.onStart = args[a].split(":")[1];
                    if (!inst.Silent)
                        player.sendMessage(ChatColor.GREEN + ThisNPC.getName() + " will run task " + inst.onStart + " on when building starts");
                } else if (args[a].toLowerCase().contains("layers:")) {
                    String test = args[a].split(":")[1];
                    if (tryParseInt(test)) {
                        int layers = Integer.parseInt(test);
                        if (layers < 1) layers = 1;
                        if (layers > Integer.MAX_VALUE) layers = Integer.MAX_VALUE;
                        inst.BuildYLayers = layers;
                    }
                } else if (args[a].toLowerCase().contains("yoffset:")) {
                    String test = args[a].split(":")[1];
                    if (tryParseInt(test)) {
                        int layers = Integer.parseInt(test);
                        inst.Yoffset = layers;
                    }
                } else if (args[a].equalsIgnoreCase("groupall")) {
                    inst.GroupByLayer = false;
                } else if (args[a].equalsIgnoreCase("ignoreair")) {
                    inst.IgnoreAir = true;
                } else if (args[a].equalsIgnoreCase("ignoreliquid")) {
                    inst.IgnoreLiquid = true;
                } else if (args[a].equalsIgnoreCase("excavate")) {
                    inst.Excavate = true;
                    if (!inst.Silent) player.sendMessage(ChatColor.GREEN + ThisNPC.getName() + " will excavate first");
                } else if (args[a].equalsIgnoreCase("spiral")) {
                    inst.BuildPatternXY = BuilderTrait.BuildPatternsXZ.spiral;
                } else if (args[a].equalsIgnoreCase("reversespiral")) {
                    inst.BuildPatternXY = BuilderTrait.BuildPatternsXZ.reversespiral;
                } else if (args[a].equalsIgnoreCase("linear")) {
                    inst.BuildPatternXY = BuilderTrait.BuildPatternsXZ.linear;
                } else if (args[a].equalsIgnoreCase("reverselinear")) {
                    inst.BuildPatternXY = BuilderTrait.BuildPatternsXZ.reverselinear;
                } else if (args[a].equalsIgnoreCase("offset")) {
                    inst.offset = true;
                }
            }

            if (inst.RequireMaterials) {
                inst.GetMatsList(inst.Excavate);
            }

            inst.TryBuild(player);

            return true;

        } else if (args[0].equalsIgnoreCase("cancel")) {
            if (!player.hasPermission("schematicbuilder.cancel")) {
                player.sendMessage(ChatColor.RED + "You do not have permissions for that command.");
                return true;
            }

            if (inst.State != BuilderTrait.BuilderState.idle) {
                sender.sendMessage(SchematicBuilder.getInstance().format(SchematicBuilder.CancelMessage, ThisNPC, inst.schematic, sender, null, "0"));
            } else {
                player.sendMessage(ChatColor.RED + ThisNPC.getName() + " is not building.");   // Talk to the player.
            }

            inst.CancelBuild();
            return true;

        } else if (args[0].equalsIgnoreCase("excavated")) {
            if (!player.hasPermission("schematicbuilder.excavated")) {
                player.sendMessage(ChatColor.RED + "You do not have permissions for that command.");
                return true;
            }

            if (inst.State == BuilderTrait.BuilderState.building && inst.Excavate) {

                if (!inst.ExcavateMaterials.isEmpty()) {
                    BlockData[] array = new BlockData[inst.ExcavateMaterials.size()];
                    inst.ExcavateMaterials.toArray(array); // fill the array
                    for (BlockData bda : array) {
                        Bukkit.getOnlinePlayers().stream().filter(player1 -> player1.getName().equals(player.getName())).findFirst().get().getInventory().addItem(new ItemStack(bda.getMaterial()));
                    }
                    inst.ExcavateMaterials = new LinkedList<>();
                    player.sendMessage(ChatColor.GREEN + ThisNPC.getName() + " gave you all excavated blocks !");   // Talk to the player.
                } else {
                    player.sendMessage(ChatColor.RED + ThisNPC.getName() + " already gave you all excavated blocks !");   // Talk to the player.
                }
                return true;


            }


        } else if (args[0].equalsIgnoreCase("preview")) {
            if (!player.hasPermission("schematicbuilder.preview")) {
                player.sendMessage(ChatColor.RED + "You do not have permissions for that command.");
                return true;
            }
            if (sender instanceof Player) {
                Player   p      = (Player) sender;
                Location tmpLoc = ThisNPC.getEntity().getLocation();
                //Bukkit.getLogger().warning(tmpLoc.toString());

                if (inst.State == BuilderTrait.BuilderState.idle) {
                    if (inst.schematic != null) {

                        HashMap<Location, BlockData> blocks = new HashMap<>();

                        for (int x = 0; x < inst.schematic.width(); ++x) {
                            for (int y = 0; y < inst.schematic.height(); ++y) {
                                for (int z = 0; z < inst.schematic.length(); ++z) {

                                    Location loc = tmpLoc.clone().add(x, y, z);

                                    BlockData bdata = inst.schematic.Blocks[x][y][z].getMat();
                                    blocks.put(loc, bdata);
                                    p.sendBlockChange(loc, bdata);

                                }
                            }
                        }

                        Bukkit.getScheduler().scheduleSyncDelayedTask(SchematicBuilder.getInstance(), new Runnable() {
                            @Override
                            public void run() {
                                for (Location loc : blocks.keySet()) {

                                    Objects.requireNonNull(Bukkit.getServer().getWorld(p.getWorld().getName())).getBlockAt(loc).getState().update();
                                }
                            }

                        }, 100);

                        /*double time = 0.1;
                            scheduler = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Constructor.getInstance(), new Runnable() {
                                Iterator<Location> it = blocks.keySet().iterator();
                                @Override
                                public void run() {
                                    if(it.hasNext()) {
                                        Location loc = it.next();
                                        p.sendBlockChange(loc, blocks.get(loc));
                                    }else{
                                        Bukkit.getScheduler().cancelTask(scheduler);
                                        Bukkit.getScheduler().scheduleSyncDelayedTask(Constructor.getInstance(), new Runnable() {
                                            @Override
                                            public void run() {
                                                for (Location loc : blocks.keySet()) {

                                                    Objects.requireNonNull(Bukkit.getServer().getWorld(p.getWorld().getName())).getBlockAt(loc).getState().update();
                                                }
                                            }

                                        }, 100);
                                    }
                                }
                            }, 0, (long) 0.01);*/

                        player.sendMessage(ChatColor.GREEN + ThisNPC.getName() + " loaded a preview of the current structure.");   // Talk to the player.
                        return true;
                    }
                }

            }


        } else if (args[0].equalsIgnoreCase("survey")) {
            if (!player.hasPermission("schematicbuilder.survey")) {
                player.sendMessage(ChatColor.RED + "You do not have permissions for that command.");
                return true;
            }

            boolean ex = false;

            for (int a = 0; a < args.length; a++) {
                if (args[a].toLowerCase().contains("excavate")) {
                    ex = true;
                }
            }
            if (inst.schematic == null) {
                player.sendMessage(ChatColor.RED + "No Schematic Loaded!");   // Talk to the player.
            } else {
                sender.sendMessage(SchematicBuilder.getInstance().format(SchematicBuilder.SurveyMessage + (ex
                        ? " (excavate)"
                        : ""), ThisNPC, inst.schematic, sender, null, "0"));
                player.sendMessage(inst.GetMatsList(ex));   // Talk to the player.
            }

            return true;

        } else if (args[0].equalsIgnoreCase("origin")) {
            if (!player.hasPermission("schematicbuilder.origin")) {
                player.sendMessage(ChatColor.RED + "You do not have permissions for that command.");
                return true;
            }

            if (args.length <= 1) {

                if (inst.getNPC().isSpawned()) {
                    inst.Origin = inst.getNPC().getEntity().getLocation();
                    player.sendMessage(ChatColor.GREEN + ThisNPC.getName() + " build origin has been set to its current location.");   // Talk to the player.
                } else player.sendMessage(ChatColor.RED + ThisNPC.getName() + " not spawned.");
            } else {
                if (args[1].equalsIgnoreCase("clear")) {
                    inst.Origin = null;
                    player.sendMessage(ChatColor.GREEN + ThisNPC.getName() + " build origin has been cleared");   // Talk to the player.
                } else if (args[1].equalsIgnoreCase("schematic")) {
                    if (inst.schematic == null) {
                        player.sendMessage(ChatColor.RED + ThisNPC.getName() + " has no schematic loaded!");   // Talk to the player.
                        return true;
                    }
                    if (inst.schematic.SchematicOrigin == null) {
                        player.sendMessage(ChatColor.RED + inst.schematic.Name + " has no origin data!");   // Talk to the player.
                        return true;
                    }
                    inst.Origin = inst.schematic.getSchematicOrigin(inst);
                    player.sendMessage(ChatColor.GREEN + ThisNPC.getName() + " build origin has been set to:." + inst.Origin);   // Talk to the player.
                } else if (args[1].equalsIgnoreCase("me")) {
                    if (player instanceof Player) {
                        inst.Origin = ((Player) player).getLocation().clone();
                        player.sendMessage(ChatColor.GREEN + ThisNPC.getName() + " build origin has been set to your location");   // Talk to the player.
                    } else player.sendMessage(ChatColor.RED + "This command can only be used in-game");
                } else if (args[1].equalsIgnoreCase("current")) {
                    if (inst.State == BuilderTrait.BuilderState.building) {
                        inst.Origin = inst.ContinueLoc.clone();
                        player.sendMessage(ChatColor.GREEN + ThisNPC.getName() + " build origin has been set to the origin of the current build");   // Talk to the player.
                    } else player.sendMessage(ChatColor.RED + ThisNPC.getName() + " is not currently building!");
                } else if (args[1].split(",").length == 3) {
                    try {
                        int x = Integer.parseInt(args[1].split(",")[0]);
                        int y = Integer.parseInt(args[1].split(",")[1]);
                        int z = Integer.parseInt(args[1].split(",")[2]);

                        inst.Origin = new Location(inst.getNPC().getEntity().getWorld(), x, y, z);

                        player.sendMessage(ChatColor.GREEN + ThisNPC.getName() + " build origin has been set to " + inst.Origin.toString());   // Talk to the player.
                    } catch (Exception e) {
                        player.sendMessage(ChatColor.RED + "Invalid Coordinates");
                    }
                } else player.sendMessage(ChatColor.RED + "Unknown origin command");
            }
            return true;
        } else if (args[0].equalsIgnoreCase("mark")) {
            if (!player.hasPermission("schematicbuilder.mark")) {
                player.sendMessage(ChatColor.RED + "You do not have permissions for that command.");
                return true;
            }

            Material mat = null;
            if (args.length > 1) {
                mat = Material.valueOf(args[1].toUpperCase());
                if (!SchematicBuilder.MarkMats.contains(mat)) {
                    mat = null;
                    player.sendMessage(ChatColor.GOLD + ThisNPC.getName() + " can not mark with " + args[1] + ".The specified item is not allowed. Using default.");   // Talk to the player.
                }
            }


            if (mat == null) mat = SchematicBuilder.MarkMats.get(0);

            if (inst.StartMark(mat)) {
                sender.sendMessage(SchematicBuilder.getInstance().format(SchematicBuilder.MarkMessage, ThisNPC, inst.schematic, sender, null, "0"));
            } else {
                player.sendMessage(ChatColor.RED + ThisNPC.getName() + " could not mark. Already building or no schematic loaded?.");   // Talk to the player.
            }

            return true;

        } else if (args[0].equalsIgnoreCase("load")) {
            if (!player.hasPermission("schematicbuilder.load")) {
                player.sendMessage(ChatColor.RED + "You do not have permissions for that command.");
                return true;
            }

            if (inst.State != BuilderTrait.BuilderState.idle) {
                player.sendMessage(ChatColor.RED + "Please cancel current build before loading new schematic.");
                return true;
            }

            if (args.length > 1) {

                String arg = "";
                for (i = 1; i < args.length; i++) {
                    arg += " " + args[i];
                }
                arg = arg.trim();

                arg = arg.replace(".schem", "");
                arg = arg.replace(".nbt", "");
                String msg = "";
                File   dir = new File(SchematicBuilder.schematicsFolder);
                File   file;
                file = new File(dir, arg + ".schem");

                if (!file.exists()) {
                    file = new File(dir, arg + ".nbt");
                    if (!file.exists()) {
                        file = new File(dir, arg + ".schem or " + arg + ".nbt");
                    }
                }

                //see if this has already been loaded to another constructor
                for (NPC npc : CitizensAPI.getNPCRegistry()) {
                    if (npc.hasTrait(BuilderTrait.class)) {
                        BuilderTrait bt = npc.getTrait(BuilderTrait.class);
                        if (bt.schematic != null && bt.schematic.Name.equals(arg)) {
                            inst.schematic = bt.schematic;
                        }
                    }
                }

                //load it from file if not found.
                if (inst.schematic == null) ;
                try {

                    File fil;
                    fil = new File(dir, arg + ".schem");

                    if (!fil.exists()) {
                        fil = new File(dir, arg + ".nbt");
                        if (!fil.exists()) {
                            throw (new java.io.FileNotFoundException("File not found"));

                        } else {

                            inst.schematic = new Structure(dir, arg).load(dir, arg);
                        }

                    } else {
                        inst.schematic = NMS.getInstance().getChooser().setSchematic(dir, arg);

                    }

					/*if(NMS.getInstance().getVersion().equals("v1_15_R1")){
					MCEditSchematicFormat_1_15_R1 format = new MCEditSchematicFormat_1_15_R1();
					inst.schematic = format.load(dir, arg);
					}else if(NMS.getInstance().getVersion().equals("v1_14_R1")) {
						MCEditSchematicFormat_1_14_R1 format = new MCEditSchematicFormat_1_14_R1();
						inst.schematic = format.load(dir, arg);
					}
					else if(NMS.getInstance().getVersion().equals("v1_13_R2")) {
						MCEditSchematicFormat_1_13_R2 format = new MCEditSchematicFormat_1_13_R2();
						inst.schematic = format.load(dir, arg);
					}*/
                } catch (Exception e) {
                    msg = ChatColor.YELLOW + e.getMessage();   // Talk to the player.
                    inst.schematic = null;
                    if (!(e instanceof java.io.FileNotFoundException)) {
                        Bukkit.getLogger().log(Level.WARNING, "ProSchematicBuilder encountered an error attempting to load: " + file);
                        e.printStackTrace();
                    }
                }

                if (inst.schematic != null) {
                    inst.SchematicName = inst.schematic.Name;
                    player.sendMessage(ChatColor.GREEN + "Loaded Sucessfully");   // Talk to the player.
                    player.sendMessage(inst.schematic.GetInfo());

                } else {
                    player.sendMessage(ChatColor.RED + ThisNPC.getName() + " could not load " + file + " " + msg);   // Talk to the player.
                    player.sendMessage(ChatColor.RED + "Check the server console or logs for more informations!");
                }
            } else {
                player.sendMessage(ChatColor.RED + "You must specify a schematic");   // Talk to the player.
            }
            return true;
        } else if (args[0].equalsIgnoreCase("timeout")) {
            if (!player.hasPermission("schematicbuilder.timeout")) {
                player.sendMessage(ChatColor.RED + "You do not have permissions for that command.");
                return true;
            }
            if (args.length <= 1) {
                player.sendMessage(ChatColor.GOLD + ThisNPC.getName() + "'s Move Timeout is " + inst.MoveTimeout);
                player.sendMessage(ChatColor.GOLD + "Usage: /schematicbuilder timeout [0.1 - 2000000.0]");
            } else {

                Double HPs = Double.valueOf(args[1]);
                if (HPs > 2000000) HPs = 2000000.0;
                if (HPs < 0.0) HPs = 0.1;

                player.sendMessage(ChatColor.GREEN + ThisNPC.getName() + " move timeout set to " + HPs + ".");   // Talk to the player.
                inst.MoveTimeout = HPs;

            }

            return true;
        } else if (args[0].equalsIgnoreCase("supply")) {
            if (!player.hasPermission("schematicbuilder.supply")) {
                player.sendMessage(ChatColor.RED + "You do not have permissions for that command.");
                return true;
            }
            if (args.length <= 1) {
                player.sendMessage(ChatColor.GOLD + ThisNPC.getName() + " currently does" + (inst.RequireMaterials
                        ? ""
                        : " NOT") + " need to be supplied with materials.");
                player.sendMessage(ChatColor.GOLD + "Usage: /schematicbuilder supply [true/false]");
            } else {

                Boolean HPs = Boolean.valueOf(args[1]);

                inst.RequireMaterials = HPs;
                player.sendMessage(ChatColor.GOLD + ThisNPC.getName() + " now does" + (inst.RequireMaterials
                        ? ""
                        : " NOT") + " need to be supplied with materials.");


            }
            return true;
        } else if (args[0].equalsIgnoreCase("hold")) {
            if (!player.hasPermission("schematicbuilder.hold")) {
                player.sendMessage(ChatColor.RED + "You do not have permissions for that command.");
                return true;
            }
            if (args.length <= 1) {
                player.sendMessage(ChatColor.GOLD + ThisNPC.getName() + " currently does" + (inst.HoldItems
                        ? ""
                        : " NOT") + " hold blocks.");
                player.sendMessage(ChatColor.GOLD + "Usage: /schematicbuilder hold [true/false]");
            } else {

                Boolean HPs = Boolean.valueOf(args[1]);
                inst.HoldItems = HPs;
                player.sendMessage(ChatColor.GOLD + ThisNPC.getName() + " now does" + (inst.HoldItems
                        ? ""
                        : " NOT") + " hold blocks.");

            }
            return true;
        } else if (args[0].equalsIgnoreCase("info")) {
            if (!player.hasPermission("schematicbuilder.info")) {
                player.sendMessage(ChatColor.RED + "You do not have permissions for that command.");
                return true;
            }
            player.sendMessage(ChatColor.GOLD + "------- Builder Info for " + ThisNPC.getName() + "------");

            //	DecimalFormat df=  new DecimalFormat("#");

            if (inst.schematic != null) player.sendMessage(ChatColor.GREEN + "Schematic: " + inst.schematic.GetInfo());
            else player.sendMessage(ChatColor.YELLOW + "No schematic loaded.");

            if (inst.Origin == null) player.sendMessage(ChatColor.GREEN + "Origin: " + ChatColor.WHITE + "My Location");
            else
                player.sendMessage(ChatColor.GREEN + "Origin: " + ChatColor.WHITE + " x:" + inst.Origin.getBlockX() + " y:" + inst.Origin.getBlockY() + " z:" + inst.Origin.getBlockZ());

            player.sendMessage(ChatColor.GREEN + "Status: " + ChatColor.WHITE + inst.State + " Timeout: " + inst.MoveTimeout);
            player.sendMessage(ChatColor.GREEN + "Require Mats: " + ChatColor.WHITE + inst.RequireMaterials + " Hold Items: " + inst.HoldItems);

            if (inst.State == BuilderTrait.BuilderState.building) {
                player.sendMessage(ChatColor.BLUE + "Location: " + ChatColor.WHITE + " x:" + inst.ContinueLoc.getBlockX() + " y:" + inst.ContinueLoc.getBlockY() + " z:" + inst.ContinueLoc.getBlockZ());
                player.sendMessage(ChatColor.BLUE + "Build Pattern XZ: " + ChatColor.WHITE + inst.BuildPatternXY + ChatColor.BLUE + " Build Y Layers: " + ChatColor.WHITE + inst.BuildYLayers);
                player.sendMessage(ChatColor.BLUE + "Ignore Air: " + ChatColor.WHITE + inst.IgnoreAir + ChatColor.BLUE + "  Ignore Liquid: " + ChatColor.WHITE + inst.IgnoreLiquid);
                player.sendMessage(ChatColor.BLUE + "Hold Items: " + ChatColor.WHITE + inst.HoldItems + ChatColor.BLUE + "  Excavte: " + ChatColor.WHITE + inst.Excavate);
                player.sendMessage(ChatColor.BLUE + "On Complete: " + ChatColor.WHITE + inst.oncomplete + ChatColor.BLUE + "  On Cancel: " + ChatColor.WHITE + inst.oncancel);
                long c = inst.startingcount;
                player.sendMessage(ChatColor.BLUE + "Blocks: Total: " + ChatColor.WHITE + c + ChatColor.BLUE + "  Remaining: " + ChatColor.WHITE + inst.Q.size());
                double percent = ((double) (c - inst.Q.size()) / (double) c) * 100;
                player.sendMessage(ChatColor.BLUE + "Complete: " + ChatColor.WHITE + String.format("%1$.1f", percent) + "%");
            }
            return true;
        }
        return false;
    }


    private boolean tryParseInt(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    public ItemStack getHead(String p) {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1);
        ItemMeta  m     = skull.getItemMeta();
        SkullMeta s     = (SkullMeta) m;
        assert s != null;
        s.setOwner(p);
        skull.setItemMeta(s);
        return skull;
    }

    private static ItemStack getItem(String b64stringtexture) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        ItemUT.addSkullTexture(head, b64stringtexture);
        ItemMeta headMeta = head.getItemMeta();
        if (headMeta != null) {
            headMeta.setDisplayName("Materials");
            ArrayList<String> lore = new ArrayList<>();
            lore.add("Next Page");
            headMeta.setLore(lore);
            head.setItemMeta(headMeta);
        }
        return head;
    }

    private static <T> Field getField(Class<?> target, String name, Class<T> fieldType, int index) {
        for (final Field field : target.getDeclaredFields()) {
            if ((name == null || field.getName().equals(name)) && fieldType.isAssignableFrom(field.getType()) && index-- <= 0) {
                field.setAccessible(true);
                return field;
            }
        }

        // Search in parent classes
        if (target.getSuperclass() != null)
            return getField(target.getSuperclass(), name, fieldType, index);
        throw new IllegalArgumentException("Cannot find field with type " + fieldType);
    }


}
