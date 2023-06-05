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
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Objects;

public class BuilderMenu extends Menu {
    public static final YAMLMenu CONFIG = new YAMLMenu("builder");

    public static class YAMLMenu extends fr.weefle.constructor.menu.YAMLMenu<BuilderTrait> {
        public YAMLMenu(String name) {super(name);}

        @Override
        public String getTitle(String yamlTitle, BuilderTrait parameter) {
            return yamlTitle.replace("%npc%", parameter.getNPC().getName());
        }

        @Override
        public Slot getSlot(String function, BuilderTrait builder) {
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
                        public void onLeftClick() {} // TODO

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
                                this.menu.getPlayer().sendMessage(SchematicBuilder.format(
                                        SchematicBuilder.getInstance().config().getCancelMessage(),
                                        builder.getNPC(),
                                        builder.getSchematic(),
                                        this.menu.getPlayer(),
                                        null, "0"));
                                this.menu.open();
                            }
                        };
                    }
                    return new Slot(this.getItem(function+"-start")) {
                        @Override
                        public void onLeftClick() {
                            Player player = this.menu.getPlayer();
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
                            if (checkNotBusy(builder, this.menu.getPlayer())) {
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
                                this.menu.openSubMenu(new MaterialsMenu(this.menu.getPlayer(), builder));
                            }
                        }
                    };
                }
                case "settings": {
                    return new Slot(this.getItem(function)) {
                        @Override
                        public void onLeftClick() {
                            if (checkNotBusy(builder, this.menu.getPlayer())) {
                                // TODO
                            }
                        }
                    };
                }
                case "new-schematic": {
                    return new Slot(this.getItem(function)) {
                        @Override
                        public void onLeftClick() {
                            if (checkNotBusy(builder, this.menu.getPlayer())) {
                                this.menu.openSubMenu(new SchematicListMenu(this.menu.getPlayer()));
                            }
                        }
                    };
                }
                case "existing-structure": {
                    return new Slot(this.getItem(function)) {
                        @Override
                        public void onLeftClick() {
                            if (checkNotBusy(builder, this.menu.getPlayer())) {
                                // TODO
                            }
                        }
                    };
                }
                case "preview": {
                    return new Slot(this.getItem(function)) {
                        @Override
                        public void onLeftClick() {
                            if (checkNotBusy(builder, this.menu.getPlayer())) {
                                PreviewSubCommand.execute(builder, this.menu.getPlayer());
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
                            ExcavatedSubCommand.execute(builder, this.menu.getPlayer());
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
