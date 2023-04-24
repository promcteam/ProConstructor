package fr.weefle.constructor.citizens;

import fr.weefle.constructor.Config;
import fr.weefle.constructor.NMS.NMS;
import fr.weefle.constructor.SchematicBuilder;
import fr.weefle.constructor.block.DataBuildBlock;
import fr.weefle.constructor.block.EmptyBuildBlock;
import fr.weefle.constructor.citizens.persistence.BuilderStatePersistenceLoader;
import fr.weefle.constructor.citizens.persistence.MaterialIntegerPersistenceLoader;
import fr.weefle.constructor.citizens.persistence.PatternXZPersistenceLoader;
import fr.weefle.constructor.citizens.persistence.SchematicPersistenceLoader;
import fr.weefle.constructor.essentials.BuilderSchematic;
import fr.weefle.constructor.essentials.BuilderTeleportStuckAction;
import fr.weefle.constructor.menu.menus.ParameterMenu;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.persistence.DelegatePersistence;
import net.citizensnpcs.api.persistence.Persist;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import net.citizensnpcs.trait.Toggleable;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.dynmap.DynmapCommonAPI;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.Map.Entry;

// TODO fix hanging blocks (like ladders or torches) dropping during excavation
@TraitName("builder")
public class BuilderTrait extends Trait implements Toggleable {
    @Persist
    boolean          toggled            = true;
    @Persist("IgnoreAir")
    boolean          ignoreAir          = false;
    @Persist("IgnoreLiquid")
    boolean          ignoreLiquid       = false;
    @Persist("Excavate")
    boolean          excavate           = false;
    @Persist("Silent")
    boolean          silent             = false;
    @Persist("State")
    @DelegatePersistence(BuilderStatePersistenceLoader.class)
    BuilderState     state              = BuilderState.IDLE;
    @Persist("PatternXY")
    @DelegatePersistence(PatternXZPersistenceLoader.class)
    BuildPatternXZ   buildPatternXZ     = BuildPatternXZ.SPIRAL;
    @Persist("HoldItems")
    boolean          holdItems          = SchematicBuilder.getInstance().config().isHoldItems();
    @Persist("RequireMaterials")
    boolean          requireMaterials   = SchematicBuilder.getInstance().config().isRequireMaterials();
    @Persist("Offset")
    boolean          offset             = false;
    @Persist("MoveTimeoutSeconds")
    double           moveTimeoutSeconds = 1.0;
    @Persist("YOffset")
    int              yOffset            = 0;
    @Persist("YLayers")
    int              buildYLayers       = 1;
    @Persist("Schematic")
    @DelegatePersistence(SchematicPersistenceLoader.class)
    BuilderSchematic schematic          = null;
    @Persist("Origin")
    Location         origin             = null;
    @Persist("ContinueLoc")
    Location         continueLoc        = null;
    @Persist
    @DelegatePersistence(MaterialIntegerPersistenceLoader.class)
    public Map<Material, Integer> NeededMaterials = new HashMap<>();

    @Persist("oncancel")
    String onCancel   = null;
    @Persist("oncomplete")
    String onComplete = null;
    @Persist("onstart")
    String onStart    = null;

    public Map<Material, Integer> ExcavateMaterials = new HashMap<>(); // Fixme only stores first layer I think?

    public boolean GroupByLayer = true;

    public long     startingcount = 1;
    public Location start         = null;

    public Queue<EmptyBuildBlock> Q = new LinkedList<>();

    public enum BuilderState {IDLE, BUILDING, MARKING, COLLECTING}

    public enum BuildPatternXZ {SPIRAL, REVERSE_SPIRAL, LINEAR, REVERSE_LINEAR}

    private boolean clearingMarks = false;

    private final Map<Player, Long> sessions = new HashMap<>();

    private BuilderSchematic _schematic = null;

    private Location mypos = null;

    private CommandSender sender = null;

    private EmptyBuildBlock next    = null;
    private Block           pending = null;

    private BukkitTask canceltaskid;

