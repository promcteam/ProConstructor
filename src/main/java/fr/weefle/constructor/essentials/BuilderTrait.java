package fr.weefle.constructor.essentials;

import fr.weefle.constructor.NMS.NMS;
import fr.weefle.constructor.SchematicBuilder;
import fr.weefle.constructor.block.DataBuildBlock;
import fr.weefle.constructor.block.EmptyBuildBlock;
import fr.weefle.constructor.util.Structure;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.util.DataKey;
import net.citizensnpcs.trait.Toggleable;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.scheduler.BukkitTask;
import org.dynmap.DynmapCommonAPI;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Level;

// TODO fix hanging blocks (like ladders or torches) dropping during excavation
public class BuilderTrait extends Trait implements Toggleable {

    private SchematicBuilder plugin;

    private boolean isToggled = true;

    public BuilderTrait() {
        super("builder");
    }

    @Override
    public void load(DataKey key) { // Fixme make consistent

        plugin = (SchematicBuilder) Bukkit.getServer().getPluginManager().getPlugin("ProSchematicBuilder");

        if (key.keyExists("Origin")) {
            try {
                Origin = new Location(plugin.getServer().getWorld(key.getString("Origin.world")), key.getDouble("Origin.x"), key.getDouble("Origin.y"), key.getDouble("Origin.z"), (float) key.getDouble("Origin.yaw"), (float) key.getDouble("Origin.pitch"));
            } catch (Exception e) {
                Origin = null;
            }
            if (Objects.requireNonNull(Origin).getWorld() == null) Origin = null;
        }

        if (key.keyExists("ContinueLoc")) {
            try {
                ContinueLoc = new Location(plugin.getServer().getWorld(key.getString("ContinueLoc.world")), key.getDouble("ContinueLoc.x"), key.getDouble("ContinueLoc.y"), key.getDouble("ContinueLoc.z"), (float) key.getDouble("ContinueLoc.yaw"), (float) key.getDouble("ContinueLoc.pitch"));
            } catch (Exception e) {
                ContinueLoc = null;
            }
            if (Objects.requireNonNull(ContinueLoc).getWorld() == null) ContinueLoc = null;
        }

        IgnoreAir = key.getBoolean("IgnoreAir", false);
        Silent = key.getBoolean("Silent", false);
        IgnoreLiquid = key.getBoolean("IgnoreLiquid", false);
        Excavate = key.getBoolean("Excavate", false);
        SchematicName = key.getString("Schematic", null);
        State = BuilderState.valueOf(key.getString("State", "idle"));
        oncancel = key.getString("oncancel", null);
        oncomplete = key.getString("oncomplete", null);
        onStart = key.getString("onstart", null);
        HoldItems = key.getBoolean("HoldItems", plugin.getConfig().getBoolean("DefaultOptions.Holditems", true));
        RequireMaterials = key.getBoolean("RequireMaterials", plugin.getConfig().getBoolean("DefaultOptions.RequireMaterials", false));

        try {
            if (key.keyExists("NeededMaterials")) {
                org.bukkit.configuration.MemorySection derp = (org.bukkit.configuration.MemorySection) key.getRaw("NeededMaterials");
                Set<String>                            keys = derp.getKeys(false);
                for (String k : keys) {
                    //TODO verify if this is working
                    NeededMaterials.put(Material.valueOf(k), derp.getInt(k));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Yoffset = key.getInt("YOffset");
        offset = key.getBoolean("Offset");
        BuildYLayers = key.getInt("YLayers");

        MoveTimeout = key.getDouble("MoveTimeoutSeconds", plugin.getConfig().getDouble("DefaultOptions.MoveTimeoutSeconds", 1.0));
        if (MoveTimeout < .1) MoveTimeout = .1;

        try {
            BuildPatternXY = BuildPatternsXZ.valueOf(key.getString("PatternXY", "spiral"));
        } catch (Exception e) {
            e.printStackTrace();
        }


        if (SchematicName != null) {
            File dir = new File(SchematicBuilder.schematicsFolder);
            try {
                if (SchematicName.endsWith(".schem")) {
                    schematic = NMS.getInstance().getChooser().setSchematic(dir, SchematicName);
                } else {
                    schematic = new Structure(dir, SchematicName).load(dir, SchematicName);
                }
            } catch (Exception e) {
                sender.sendMessage(ChatColor.RED + "Failed to load schematic "+SchematicName+", check console for more details.");
                SchematicBuilder.getInstance().getLogger().log(Level.WARNING, "Failed to load schematic: " + SchematicName);
                e.printStackTrace();
                return;
            }
        }

        loaded = true;

    }

    private boolean loaded = false;

    @Override
    public void onSpawn() {

        plugin = (SchematicBuilder) Bukkit.getPluginManager().getPlugin("ProSchematicBuilder");


        if (!this.loaded) {
            load(new net.citizensnpcs.api.util.MemoryDataKey());
        }

        npc.getNavigator().getDefaultParameters().avoidWater(false);

        if (State == BuilderState.building || State == BuilderState.collecting) {
            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                public void run() {
                    State = BuilderState.idle;
                    TryBuild(plugin.getServer().getConsoleSender());
                }
            }, 20);
        } else State = BuilderState.idle;
    }

    @Override
    public void onRemove() {
        this.State = BuilderState.idle;
    }


    @Override
    public void save(DataKey key) {
        key.setBoolean("toggled", true);
        key.setBoolean("IgnoreAir", IgnoreAir);
        key.setBoolean("IgnoreLiquid", IgnoreLiquid);
        key.setBoolean("Excavate", Excavate);
        key.setBoolean("Silent", Silent);
        key.setString("State", State.toString());
        key.setString("PatternXY", BuildPatternXY.toString());
        key.setBoolean("HoldItems", HoldItems);
        key.setBoolean("RequireMaterials", RequireMaterials);
        key.setBoolean("Offset", offset);
        key.setDouble("MoveTimeoutSeconds", MoveTimeout);
        key.setInt("YOffset", Yoffset);
        key.setInt("YLayers", BuildYLayers);

        HashMap<String, Integer> items = new HashMap<>();
        for (Map.Entry<Material,Integer> entry : NeededMaterials.entrySet()) {
            items.put(entry.getKey().name(), entry.getValue());
        }

        if (items.size() > 0) key.setRaw("NeededMaterials", items);
        else if (key.keyExists("NeededMaterials")) key.removeKey("NeededMaterials");

        if (oncancel != null) key.setString("oncancel", oncancel);
        else if (key.keyExists("oncancel")) key.removeKey("oncancel");

        if (onStart != null) key.setString("onstart", onStart);
        else if (key.keyExists("onstart")) key.removeKey("onstart");

        if (oncomplete != null) key.setString("oncomplete", oncomplete);
        else if (key.keyExists("oncomplete")) key.removeKey("oncomplete");

        if (Origin != null) {
            key.setDouble("Origin.x", Origin.getX());
            key.setDouble("Origin.y", Origin.getY());
            key.setDouble("Origin.z", Origin.getZ());
            key.setString("Origin.world", Objects.requireNonNull(Origin.getWorld()).getName());
            key.setDouble("Origin.yaw", Origin.getYaw());
            key.setDouble("Origin.pitch", Origin.getPitch());
        } else if (key.keyExists("Origin")) key.removeKey("Origin");

        if (ContinueLoc != null) {
            key.setDouble("ContinueLoc.x", ContinueLoc.getX());
            key.setDouble("ContinueLoc.y", ContinueLoc.getY());
            key.setDouble("ContinueLoc.z", ContinueLoc.getZ());
            key.setString("ContinueLoc.world", Objects.requireNonNull(ContinueLoc.getWorld()).getName());
            key.setDouble("ContinueLoc.yaw", ContinueLoc.getYaw());
            key.setDouble("ContinueLoc.pitch", ContinueLoc.getPitch());
        } else if (key.keyExists("ContinueLoc")) key.removeKey("ContinueLoc");

        if (SchematicName != null) key.setString("Schematic", SchematicName);
        else if (key.keyExists("Schematic")) key.removeKey("Schematic");

    }

    @Override
    public boolean toggle() {
        isToggled = !isToggled;
        return isToggled;
    }

    public boolean isToggled() {
        return isToggled;
    }


    public BuilderState     State         = BuilderState.idle;
    public BuilderSchematic schematic     = null;
    public String           SchematicName = null;
    public Boolean          IgnoreAir, IgnoreLiquid, Excavate;
    public Map<Material,Integer> ExcavateMaterials = new HashMap<>(); // Fixme only stores first layer I think?
    public Boolean          RequireMaterials  = false;
    public Location         Origin            = null;
    public Location         ContinueLoc       = null;
    public String           onStart           = null;
    public String           oncomplete        = null;
    public String           oncancel          = null;
    public Boolean          HoldItems         = true;
    public Boolean          GroupByLayer      = true;
    public Integer          BuildYLayers      = 1;
    public Integer          Yoffset           = 0;
    public BuildPatternsXZ  BuildPatternXY    = BuildPatternsXZ.spiral;
    public Boolean          offset            = false;
    public double           MoveTimeout       = 1.0;
    public Boolean          Silent            = false;

    public Map<Material, Integer> NeededMaterials = new HashMap<>();

    public Queue<EmptyBuildBlock> Q = new LinkedList<EmptyBuildBlock>();

    public enum BuilderState {idle, building, marking, collecting}

    public enum BuildPatternsXZ {spiral, reversespiral, linear, reverselinear}

    private boolean clearingMarks = false;

    Map<Player, Long> Sessions = new HashMap<Player, Long>();

    public String GetMatsList(boolean excavate) {
        if (!npc.isSpawned()) return "";
        if (schematic == null) return "";
        if (this.State != BuilderState.idle) return ChatColor.RED + "Cannot survey while building";

        Location start = null;

        if (Origin != null) start = Origin.clone();
        else if (ContinueLoc != null) start = ContinueLoc.clone();
        else start = npc.getEntity().getLocation().clone();

        if (schematic.offset != null && offset) {
            offset = false;
            start = start.add(schematic.offset);

        }

        try {

            NeededMaterials = NMS.getInstance().getUtil().MaterialsList(schematic.BuildQueue(start, true, true, excavate, BuildPatternsXZ.linear, false, 1, 0));

        } catch (Exception e) {
            plugin.getServer().getConsoleSender().sendMessage(e.getMessage());
        }

        return NMS.getInstance().getUtil().printList(NeededMaterials);

    }

    public boolean TryBuild(CommandSender sender) {

        if (sender == null) sender = this.sender;

        this.sender = sender;

        if (this.RequireMaterials) { // TODO remove materials as they reach 0
            java.util.Iterator<Entry<Material, Integer>> it = NeededMaterials.entrySet().iterator();
            long                                       c  = 0;
            while (it.hasNext()) {
                c += it.next().getValue();
            }

            if (c > 0) {
                if (!Silent)
                    sender.sendMessage(plugin.format(plugin.CollectingMessage, npc, schematic, sender, SchematicName, c + ""));
                this.State = BuilderState.collecting;
                return true;
            }


        }

        return StartBuild(sender);
    }

    public long     startingcount = 1;
    public Location start         = null;

    private boolean StartBuild(CommandSender player) {
        if (!npc.isSpawned()) return false;
        if (schematic == null) return false;
        if (this.State == BuilderState.building) {
            return false;
        }


        if (Origin != null) start = Origin.clone();
        else if (ContinueLoc != null) start = ContinueLoc.clone();
        else start = npc.getEntity().getLocation().clone();

        if (schematic.offset != null && offset) {
            offset = false;
            start = start.add(schematic.offset);
            //Bukkit.getLogger().warning(schematic.offset.toString());
        }

        Q = schematic.BuildQueue(start, IgnoreLiquid, IgnoreAir, Excavate, this.BuildPatternXY, this.GroupByLayer, this.BuildYLayers, this.Yoffset);
        if (!schematic.excludedMaterials.isEmpty()) {
            for (BlockData bdata : schematic.excludedMaterials) {
                if (bdata.getMaterial() == org.bukkit.Material.AIR || !bdata.getMaterial().isItem()) {continue;}
                ExcavateMaterials.put(bdata.getMaterial(), ExcavateMaterials.getOrDefault(bdata.getMaterial(), 0)+1);
            }
        }


        startingcount = Q.size();
        ContinueLoc = start.clone();

        mypos = npc.getEntity().getLocation().clone();

        this.State = BuilderState.building;

        NeededMaterials.clear();

        if (!Silent) sender.sendMessage(plugin.format(plugin.StartedMessage, npc, schematic, player, null, "0"));

        if (onStart != null) {
            String resp = plugin.runTask(onStart, npc);
            if (!Silent) {
                if (resp == null) sender.sendMessage("Task " + onStart + " completed.");
                else sender.sendMessage("Task " + onStart + " could not be run: " + resp);
            }

        }

        plugin.DenizenAction(npc, "Build Start");
        plugin.DenizenAction(npc, "Build " + schematic.Name + " Start");

        SetupNextBlock();

        return true;
    }

    private BuilderSchematic _schematic = null;

    private Location mypos = null;

    private final Queue<EmptyBuildBlock> marks  = new LinkedList<EmptyBuildBlock>();
    private final Queue<EmptyBuildBlock> _marks = new LinkedList<EmptyBuildBlock>();

    public boolean StartMark(Material mat) {
        if (!npc.isSpawned()) return false;
        if (schematic == null) return false;
        if (this.State != BuilderState.idle) return false;

        oncomplete = null;
        oncancel = null;
        onStart = null;
        _schematic = schematic;

        mypos = npc.getEntity().getLocation().clone();

        schematic = new BuilderSchematic();
        schematic.Name = _schematic.Name;

        if (Origin == null) {
            ContinueLoc = this.npc.getEntity().getLocation().clone();
        } else {
            ContinueLoc = Origin.clone();
        }
        Q = schematic.CreateMarks(_schematic.width(), _schematic.height(), _schematic.length(), mat);
        this.State = BuilderState.marking;

        SetupNextBlock();

        return true;
    }

    private CommandSender sender = null;

    private EmptyBuildBlock next    = null;
    private Block           pending = null;

    public void SetupNextBlock() {

        BlockData bdata = null;

        if (marks.isEmpty()) {
            if (schematic == null) {
                CancelBuild();
                return;
            }


            next = Q.poll();

            if (next == null) {
                CompleteBuild();
                return;
            }

            bdata = next.getMat();

            pending = Objects.requireNonNull(ContinueLoc.getWorld()).getBlockAt(schematic.offset(next, ContinueLoc));


        } else {
            clearingMarks = true;
            next = marks.remove();
            pending = Objects.requireNonNull(ContinueLoc.getWorld()).getBlockAt(next.X, next.Y, next.Z);

        }

        assert bdata != null;
        if (bdata.equals(pending.getLocation().getBlock().getBlockData())) {
            SetupNextBlock();
        } else {

            if (npc.isSpawned()) {

                if ((npc.getEntity() instanceof org.bukkit.entity.HumanEntity || npc.getEntity() instanceof org.bukkit.entity.Enderman) && this.HoldItems) {


                    if ((npc.getEntity() instanceof org.bukkit.entity.HumanEntity) && this.HoldItems)
                        ((org.bukkit.entity.HumanEntity) npc.getEntity()).getInventory().setItemInHand(new ItemStack(next.getMat().getMaterial()));
                    else if ((npc.getEntity() instanceof org.bukkit.entity.Enderman) && this.HoldItems)
                        ((org.bukkit.entity.Enderman) npc.getEntity()).setCarriedMaterial(new MaterialData(next.getMat().getMaterial()));
                }
            }


            plugin.getServer().getScheduler().runTask(plugin, new Runnable() {
                public void run() {
                    if (npc.isSpawned()) {
                        //((Player)npc.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 100, 1));
                        //npc.getEntity().playEffect(EntityEffect.VILLAGER_HAPPY);
                        Location loc = findaspot(pending).add(0.5, 0.5, 0.5);
                        //npc.faceLocation(loc);
                        npc.getNavigator().setTarget(loc);
                        npc.getNavigator().getLocalParameters().stationaryTicks((int) (MoveTimeout * 20));
                        npc.getNavigator().getLocalParameters().stuckAction(BuilderTeleportStuckAction.INSTANCE);
                        //npc.getNavigator().getLocalParameters().useNewPathfinder();
                        //npc.getNavigator().setPaused(false);
                        //npc.getNavigator().getPathStrategy().clearCancelReason();
                        npc.getNavigator().getPathStrategy().update();
                    }
                }
            });

            canceltaskid = plugin.getServer().getScheduler().runTaskLater(plugin, new Runnable() {
                public void run() {
                    if (npc.isSpawned()) {
                        if (npc.getNavigator().isNavigating()) {
                            npc.getEntity().teleport(npc.getNavigator().getTargetAsLocation());
                            //Location loc = findaspot(pending).add(0.5, 0.5, 0.5);
                            //npc.faceLocation(loc);
                            //npc.getNavigator().setTarget(loc);
                            //npc.getNavigator().getLocalParameters().useNewPathfinder();
                            //npc.getNavigator().setPaused(false);
                            //npc.getNavigator().getPathStrategy().clearCancelReason();
                            npc.getNavigator().getPathStrategy().update();
                            npc.getNavigator().cancelNavigation();
                        }
                    }
                }
            }, (long) (MoveTimeout * 20) + 1);

        }

    }

    public void CancelBuild() {
        if (oncancel != null) plugin.runTask(oncancel, npc);
        plugin.DenizenAction(npc, "Build Cancel");
        if (schematic != null) plugin.DenizenAction(npc, "Build " + schematic.Name + " Cancel");

        stop();
    }

    public void CompleteBuild() {
        if (sender == null) sender = plugin.getServer().getConsoleSender();

        if (this.State == BuilderState.building) {
            if (!Silent) sender.sendMessage(plugin.format(plugin.CompleteMessage, npc, schematic, sender, null, "0"));


            if (oncomplete != null) {
                String resp = plugin.runTask(oncomplete, npc);
                if (!Silent) {
                    if (resp == null) sender.sendMessage("Task " + oncomplete + " completed.");
                    else sender.sendMessage("Task " + oncomplete + " could not be run: " + resp);
                }
            }

            plugin.DenizenAction(npc, "Build Complete");
            plugin.DenizenAction(npc, "Build " + schematic.Name + " Complete");
        }

        stop();

    }

    private void stop() {
        boolean stop = State == BuilderState.building;
        if (canceltaskid != null && !canceltaskid.isCancelled()) canceltaskid.cancel();

        if (this.State == BuilderState.marking) {
            this.State = BuilderState.idle;
            if (Origin != null) npc.getNavigator().setTarget(Origin);
            else npc.getEntity().teleport(mypos);
            marks.addAll(_marks);
            _marks.clear();
            if (_schematic != null) schematic = _schematic;
            _schematic = null;
        } else {
            this.State = BuilderState.idle;
            if (stop && npc.isSpawned()) {
                if (npc.getNavigator().isNavigating()) npc.getNavigator().cancelNavigation();
                npc.getNavigator().setTarget(mypos);

				/*if(sender instanceof Player) {
					Player player = (Player) sender;
					HashMap<UUID, String> players = new HashMap<>();
					players.put(player.getUniqueId(), player.getName());
					Region region = new Region(player.getName()+"_"+UUID.randomUUID(), players, schematic.getSchematicOrigin(this), new int[]{},null, 0.0);
					RegionManager.getInstance().addRegion(region);
				}*/

            }
        }


        if ((npc.getEntity() instanceof org.bukkit.entity.HumanEntity) && this.HoldItems)
            ((org.bukkit.entity.HumanEntity) npc.getEntity()).getInventory().setItemInHand(new ItemStack(Material.AIR));
        else if ((npc.getEntity() instanceof org.bukkit.entity.Enderman) && this.HoldItems)
            ((org.bukkit.entity.Enderman) npc.getEntity()).setCarriedMaterial(new MaterialData(Material.AIR));

        if (stop && plugin.getServer().getPluginManager().getPlugin("dynmap") != null) {
            if (Objects.requireNonNull(plugin.getServer().getPluginManager().getPlugin("dynmap")).isEnabled()) {
                org.dynmap.DynmapCommonAPI dyn = (DynmapCommonAPI) (plugin.getServer().getPluginManager().getPlugin("dynmap"));
                Objects.requireNonNull(dyn).triggerRenderOfVolume(npc.getEntity().getWorld().getName(), this.ContinueLoc.getBlockX() - schematic.width() / 2, this.ContinueLoc.getBlockY(), this.ContinueLoc.getBlockZ() - schematic.length() / 2, this.ContinueLoc.getBlockX() + schematic.width() / 2, this.ContinueLoc.getBlockY() + schematic.height() / 2, this.ContinueLoc.getBlockZ() + schematic.length() / 2);
            }
        }

        sender = null;
        oncomplete = null;
        oncancel = null;
        onStart = null;
        ContinueLoc = null;

    }

    private BukkitTask canceltaskid;

    public void PlaceNextBlock() throws IllegalArgumentException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, ClassNotFoundException, InstantiationException {

        if (canceltaskid != null && !canceltaskid.isCancelled()) {
            canceltaskid.cancel();
        }

        BlockData bdata = next.getMat();


        if (State == BuilderState.marking && !clearingMarks) {
            _marks.add(new DataBuildBlock(pending.getX(), pending.getY(), pending.getZ(), pending.getBlockData()));
        }

        pending.setBlockData(bdata);
        pending.getWorld().playEffect(pending.getLocation(), Effect.STEP_SOUND, pending.getType());
        NMS.getInstance().getChecker().check(next, pending);

        if (this.npc.getEntity() instanceof Player) {
            //arm swing
            net.citizensnpcs.util.PlayerAnimation.ARM_SWING.play((Player) this.npc.getEntity(), 64);
        }

        if (marks.size() == 0) clearingMarks = false;


        SetupNextBlock();


    }

    //Given a BuildBlock to place, find a good place to stand to place it.
    private Location findaspot(Block base) {
        if (base == null) return null;

        for (int a = 3; a >= -5; a--) {
            if (NMS.getInstance().getUtil().canStand(base.getRelative(0, a, -1)))
                return base.getRelative(0, a - 1, -1).getLocation();
            if (NMS.getInstance().getUtil().canStand(base.getRelative(0, a, 1)))
                return base.getRelative(0, a - 1, 1).getLocation();
            if (NMS.getInstance().getUtil().canStand(base.getRelative(1, a, 0)))
                return base.getRelative(1, a - 1, 0).getLocation();
            if (NMS.getInstance().getUtil().canStand(base.getRelative(-1, a, 0)))
                return base.getRelative(-1, a - 1, 0).getLocation();
            if (NMS.getInstance().getUtil().canStand(base.getRelative(-1, a, -1)))
                return base.getRelative(-1, a - 1, -1).getLocation();
            if (NMS.getInstance().getUtil().canStand(base.getRelative(-1, a, 1)))
                return base.getRelative(-1, a - 1, 1).getLocation();
            if (NMS.getInstance().getUtil().canStand(base.getRelative(1, a, 1)))
                return base.getRelative(1, a - 1, 1).getLocation();
            if (NMS.getInstance().getUtil().canStand(base.getRelative(1, a, -1)))
                return base.getRelative(1, a - 1, -1).getLocation();
        }


        return base.getLocation();

    }


}





