package studio.magemonkey.blueprint.menus;

import com.google.common.base.Preconditions;
import net.citizensnpcs.api.npc.NPC;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;
import studio.magemonkey.blueprint.Blueprint;
import studio.magemonkey.blueprint.PersistentBuilding;
import studio.magemonkey.blueprint.commands.ExcavatedSubCommand;
import studio.magemonkey.blueprint.commands.PreviewSubCommand;
import studio.magemonkey.blueprint.hooks.citizens.BuilderTrait;
import studio.magemonkey.blueprint.schematic.Schematic;
import studio.magemonkey.blueprint.schematic.YAMLSchematic;
import studio.magemonkey.blueprint.util.Util;
import studio.magemonkey.codex.manager.api.menu.Menu;
import studio.magemonkey.codex.manager.api.menu.Slot;
import studio.magemonkey.codex.manager.api.menu.YAMLMenu;
import studio.magemonkey.codex.util.ItemUT;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BuilderMenu extends Menu {
    public static final YAMLBuilderMenu CONFIG =
            new YAMLBuilderMenu(Blueprint.getInstance(), "menus/builder.yml");

    public static class YAMLBuilderMenu extends YAMLMenu<BuilderTrait> {
        public YAMLBuilderMenu(Plugin plugin, String path) {super(plugin, path);}

        @Override
        public String getTitle(String yamlTitle, BuilderTrait parameter) {
            return yamlTitle.replace("%npc%", parameter.getNPC().getName());
        }

        @Override
        @Nullable
        public Slot getSlot(String function, BuilderTrait builder, Player player) {
            switch (function) {
                case "schematic": {
                    Schematic schematic = builder.getSchematic();
                    if (schematic == null) {
                        return new Slot(this.getItem(function + "-null"));
                    }
                    int tier =
                            schematic instanceof YAMLSchematic ? ((YAMLSchematic) schematic).getTier() + 1 : 1;
                    ItemStack itemStack = this.getItem(function + "-notnull");
                    itemStack.setAmount(Math.max(1, tier));
                    ItemMeta meta = itemStack.getItemMeta();
                    if (meta != null) {
                        meta.setDisplayName(meta.getDisplayName()
                                .replace("%name%", schematic.getDisplayName())
                                .replace("%tier%", String.valueOf(tier)));
                    }
                    itemStack.setItemMeta(meta);
                    return new Slot(itemStack) {
                        @Override
                        public void onLeftClick() {this.menu.openSubMenu(new TiersMenu(player, schematic));}

                        @Override
                        public void onDrop() {
                            builder.setSchematic(null);
                            this.menu.open();
                        }
                    };
                }
                case "trigger": {
                    if (builder.getSchematic() == null) {
                        return new Slot(this.getItem(function + "-empty")) {
                        };
                    }
                    if (!builder.getMissingMaterials().isEmpty()) {
                        return new Slot(this.getItem(function + "-missing-materials")) {
                        };
                    }
                    if (builder.getState() == BuilderTrait.BuilderState.BUILDING) {
                        return new Slot(this.getItem(function + "-stop")) {
                            @Override
                            public void onLeftClick() {
                                builder.CancelBuild();
                                player.sendMessage(Blueprint.format(
                                        Blueprint.getInstance().config().getCancelMessage(),
                                        builder.getNPC(),
                                        builder.getSchematic(),
                                        player,
                                        null, "0"));
                                this.menu.open();
                            }
                        };
                    }
                    return new Slot(this.getItem(function + "-start")) {
                        @Override
                        public void onLeftClick() {
                            if (builder.TryBuild(player)) {
                                menu.close();
                            } else {
                                player.sendMessage(ChatColor.RED + builder.getNPC().getName()
                                        + " needs a structure to build first!");
                            }
                        }
                    };
                }
                case "origin": {
                    Location  origin    = builder.getOrigin();
                    ItemStack itemStack = this.getItem(function);
                    ItemUT.replaceLore(itemStack, "%world%", Objects.requireNonNull(origin.getWorld()).getName());
                    ItemUT.replaceLore(itemStack, "%x%", String.valueOf(origin.getBlockX()));
                    ItemUT.replaceLore(itemStack, "%y%", String.valueOf(origin.getBlockY()));
                    ItemUT.replaceLore(itemStack, "%z%", String.valueOf(origin.getBlockZ()));
                    return new Slot(itemStack) {
                        @Override
                        public void onLeftClick() {
                            Schematic schematic = builder.getSchematic();
                            if (schematic == null) {
                                player.sendMessage(Blueprint.format(
                                        Blueprint.getInstance().config().getNoSchematicSelectedMessage(),
                                        builder.getNPC(),
                                        builder.getSchematic(),
                                        player,
                                        null, "0"));
                                return;
                            }
                            schematic.hidePreview();
                            if (schematic instanceof YAMLSchematic && ((YAMLSchematic) schematic).getTier() >= 0) {
                                player.sendMessage(Blueprint.format(
                                        Blueprint.getInstance().config().getCantMoveSchematicMessage(),
                                        builder.getNPC(),
                                        builder.getSchematic(),
                                        player,
                                        null, "0"));
                                return;
                            }
                            if (!checkNotBusy(builder, player)) {
                                return;
                            }

                            Menu menu = this.menu;
                            BaseComponent component = new TextComponent(
                                    "▸ While facing in each direction, left click to push the building, or right click to bring it closer.");
                            player.spigot().sendMessage(component);

                            component = new TextComponent("");
                            BaseComponent component1 = new TextComponent(
                                    ChatColor.BLUE.toString() + ChatColor.UNDERLINE + "Rotate counterclockwise");
                            component1.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,
                                    "counterclockwise"));
                            component.addExtra(component1);
                            component.addExtra(new TextComponent(" "));
                            component1 = new TextComponent(
                                    ChatColor.BLUE.toString() + ChatColor.UNDERLINE + "Rotate clockwise");
                            component1.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "clockwise"));
                            component.addExtra(component1);
                            player.spigot().sendMessage(component);

                            component = new TextComponent("");
                            component1 = new TextComponent(ChatColor.GOLD.toString() + ChatColor.UNDERLINE + "Stop");
                            component1.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "Stop"));
                            component.addExtra(component1);
                            component.addExtra(new TextComponent(" "));
                            component1 = new TextComponent(ChatColor.GOLD.toString() + ChatColor.UNDERLINE + "Here");
                            component1.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "Here"));
                            component.addExtra(component1);
                            component.addExtra(new TextComponent(" "));
                            component1 =
                                    new TextComponent(ChatColor.GOLD.toString() + ChatColor.UNDERLINE + "To builder");
                            component1.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "To builder"));
                            component.addExtra(component1);
                            player.spigot().sendMessage(component);
                            this.menu.fakeClose();

                            BukkitTask task = new BukkitRunnable() {
                                @Override
                                public void run() {
                                    ParticleCuboid cuboid = new ParticleCuboid(builder.getOrigin().toVector(),
                                            new Vector(schematic.getWidth(),
                                                    schematic.getHeight(),
                                                    schematic.getLength()));
                                    cuboid.rotate(builder.getRotation());
                                    cuboid.render(player);
                                }
                            }.runTaskTimer(Blueprint.getInstance(), 5, 5);
                            menu.registerTask(task);
                            menu.registerListener(new Listener() {
                                @EventHandler
                                public void onChat(AsyncPlayerChatEvent event) {
                                    if (!event.getPlayer().equals(player)) {
                                        return;
                                    }
                                    String message = event.getMessage().strip();
                                    if (message.equalsIgnoreCase("counterclockwise")) {
                                        event.setCancelled(true);
                                        builder.setRotation(builder.getRotation() - 1);
                                    } else if (message.equalsIgnoreCase("clockwise")) {
                                        event.setCancelled(true);
                                        builder.setRotation(builder.getRotation() + 1);
                                    } else if (message.equalsIgnoreCase("Stop")) {
                                        HandlerList.unregisterAll(this);
                                        event.setCancelled(true);
                                        menu.unregisterTask(task);
                                        task.cancel();
                                        menu.openSync();
                                    } else if (message.equalsIgnoreCase("Here")) {
                                        event.setCancelled(true);
                                        builder.setOrigin(player.getLocation());
                                    } else if (message.equalsIgnoreCase("To builder")) {
                                        event.setCancelled(true);
                                        builder.setOrigin(null);
                                    }
                                }

                                @EventHandler
                                public void onBlockClick(PlayerInteractEvent event) {
                                    switch (event.getAction()) {
                                        case LEFT_CLICK_BLOCK:
                                        case LEFT_CLICK_AIR:
                                        case RIGHT_CLICK_BLOCK:
                                        case RIGHT_CLICK_AIR: {
                                            float    yaw      = player.getLocation().getYaw();
                                            float    pitch    = player.getLocation().getPitch();
                                            Location location = builder.getOrigin();
                                            Vector   offset;
                                            if (pitch < -45F) { // Up
                                                offset = new Vector(0, 1, 0);
                                            } else if (pitch <= 45F) { // Front
                                                if (yaw < -135F) { // North
                                                    offset = new Vector(0, 0, -1);
                                                } else if (yaw < -45F) { // East
                                                    offset = new Vector(1, 0, 0);
                                                } else if (yaw < 45F) { // South
                                                    offset = new Vector(0, 0, 1);
                                                } else if (yaw < 135F) {
                                                    offset = new Vector(-1, 0, 0);
                                                } else { // Also north
                                                    offset = new Vector(0, 0, -1);
                                                }
                                            } else { // Down
                                                offset = new Vector(0, -1, 0);
                                            }
                                            float soundPitch = 0.5F;
                                            switch (event.getAction()) {
                                                case RIGHT_CLICK_BLOCK:
                                                case RIGHT_CLICK_AIR: {
                                                    offset.multiply(-1);
                                                    soundPitch = 1.5F;
                                                    break;
                                                }
                                            }
                                            player.playSound(player.getEyeLocation(),
                                                    Sound.ENTITY_PLAYER_ATTACK_WEAK,
                                                    SoundCategory.MASTER,
                                                    1,
                                                    soundPitch);
                                            location.add(offset);
                                            builder.setOrigin(location);
                                            break;
                                        }
                                    }
                                }
                            });
                        }
                    };
                }
                case "required-materials": {
                    int total = 0;
                    if (builder.getSchematic() != null) {
                        for (Integer amount : builder.getMissingMaterials().values()) {
                            total += amount;
                        }
                    }
                    ItemStack itemStack = this.getItem(function);
                    ItemUT.replaceLore(itemStack, "%current%", String.valueOf(total));
                    int finalTotal = total;
                    return new Slot(itemStack) {
                        public void onLeftClick() {
                            if (finalTotal > 0) {
                                this.menu.openSubMenu(new MaterialsMenu(player, builder));
                            }
                        }
                    };
                }
                case "settings": {
                    if (player.hasPermission("schematicbuilder.npc.edit")) {
                        return new Slot(this.getItem(function + "-admin")) {
                            @Override
                            public void onLeftClick() {
                                this.menu.openSubMenu(new SettingsMenu(player, builder.getNPC(), SettingsMenu.PLAYER));
                            }

                            @Override
                            public void onShiftLeftClick() {
                                this.menu.openSubMenu(new SettingsMenu(player, builder.getNPC(), SettingsMenu.ADMIN));
                            }
                        };
                    } else if (SettingsMenu.PLAYER.isEmpty()) {
                        return null;
                    } else {
                        return new Slot(this.getItem(function + "-player")) {
                            @Override
                            public void onLeftClick() {
                                this.menu.openSubMenu(new SettingsMenu(player, builder.getNPC(), SettingsMenu.PLAYER));
                            }
                        };
                    }
                }
                case "new-schematic": {
                    return new Slot(this.getItem(function)) {
                        @Override
                        public void onLeftClick() {
                            if (checkNotBusy(builder, player)) {
                                this.menu.openSubMenu(new SchematicListMenu(player));
                            }
                        }
                    };
                }
                case "existing-structure": {
                    return new Slot(this.getItem(function)) {
                        @Override
                        public void onLeftClick() {
                            if (checkNotBusy(builder, player)) {
                                Menu menu = this.menu;
                                BaseComponent component = new TextComponent(
                                        "▸ Click on a block from the structure you want to select, or type ");
                                BaseComponent component1 =
                                        new TextComponent(ChatColor.GOLD.toString() + ChatColor.UNDERLINE + "cancel");
                                component1.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "cancel"));
                                component.addExtra(component1);
                                component.addExtra(new TextComponent(" to go back."));
                                player.spigot().sendMessage(component);

                                this.menu.fakeClose();
                                menu.registerListener(new Listener() {
                                    @EventHandler
                                    public void onChat(AsyncPlayerChatEvent event) {
                                        if (!event.getPlayer().equals(player)) {
                                            return;
                                        }
                                        if (event.getMessage().strip().equalsIgnoreCase("cancel")) {
                                            HandlerList.unregisterAll(this);
                                            event.setCancelled(true);
                                            menu.openSync();
                                        }
                                    }

                                    @EventHandler
                                    public void onBlockClick(PlayerInteractEvent event) {
                                        switch (event.getAction()) {
                                            case LEFT_CLICK_BLOCK:
                                            case RIGHT_CLICK_BLOCK:
                                            case LEFT_CLICK_AIR:
                                            case RIGHT_CLICK_AIR: {
                                                event.setCancelled(true);
                                                Block block = event.getClickedBlock();
                                                if (block == null) {
                                                    return;
                                                }
                                                PersistentBuilding persistentBuilding = Blueprint.getInstance()
                                                        .getBuildingRegistry()
                                                        .getPersistentBuilding(block.getLocation());
                                                if (persistentBuilding == null) {
                                                    BaseComponent component = new TextComponent(ChatColor.RED
                                                            + "This block doesn't belong to a existing building. Type ");
                                                    BaseComponent component1 = new TextComponent(
                                                            ChatColor.GOLD.toString() + ChatColor.UNDERLINE + "cancel");
                                                    component1.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,
                                                            "cancel"));
                                                    component.addExtra(component1);
                                                    component.addExtra(new TextComponent(
                                                            ChatColor.RED + " to go back."));
                                                    player.spigot().sendMessage(component);
                                                } else {
                                                    HandlerList.unregisterAll(this);
                                                    builder.setPersistentBuilding(persistentBuilding);
                                                    player.sendMessage(ChatColor.GREEN + "Building selected");
                                                    menu.open();
                                                }
                                                break;
                                            }
                                        }
                                    }
                                });
                            }
                        }
                    };
                }
                case "preview": {
                    return new Slot(this.getItem(function)) {
                        @Override
                        public void onLeftClick() {
                            if (checkNotBusy(builder, player)) {
                                PreviewSubCommand.execute(builder, player);
                            }
                        }
                    };
                }
                case "collected-materials": {
                    int total = 0;
                    for (Integer amount : builder.ExcavateMaterials.values()) {
                        total += amount;
                    }
                    ItemStack itemStack = this.getItem(function);
                    ItemUT.replaceLore(itemStack, "%current%", String.valueOf(total));
                    return new Slot(itemStack) {
                        @Override
                        public void onLeftClick() {
                            ExcavatedSubCommand.execute(builder, player);
                            this.menu.open();
                        }
                    };
                }
                case "excavate": {
                    ItemStack itemStack = this.getItem(function);
                    ItemUT.replaceLore(itemStack, "%current%", String.valueOf(builder.isExcavate()));
                    return new Slot(itemStack) {
                        @Override
                        public void onLeftClick() {
                            builder.setExcavate(!builder.isExcavate());
                            this.menu.open();
                        }
                    };
                }
                case "ignore-air": {
                    ItemStack itemStack = this.getItem(function);
                    ItemUT.replaceLore(itemStack, "%current%", String.valueOf(builder.isIgnoreAir()));
                    return new Slot(itemStack) {
                        @Override
                        public void onLeftClick() {
                            builder.setIgnoreAir(!builder.isIgnoreAir());
                            this.menu.open();
                        }
                    };
                }
                case "layers": {
                    ItemStack itemStack = this.getItem(function);
                    ItemUT.replaceLore(itemStack, "%current%", String.valueOf(builder.getBuildYLayers()));
                    return new Slot(itemStack) {
                        @Override
                        public void onMiddleClick() {
                            Menu          menu      = this.menu;
                            BaseComponent component = new TextComponent("▸ Enter the desired layers, or ");
                            BaseComponent component1 =
                                    new TextComponent(ChatColor.GOLD.toString() + ChatColor.UNDERLINE + "cancel");
                            component1.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "cancel"));
                            component.addExtra(component1);
                            component.addExtra(new TextComponent(" to go back. "));
                            component1 = new TextComponent(
                                    ChatColor.GOLD.toString() + ChatColor.UNDERLINE + "Current value");
                            component1.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,
                                    String.valueOf(builder.getBuildYLayers())));
                            component.addExtra(component1);
                            player.spigot().sendMessage(component);

                            this.menu.fakeClose();
                            menu.registerListener(new Listener() {
                                @EventHandler
                                public void onChat(AsyncPlayerChatEvent event) {
                                    if (!event.getPlayer().equals(player)) {
                                        return;
                                    }
                                    HandlerList.unregisterAll(this);
                                    event.setCancelled(true);
                                    String message = event.getMessage().strip();
                                    if (!message.equalsIgnoreCase("cancel")) {
                                        try {
                                            builder.setBuildYLayers(Integer.parseInt(message));
                                        } catch (NumberFormatException e) {
                                            player.sendMessage("Invalid number '" + message + '\'');
                                        }
                                    }
                                    menu.openSync();
                                }
                            });
                        }

                        @Override
                        public void onLeftClick() {
                            builder.setBuildYLayers(builder.getBuildYLayers() - 1);
                            this.menu.open();
                        }

                        @Override
                        public void onRightClick() {
                            builder.setBuildYLayers(builder.getBuildYLayers() + 1);
                            this.menu.open();
                        }

                        @Override
                        public void onDrop() {
                            builder.setBuildYLayers(1);
                            this.menu.open();
                        }
                    };
                }
                case "silent": {
                    boolean   isSilent  = builder.isSilent();
                    ItemStack itemStack = this.getItem(function + '-' + isSilent);
                    ItemUT.replaceLore(itemStack, "%current%", String.valueOf(isSilent));
                    return new Slot(itemStack) {
                        @Override
                        public void onLeftClick() {
                            builder.setSilent(!builder.isSilent());
                            this.menu.open();
                        }
                    };
                }
                case "citizens": {
                    ItemStack    itemStack = this.getItem(function);
                    List<String> actions   = new ArrayList<>();
                    actions.add(ChatColor.BLUE + "On Start: " + ChatColor.WHITE + builder.getOnStart());
                    actions.add(ChatColor.BLUE + "On Complete: " + ChatColor.WHITE + builder.getOnComplete());
                    actions.add(ChatColor.BLUE + "On Cancel: " + ChatColor.WHITE + builder.getOnCancel());
                    ItemUT.replaceLore(itemStack, "%current%", actions);
                    return new Slot(itemStack) {
                        @Override
                        public void onLeftClick() {
                            if (checkNotBusy(builder, player)) {
                                this.menu.openSubMenu(new CitizensActionsMenu(player, builder.getNPC()));
                            }
                        }
                    };
                }
                case "needs-materials": {
                    boolean   needsMaterials = builder.requiresMaterials();
                    ItemStack itemStack      = this.getItem(function + '-' + needsMaterials);
                    ItemUT.replaceLore(itemStack, "%current%", String.valueOf(needsMaterials));
                    return new Slot(itemStack) {
                        @Override
                        public void onLeftClick() {
                            builder.setRequireMaterials(!needsMaterials);
                            this.menu.open();
                        }
                    };
                }
                case "hold-item": {
                    boolean   holdsItems = builder.holdsItems();
                    ItemStack itemStack  = this.getItem(function);
                    ItemUT.replaceLore(itemStack, "%current%", String.valueOf(holdsItems));
                    return new Slot(itemStack) {
                        @Override
                        public void onLeftClick() {
                            builder.setHoldsItems(!holdsItems);
                            this.menu.open();
                        }
                    };
                }
                case "ignore-liquid": {
                    boolean   ignoresLiquids = builder.ignoresLiquids();
                    ItemStack itemStack      = this.getItem(function);
                    ItemUT.replaceLore(itemStack, "%current%", String.valueOf(ignoresLiquids));
                    return new Slot(itemStack) {
                        @Override
                        public void onLeftClick() {
                            builder.setIgnoresLiquids(!ignoresLiquids);
                            this.menu.open();
                        }
                    };
                }
                case "build-pattern": {
                    ItemStack itemStack = this.getItem(function);
                    ItemUT.replaceLore(itemStack, "%current%", builder.getBuildPatternXZ().name().toLowerCase());
                    return new Slot(itemStack) {
                        @Override
                        public void onLeftClick() {
                            if (checkNotBusy(builder, player)) {
                                this.menu.openSubMenu(new BuildPatternMenu(player, builder.getNPC()));
                            }
                        }
                    };
                }
                case "timeout": {
                    ItemStack itemStack = this.getItem(function);
                    ItemUT.replaceLore(itemStack, "%current%", String.valueOf(builder.getMoveTimeoutSeconds()));
                    return new Slot(itemStack) {
                        @Override
                        public void onMiddleClick() {
                            Menu          menu      = this.menu;
                            BaseComponent component = new TextComponent("▸ Enter the building timeout, or ");
                            BaseComponent component1 =
                                    new TextComponent(ChatColor.GOLD.toString() + ChatColor.UNDERLINE + "cancel");
                            component1.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "cancel"));
                            component.addExtra(component1);
                            component.addExtra(new TextComponent(" to go back. "));
                            component1 = new TextComponent(
                                    ChatColor.GOLD.toString() + ChatColor.UNDERLINE + "Current value");
                            component1.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,
                                    String.valueOf(builder.getMoveTimeoutSeconds())));
                            component.addExtra(component1);
                            player.spigot().sendMessage(component);

                            this.menu.fakeClose();
                            menu.registerListener(new Listener() {
                                @EventHandler
                                public void onChat(AsyncPlayerChatEvent event) {
                                    if (!event.getPlayer().equals(player)) {
                                        return;
                                    }
                                    HandlerList.unregisterAll(this);
                                    event.setCancelled(true);
                                    String message = event.getMessage().strip();
                                    if (!message.equalsIgnoreCase("cancel")) {
                                        try {
                                            builder.setMoveTimeoutSeconds(Double.parseDouble(message));
                                        } catch (NumberFormatException e) {
                                            player.sendMessage("Invalid number '" + message + '\'');
                                        }
                                    }
                                    menu.openSync();
                                }
                            });
                        }

                        @Override
                        public void onLeftClick() {
                            builder.setMoveTimeoutSeconds(builder.getMoveTimeoutSeconds() - 1);
                            this.menu.open();
                        }

                        @Override
                        public void onRightClick() {
                            builder.setMoveTimeoutSeconds(builder.getMoveTimeoutSeconds() + 1);
                            this.menu.open();
                        }

                        @Override
                        public void onDrop() {
                            builder.setMoveTimeoutSeconds(1);
                            this.menu.open();
                        }
                    };
                }
                case "queue-by-category": {
                    ItemStack itemStack = this.getItem(function);
                    ItemUT.replaceLore(itemStack, "%current%", String.valueOf(builder.GroupByLayer));
                    return new Slot(itemStack) {
                        @Override
                        public void onLeftClick() {
                            builder.GroupByLayer = !builder.GroupByLayer;
                            this.menu.open();
                        }
                    };
                }
                case "load-entities": {
                    ItemStack itemStack = this.getItem(function);
                    ItemUT.replaceLore(itemStack, "%current%", String.valueOf(builder.isLoadEntities()));
                    return new Slot(itemStack) {
                        @Override
                        public void onLeftClick() {
                            builder.setLoadEntities(!builder.isLoadEntities());
                            this.menu.open();
                        }
                    };
                }
            }
            return null;
        }
    }

    private static boolean checkNotBusy(BuilderTrait builder, Player player) {
        switch (builder.getState()) {
            case IDLE:
            case COLLECTING: {
                return true;
            }
            default: {
                player.sendMessage(Blueprint.format(
                        Blueprint.getInstance().config().getCantWhileBuildingMessage(),
                        builder.getNPC(),
                        builder.getSchematic(),
                        player,
                        null, "0"));
                return false;
            }
        }
    }

    protected final NPC npc;

    public BuilderMenu(Player player, NPC npc) {
        super(player,
                CONFIG.getRows(),
                CONFIG.getTitle(Objects.requireNonNull(Blueprint.getBuilder(npc),
                        npc.getName() + " is not a builder")));
        this.npc = npc;
    }

    @Override
    public void setContents() {
        BuilderTrait builder = Blueprint.getBuilder(npc);
        Preconditions.checkArgument(builder != null, npc.getName() + " is not a builder");
        CONFIG.setSlots(this, builder);
    }

    private static class ParticleCuboid {
        private Vector origin;
        private Vector size;

        public ParticleCuboid(Vector origin, Vector size) {
            this.origin = new Vector(origin.getBlockX(), origin.getBlockY(), origin.getBlockZ());
            this.size = new Vector(size.getBlockX(), size.getBlockY(), size.getBlockZ());
        }

        public void rotate(int rotations) {
            this.size = Util.rotateVector(this.size, rotations);
            if (rotations == 1) {
                this.origin.setX(this.origin.getX() + 1);
            } else if (rotations == 2) {
                this.origin.setX(this.origin.getX() + 1);
                this.origin.setZ(this.origin.getZ() + 1);
            } else if (rotations == 3) {
                this.origin.setZ(this.origin.getZ() + 1);
            }
        }

        public void render(Player player) {
            double width  = this.size.getX();
            double height = this.size.getY();
            double length = this.size.getZ();

            double offset = width / 5;
            int    amount = Math.max(1, Math.abs((int) width * 5));
            double x      = this.origin.getX() + width / 2;
            double y      = this.origin.getY();
            double z      = this.origin.getZ();

            player.spawnParticle(Particle.CRIT, x, y, z, amount, offset, 0, 0, 0);
            player.spawnParticle(Particle.CRIT, x, y + height, z, amount, offset, 0, 0, 0);
            player.spawnParticle(Particle.CRIT, x, y, z + length, amount, offset, 0, 0, 0);
            player.spawnParticle(Particle.CRIT, x, y + height, z + length, amount, offset, 0, 0, 0);

            x = this.origin.getX();
            y += height / 2;
            offset = height / 5;
            amount = Math.max(1, Math.abs((int) height * 5));
            player.spawnParticle(Particle.CRIT, x, y, z, amount, 0, offset, 0, 0);
            player.spawnParticle(Particle.CRIT, x + width, y, z, amount, 0, offset, 0, 0);
            player.spawnParticle(Particle.CRIT, x, y, z + length, amount, 0, offset, 0, 0);
            player.spawnParticle(Particle.CRIT, x + width, y, z + length, amount, 0, offset, 0, 0);

            y = this.origin.getY();
            z += length / 2;
            offset = length / 5;
            amount = Math.max(5, Math.abs((int) length * 5));
            player.spawnParticle(Particle.CRIT, x, y, z, amount, 0, 0, offset, 0);
            player.spawnParticle(Particle.CRIT, x + width, y, z, amount, 0, 0, offset, 0);
            player.spawnParticle(Particle.CRIT, x, y + height, z, amount, 0, 0, offset, 0);
            player.spawnParticle(Particle.CRIT, x + width, y + height, z, amount, 0, 0, offset, 0);
        }
    }
}