    private final Queue<EmptyBuildBlock> marks  = new LinkedList<>();
    private final Queue<EmptyBuildBlock> _marks = new LinkedList<>();

    public BuilderTrait() {super("builder");}

    public boolean isIgnoreAir() {return ignoreAir;}

    public void setIgnoreAir(boolean ignoreAir) {this.ignoreAir = ignoreAir;}

    public boolean isIgnoreLiquid() {return ignoreLiquid;}

    public void setIgnoreLiquid(boolean ignoreLiquid) {this.ignoreLiquid = ignoreLiquid;}

    public boolean isExcavate() {return excavate;}

    public void setExcavate(boolean excavate) {this.excavate = excavate;}

    public boolean isSilent() {return silent;}

    public void setSilent(boolean silent) {this.silent = silent;}

    @NotNull
    public BuilderState getState() {return state;}

    @NotNull
    public BuildPatternXZ getBuildPatternXZ() {return buildPatternXZ;}

    public void setBuildPatternXZ(@NotNull BuildPatternXZ buildPatternXZ) {this.buildPatternXZ = buildPatternXZ;}

    public boolean isHoldItems() {return holdItems;}

    public void setHoldItems(boolean holdItems) {this.holdItems = holdItems;}

    public boolean isRequireMaterials() {return requireMaterials;}

    public void setRequireMaterials(boolean requireMaterials) {this.requireMaterials = requireMaterials;}

    public double getMoveTimeoutSeconds() {return moveTimeoutSeconds;}

    public void setMoveTimeoutSeconds(double moveTimeoutSeconds) {this.moveTimeoutSeconds = moveTimeoutSeconds;}

    public void setYOffset(int yOffset) {this.yOffset = yOffset;}

    public int getBuildYLayers() {return buildYLayers;}

    public void setBuildYLayers(int buildYLayers) {this.buildYLayers = buildYLayers;}

    @Nullable
    public BuilderSchematic getSchematic() {return schematic;}

    public void setSchematic(BuilderSchematic schematic) {this.schematic = schematic;}

    @Nullable
    public Location getOrigin() {return origin;}

    public void setOrigin(@Nullable Location origin) {this.origin = origin;}

    @Nullable
    public Location getContinueLoc() {return continueLoc == null ? null : continueLoc.clone();}

    public void setContinueLoc(@Nullable Location continueLoc) {this.continueLoc = continueLoc;}

    @Nullable
    public String getOnCancel() {return onCancel;}

    public void setOnCancel(@Nullable String onCancel) {this.onCancel = onCancel;}

    @Nullable
    public String getOnComplete() {return onComplete;}

    public void setOnComplete(@Nullable String onComplete) {this.onComplete = onComplete;}

    @Nullable
    public String getOnStart() {return onStart;}

    public void setOnStart(@Nullable String onStart) {this.onStart = onStart;}

    @Override
    public void onSpawn() {
        npc.getNavigator().getDefaultParameters().avoidWater(false);

        if (state == BuilderState.BUILDING || state == BuilderState.COLLECTING) {
            new BukkitRunnable() {
                public void run() {
                    state = BuilderState.IDLE;
                    TryBuild(Bukkit.getServer().getConsoleSender());
                }
            }.runTaskLater(SchematicBuilder.getInstance(), 20);
        } else state = BuilderState.IDLE;
    }

