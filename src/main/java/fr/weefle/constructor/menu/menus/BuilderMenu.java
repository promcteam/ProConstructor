package fr.weefle.constructor.menu.menus;

import com.google.common.base.Preconditions;
import fr.weefle.constructor.SchematicBuilder;
import fr.weefle.constructor.commands.ExcavatedSubCommand;
import fr.weefle.constructor.commands.PreviewSubCommand;
import fr.weefle.constructor.hooks.citizens.BuilderTrait;
import fr.weefle.constructor.menu.Menu;
import fr.weefle.constructor.menu.Slot;
import fr.weefle.constructor.menu.YAMLMenu;
import fr.weefle.constructor.schematic.Schematic;
import fr.weefle.constructor.schematic.YAMLSchematic;
import mc.promcteam.engine.utils.ItemUT;
import net.citizensnpcs.api.npc.NPC;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BuilderMenu extends Menu {
    public static final YAMLBuilderMenu CONFIG = new YAMLBuilderMenu("builder");

    public static class YAMLBuilderMenu extends YAMLMenu<BuilderTrait> {
        public YAMLBuilderMenu(String name) {super(name);}

        @Override
        public String getTitle(String yamlTitle, BuilderTrait parameter) {
            return yamlTitle.replace("%npc%", parameter.getNPC().getName());
        }

        @Override
        public Slot getSlot(String function, BuilderTrait builder, Player player) {
            switch (function) {
                case "schematic": {
                    Schematic schematic = builder.getSchematic();
                    if (schematic == null) {return new Slot(this.getItem(function+"-null"));}
                    int tier = schematic instanceof YAMLSchematic ? ((YAMLSchematic) schematic).getTier()+1 : 1;
                    ItemStack itemStack = this.getItem(function+"-notnull");
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
                        return new Slot(this.getItem(function+"-empty")) {};
                    }
                    if (!builder.getMissingMaterials().isEmpty()) {
                        return new Slot(this.getItem(function+"-missing-materials")) {};
                    }
                    if (builder.getState() == BuilderTrait.BuilderState.BUILDING) {
                        return new Slot(this.getItem(function+"-stop")) {
                            @Override
                            public void onLeftClick() {
                                builder.CancelBuild();
                                player.sendMessage(SchematicBuilder.format(
                                        SchematicBuilder.getInstance().config().getCancelMessage(),
                                        builder.getNPC(),
                                        builder.getSchematic(),
                                        player,
                                        null, "0"));
                                this.menu.open();
                            }
                        };
                    }
                    return new Slot(this.getItem(function+"-start")) {
                        @Override
                        public void onLeftClick() {
                            if (!builder.TryBuild(player)) {
                                player.sendMessage(ChatColor.RED + builder.getNPC().getName() + " needs a structure to build first!");
                            }
                        }
                    };
                }
                case "origin": {
                    Location  origin    = builder.getOrigin();
                    ItemStack itemStack = this.getItem(function);
                    ItemUT.replaceLore(itemStack, "%world%", Objects.requireNonNull(origin.getWorld().getName()));
                    ItemUT.replaceLore(itemStack, "%x%", String.valueOf(origin.getBlockX()));
                    ItemUT.replaceLore(itemStack, "%y%", String.valueOf(origin.getBlockY()));
                    ItemUT.replaceLore(itemStack, "%z%", String.valueOf(origin.getBlockZ()));
                    return new Slot(itemStack) {
                        @Override
                        public void onLeftClick() {
                            if (checkNotBusy(builder, player)) {
                                // TODO
                            }
                        }
                    };
                }
                case "required-materials": {
                    int total = 0;
                    if (builder.getSchematic() != null) {
                        for (Integer amount : builder.getMissingMaterials().values()) {total += amount;}
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
                        return new Slot(this.getItem(function+"-admin")) {
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
                        return new Slot(this.getItem(function+"-player")) {
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
                                // TODO
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
                    for (Integer amount : builder.ExcavateMaterials.values()) {total += amount;}
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
                            Menu   menu   = this.menu;
                            BaseComponent component  = new TextComponent("▸ Enter the desired layers, or ");
                            BaseComponent component1 = new TextComponent(ChatColor.GOLD.toString()+ChatColor.UNDERLINE+"cancel");
                            component1.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "cancel"));
                            component.addExtra(component1);
                            component.addExtra(new TextComponent(" to go back. "));
                            component1 = new TextComponent(ChatColor.GOLD.toString()+ChatColor.UNDERLINE+"Current value");
                            component1.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, String.valueOf(builder.getBuildYLayers())));
                            component.addExtra(component1);
                            player.spigot().sendMessage(component);

                            this.menu.fakeClose();
                            menu.registerListener(new Listener() {
                                @EventHandler
                                public void onChat(AsyncPlayerChatEvent event) {
                                    if (!event.getPlayer().equals(player)) {return;}
                                    HandlerList.unregisterAll(this);
                                    event.setCancelled(true);
                                    String message = event.getMessage().strip();
                                    if (!message.equalsIgnoreCase("cancel")) {
                                        try {
                                            builder.setBuildYLayers(Integer.parseInt(message));
                                        } catch (NumberFormatException e) {
                                            player.sendMessage("Invalid number '"+message+'\'');
                                        }
                                    }
                                    menu.open();
                                }
                            });
                        }

                        @Override
                        public void onLeftClick() {
                            builder.setBuildYLayers(builder.getBuildYLayers()-1);
                            this.menu.open();
                        }

                        @Override
                        public void onRightClick() {
                            builder.setBuildYLayers(builder.getBuildYLayers()+1);
                            this.menu.open();
                        }

                        @Override
                        public void onDrop() {
                            // TODO default value
                        }
                    };
                }
                case "silent": {
                    boolean isSilent = builder.isSilent();
                    ItemStack itemStack = this.getItem(function+'-'+isSilent);
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
                    actions.add(ChatColor.BLUE+"On Start: "+ChatColor.WHITE+builder.getOnStart());
                    actions.add(ChatColor.BLUE+"On Complete: "+ChatColor.WHITE+builder.getOnComplete());
                    actions.add(ChatColor.BLUE+"On Cancel: "+ChatColor.WHITE+builder.getOnCancel());
                    ItemUT.replaceLore(itemStack, "%current%", actions);
                    return new Slot(itemStack) {
                        @Override
                        public void onLeftClick() {
                            // TODO
                        }
                    };
                }
                case "needs-materials": {
                    boolean needsMaterials = builder.requiresMaterials();
                    ItemStack itemStack = this.getItem(function+'-'+needsMaterials);
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
                    boolean holdsItems = builder.holdsItems();
                    ItemStack itemStack = this.getItem(function);
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
                    boolean ignoresLiquids = builder.ignoresLiquids();
                    ItemStack itemStack = this.getItem(function);
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
                            // TODO
                        }
                    };
                }
                case "timeout": {
                    ItemStack itemStack = this.getItem(function);
                    ItemUT.replaceLore(itemStack, "%current%", String.valueOf(builder.getMoveTimeoutSeconds()));
                    return new Slot(itemStack) {
                        @Override
                        public void onMiddleClick() {
                            Menu menu = this.menu;
                            BaseComponent component = new TextComponent("▸ Enter the building timeout, or ");
                            BaseComponent component1 = new TextComponent(ChatColor.GOLD.toString()+ChatColor.UNDERLINE+"cancel");
                            component1.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "cancel"));
                            component.addExtra(component1);
                            component.addExtra(new TextComponent(" to go back. "));
                            component1 = new TextComponent(ChatColor.GOLD.toString()+ChatColor.UNDERLINE+"Current value");
                            component1.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, String.valueOf(builder.getMoveTimeoutSeconds())));
                            component.addExtra(component1);
                            player.spigot().sendMessage(component);

                            this.menu.fakeClose();
                            menu.registerListener(new Listener() {
                                @EventHandler
                                public void onChat(AsyncPlayerChatEvent event) {
                                    if (!event.getPlayer().equals(player)) {return;}
                                    HandlerList.unregisterAll(this);
                                    event.setCancelled(true);
                                    String message = event.getMessage().strip();
                                    if (!message.equalsIgnoreCase("cancel")) {
                                        try {
                                            builder.setMoveTimeoutSeconds(Double.parseDouble(message));
                                        } catch (NumberFormatException e) {
                                            player.sendMessage("Invalid number '"+message+'\'');
                                        }
                                    }
                                    menu.open();
                                }
                            });
                        }
                        @Override
                        public void onLeftClick() {
                            builder.setMoveTimeoutSeconds(builder.getMoveTimeoutSeconds()-1);
                            this.menu.open();
                        }

                        @Override
                        public void onRightClick() {
                            builder.setMoveTimeoutSeconds(builder.getMoveTimeoutSeconds()+1);
                            this.menu.open();
                        }

                        @Override
                        public void onDrop() {
                            // TODO default value
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
            }
            return null;
        }
    };

    private static boolean checkNotBusy(BuilderTrait builder, Player player) {
        switch (builder.getState()) {
            case IDLE: case COLLECTING: {
                return true;
            }
            default: {
                player.sendMessage(SchematicBuilder.format(
                        SchematicBuilder.getInstance().config().getCantWhileBuildingMessage(),
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
        super(player, CONFIG.getRows(), CONFIG.getTitle(Objects.requireNonNull(SchematicBuilder.getBuilder(npc), npc.getName()+" is not a builder")));
        this.npc = npc;
    }

    @Override
    public void setContents() {
        BuilderTrait builder = SchematicBuilder.getBuilder(npc);
        Preconditions.checkArgument(builder != null, npc.getName()+" is not a builder");
        CONFIG.setSlots(this, builder);
    }
}