    public void handleRightClick(NPCRightClickEvent event) {
        Player player = event.getClicker();
        if (this.state == BuilderState.IDLE || this.state == BuilderState.BUILDING) {
            player.performCommand("npc select " + npc.getId());
            new ParameterMenu(event.getClicker(), npc).open();
        } else if (this.state == BuilderState.COLLECTING) {
            ItemStack is = player.getItemInHand();

            if (is.getType().isBlock() && !(is.getType() == Material.AIR)) {


                String itemname = is.getType().name();


                if (!player.hasPermission("schematicbuilder.donate")) {
                    player.sendMessage(ChatColor.RED + "You do not have permission to donate");
                    return;
                }

                int    needed = this.NeededMaterials.getOrDefault(itemname, 0);
                Config config = SchematicBuilder.getInstance().config();
                if (needed > 0) {

                    //yup, i need it
                    int taking = Math.min(is.getAmount(), needed);

                    Long session = this.sessions.get(player);
                    if (session != null && System.currentTimeMillis() < session + 5 * 1000) {
                        //take it

                        //update player hand item
                        ItemStack newis;

                        if (is.getAmount() - taking > 0) newis = is.clone();
                        else newis = new ItemStack(Material.AIR);
                        newis.setAmount(is.getAmount() - taking);
                        event.getClicker().setItemInHand(newis);

                        //update needed

                        this.NeededMaterials.put(is.getType(), (needed - taking));
                        player.sendMessage(SchematicBuilder.format(config.getSupplyTakenMessage(), this.npc,
                                this.schematic,
                                player,
                                itemname,
                                taking + ""));
                        //check if can start
                        this.TryBuild(null);

                    } else {
                        player.sendMessage(SchematicBuilder.format(config.getSupplyNeedMessage(),
                                this.npc,
                                this.schematic,
                                player,
                                itemname,
                                needed + ""));
                        this.sessions.put(player, System.currentTimeMillis());
                    }

                } else {
                    player.sendMessage(SchematicBuilder.format(config.getSupplyDontNeedMessage(),
                            this.npc,
                            this.schematic,
                            player,
                            itemname,
                            "0"));
                    //don't need it or already have it.
                }
            }
        }
    }

    @Override
    public void onRemove() {this.state = BuilderState.IDLE;}

    @Override
    public boolean toggle() {
        toggled = !toggled;
        return toggled;
    }

    public boolean isToggled() {return toggled;}

    public String GetMatsList(boolean excavate) {
        if (!npc.isSpawned()) return "";
        if (schematic == null) return "";
        if (this.state != BuilderState.IDLE) return ChatColor.RED + "Cannot survey while building";

        Location start;

        if (origin != null) start = origin.clone();
        else if (continueLoc != null) start = continueLoc.clone();
        else start = npc.getEntity().getLocation().clone();

        if (schematic.offset != null && offset) {
            offset = false;
            start = start.add(schematic.offset);
        }

        try {
            NeededMaterials = NMS.getInstance().getUtil().MaterialsList(schematic.BuildQueue(start, true, true, excavate, BuildPatternXZ.LINEAR, false, 1, 0));
        } catch (Exception e) {
            Bukkit.getServer().getConsoleSender().sendMessage(e.getMessage());
        }

        return NMS.getInstance().getUtil().printList(NeededMaterials);
    }

    public boolean TryBuild(CommandSender sender) {
        if (sender == null) sender = this.sender;
        this.sender = sender;

        if (this.requireMaterials) { // TODO remove materials as they reach 0
            java.util.Iterator<Entry<Material, Integer>> it = NeededMaterials.entrySet().iterator();
            long                                         c  = 0;
            while (it.hasNext()) {
                c += it.next().getValue();
            }

            if (c > 0) {
                if (!silent)
                    sender.sendMessage(
                            SchematicBuilder.format(SchematicBuilder.getInstance().config().getCollectingMessage(),
                                    npc,
                                    schematic, sender,
                                    schematic.Name, c +
                                            ""));
                this.state = BuilderState.COLLECTING;
                return true;
            }
        }

        return StartBuild(sender);
    }

    private boolean StartBuild(CommandSender player) {
        if (!npc.isSpawned()) return false;
        if (schematic == null) return false;
        if (this.state == BuilderState.BUILDING) {
            return false;
        }


        if (origin != null) start = origin.clone();
        else if (continueLoc != null) start = continueLoc.clone();
        else start = npc.getEntity().getLocation().clone();

        if (schematic.offset != null && offset) {
            offset = false;
            start = start.add(schematic.offset);
            //Bukkit.getLogger().warning(schematic.offset.toString());
        }

        Q = schematic.BuildQueue(start, ignoreLiquid, ignoreAir, excavate, this.buildPatternXZ, this.GroupByLayer, this.buildYLayers, this.yOffset);
        if (!schematic.excludedMaterials.isEmpty()) {
            for (BlockData bdata : schematic.excludedMaterials) {
                if (bdata.getMaterial() == org.bukkit.Material.AIR || !bdata.getMaterial().isItem()) {continue;}
                ExcavateMaterials.put(bdata.getMaterial(), ExcavateMaterials.getOrDefault(bdata.getMaterial(), 0) + 1);
            }
        }

        startingcount = Q.size();
        continueLoc = start.clone();

        mypos = npc.getEntity().getLocation().clone();

        this.state = BuilderState.BUILDING;

        NeededMaterials.clear();

        if (!silent) sender.sendMessage(SchematicBuilder.format(
                SchematicBuilder.getInstance().config().getStartedMessage(),
                npc,
                schematic, player, null,
                "0"));

        if (onStart != null) {
            String resp = SchematicBuilder.runTask(onStart, npc);
            if (!silent) {
                if (resp == null) sender.sendMessage("Task " + onStart + " completed.");
                else sender.sendMessage("Task " + onStart + " could not be run: " + resp);
            }

        }

        SchematicBuilder.denizenAction(npc, "Build Start");
        SchematicBuilder.denizenAction(npc, "Build " + schematic.Name + " Start");

        SetupNextBlock();

        return true;
    }

    public boolean StartMark(Material mat) {
        if (!npc.isSpawned()) return false;
        if (schematic == null) return false;
        if (this.state != BuilderState.IDLE) return false;

        onComplete = null;
        onCancel = null;
        onStart = null;
        _schematic = schematic;

        mypos = npc.getEntity().getLocation().clone();

        schematic = new BuilderSchematic();
        schematic.Name = _schematic.Name;

        if (origin == null) {
            continueLoc = this.npc.getEntity().getLocation().clone();
        } else {
            continueLoc = origin.clone();
        }
        Q = schematic.CreateMarks(_schematic.width(), _schematic.height(), _schematic.length(), mat);
        this.state = BuilderState.MARKING;

        SetupNextBlock();

        return true;
    }

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

            pending = Objects.requireNonNull(continueLoc.getWorld()).getBlockAt(schematic.offset(next, continueLoc));


        } else {
            clearingMarks = true;
            next = marks.remove();
            pending = Objects.requireNonNull(continueLoc.getWorld()).getBlockAt(next.X, next.Y, next.Z);

        }

        assert bdata != null;
        if (bdata.equals(pending.getLocation().getBlock().getBlockData())) {
            SetupNextBlock();
        } else {
            if (npc.isSpawned()) {
                if ((npc.getEntity() instanceof org.bukkit.entity.HumanEntity || npc.getEntity() instanceof org.bukkit.entity.Enderman) && this.holdItems) {
                    if ((npc.getEntity() instanceof org.bukkit.entity.HumanEntity) && this.holdItems) {
                        ((org.bukkit.entity.HumanEntity) npc.getEntity()).getInventory().setItemInHand(new ItemStack(next.getMat().getMaterial()));
                    } else if ((npc.getEntity() instanceof org.bukkit.entity.Enderman) && this.holdItems) {
                        ((org.bukkit.entity.Enderman) npc.getEntity()).setCarriedMaterial(new MaterialData(next.getMat().getMaterial()));
                    }
                }
            }


            new BukkitRunnable() {
                @Override
                public void run() {
                    if (npc.isSpawned()) {
                        //((Player)npc.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 100, 1));
                        //npc.getEntity().playEffect(EntityEffect.VILLAGER_HAPPY);
                        Location loc = findaspot(pending).add(0.5, 0.5, 0.5);
                        //npc.faceLocation(loc);
                        npc.getNavigator().setTarget(loc);
                        npc.getNavigator().getLocalParameters().stationaryTicks((int) (moveTimeoutSeconds * 20));
                        npc.getNavigator().getLocalParameters().stuckAction(BuilderTeleportStuckAction.INSTANCE);
                        //npc.getNavigator().getLocalParameters().useNewPathfinder();
                        //npc.getNavigator().setPaused(false);
                        //npc.getNavigator().getPathStrategy().clearCancelReason();
                        npc.getNavigator().getPathStrategy().update();
                    }
                }
            }.runTask(SchematicBuilder.getInstance());

            canceltaskid = new BukkitRunnable() {
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
            }.runTaskLater(SchematicBuilder.getInstance(), (long) (moveTimeoutSeconds * 20) + 1);
        }
    }

    public void CancelBuild() {
        if (onCancel != null) SchematicBuilder.runTask(onCancel, npc);
        SchematicBuilder.denizenAction(npc, "Build Cancel");
        if (schematic != null) SchematicBuilder.denizenAction(npc, "Build " + schematic.Name + " Cancel");
        stop();
    }

    public void CompleteBuild() {
        if (sender == null) sender = Bukkit.getServer().getConsoleSender();

        if (this.state == BuilderState.BUILDING) {
            if (!silent) sender.sendMessage(
                    SchematicBuilder.format(SchematicBuilder.getInstance().config().getCompleteMessage(),
                            npc,
                            schematic, sender,
                            null, "0"));

            if (onComplete != null) {
                String resp = SchematicBuilder.runTask(onComplete, npc);
                if (!silent) {
                    if (resp == null) sender.sendMessage("Task " + onComplete + " completed.");
                    else sender.sendMessage("Task " + onComplete + " could not be run: " + resp);
                }
            }

            SchematicBuilder.denizenAction(npc, "Build Complete");
            SchematicBuilder.denizenAction(npc, "Build " + schematic.Name + " Complete");
        }
        stop();
    }

    private void stop() {
        boolean stop = state == BuilderState.BUILDING;
        if (canceltaskid != null && !canceltaskid.isCancelled()) canceltaskid.cancel();

        if (this.state == BuilderState.MARKING) {
            this.state = BuilderState.IDLE;
            if (origin != null) npc.getNavigator().setTarget(origin);
            else npc.getEntity().teleport(mypos);
            marks.addAll(_marks);
            _marks.clear();
            if (_schematic != null) schematic = _schematic;
            _schematic = null;
        } else {
            this.state = BuilderState.IDLE;
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

        if ((npc.getEntity() instanceof org.bukkit.entity.HumanEntity) && this.holdItems)
            ((org.bukkit.entity.HumanEntity) npc.getEntity()).getInventory().setItemInHand(new ItemStack(Material.AIR));
        else if ((npc.getEntity() instanceof org.bukkit.entity.Enderman) && this.holdItems)
            ((org.bukkit.entity.Enderman) npc.getEntity()).setCarriedMaterial(new MaterialData(Material.AIR));

        Plugin dynmap;
        if (stop && (dynmap = Bukkit.getServer().getPluginManager().getPlugin("dynmap")) != null && dynmap.isEnabled()) {
            org.dynmap.DynmapCommonAPI dyn = (DynmapCommonAPI) dynmap;
            Objects.requireNonNull(dyn).triggerRenderOfVolume(npc.getEntity().getWorld().getName(), this.continueLoc.getBlockX() - schematic.width() / 2, this.continueLoc.getBlockY(), this.continueLoc.getBlockZ() - schematic.length() / 2, this.continueLoc.getBlockX() + schematic.width() / 2, this.continueLoc.getBlockY() + schematic.height() / 2, this.continueLoc.getBlockZ() + schematic.length() / 2);
        }

        sender = null;
        onComplete = null;
        onCancel = null;
        onStart = null;
        continueLoc = null;
    }

    public void PlaceNextBlock() throws IllegalArgumentException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, ClassNotFoundException, InstantiationException {

        if (canceltaskid != null && !canceltaskid.isCancelled()) {
            canceltaskid.cancel();
        }

        BlockData bdata = next.getMat();


        if (state == BuilderState.MARKING && !clearingMarks) {
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





