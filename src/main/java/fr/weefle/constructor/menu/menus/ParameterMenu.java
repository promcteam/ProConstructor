package fr.weefle.constructor.menu.menus;

import com.google.common.base.Preconditions;
import fr.weefle.constructor.SchematicBuilder;
import fr.weefle.constructor.hooks.citizens.BuilderTrait;
import fr.weefle.constructor.menu.Menu;
import fr.weefle.constructor.menu.Slot;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

public class ParameterMenu extends Menu {
    protected final NPC npc;

    public ParameterMenu(Player player, NPC npc) {
        super(player, 1, "SchematicBuilder - Parameters");
        this.npc = npc;
    }

    @Override
    public void setContents() {
        BuilderTrait builderTrait = SchematicBuilder.getBuilder(npc);
        Preconditions.checkArgument(builderTrait != null, npc.getName()+" is not a builder");
        ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD);
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            if (meta instanceof SkullMeta) {
                ((SkullMeta) meta).setOwner(npc.getName());
            }
            meta.setDisplayName(ChatColor.RESET+npc.getName());
            List<String> lore = new ArrayList<>();
            if (builderTrait.getState() == BuilderTrait.BuilderState.BUILDING) {
                lore.add(ChatColor.RESET + npc.getName() + " is building...");
                lore.add(ChatColor.GOLD + "Left-Click: " + ChatColor.YELLOW + "Cancel building");
            } else {
                lore.add(ChatColor.RESET + npc.getName() + " isn't building.");
                lore.add(ChatColor.GOLD + "Left-Click: " + ChatColor.YELLOW + "Start building!");
            }
            meta.setLore(lore);
            itemStack.setItemMeta(meta);
        }
        this.setSlot(0, new Slot(itemStack) {
            @Override
            public void onLeftClick() {
                if (builderTrait.getState() == BuilderTrait.BuilderState.BUILDING) {
                    builderTrait.CancelBuild();
                    player.sendMessage(ChatColor.RED + npc.getName() + " isn't building anymore.");
                    player.closeInventory();
                } else {
                    if (builderTrait.isRequireMaterials()) {
                        builderTrait.GetMatsList();
                    }
                    //Bukkit.getLogger().warning(plugin.getBuilder(npc).RequireMaterials.toString());
                    if (!builderTrait.TryBuild(player)) {
                        player.sendMessage(ChatColor.RED + npc.getName() + " needs a structure to build first!");
                    }
                }
            }
        });

        itemStack = new ItemStack(builderTrait.isExcavate() ? Material.GREEN_CONCRETE : Material.RED_CONCRETE);
        meta = itemStack.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.RESET+"Excavate");
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.AQUA + "Current: " + ChatColor.YELLOW + builderTrait.isExcavate());
            meta.setLore(lore);
            itemStack.setItemMeta(meta);
        }
        this.setSlot(1, new Slot(itemStack) {
            @Override
            public void onLeftClick() {
                builderTrait.setExcavate(!builderTrait.isExcavate());
                setContents();
                open();
            }
        });

        itemStack = new ItemStack(builderTrait.isIgnoreAir() ? Material.GREEN_CONCRETE : Material.RED_CONCRETE);
        meta = itemStack.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.RESET+"Ignore Air");
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.AQUA + "Current: " + ChatColor.GREEN + builderTrait.isIgnoreAir());
            meta.setLore(lore);
            itemStack.setItemMeta(meta);
        }
        this.setSlot(2, new Slot(itemStack) {
            @Override
            public void onLeftClick() {
                builderTrait.setIgnoreAir(!builderTrait.isIgnoreAir());
                setContents();
                open();
            }
        });

        itemStack = new ItemStack(builderTrait.isIgnoreLiquids() ? Material.GREEN_CONCRETE : Material.RED_CONCRETE);
        meta = itemStack.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.RESET+"Ignore Liquids");
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.AQUA + "Current: " + ChatColor.GREEN + builderTrait.isIgnoreLiquids());
            meta.setLore(lore);
            itemStack.setItemMeta(meta);
        }
        this.setSlot(3, new Slot(itemStack) {
            @Override
            public void onLeftClick() {
                builderTrait.setIgnoreLiquids(!builderTrait.isIgnoreLiquids());
                setContents();
                open();
            }
        });

        itemStack = new ItemStack(builderTrait.isRequireMaterials() ? Material.GREEN_CONCRETE : Material.RED_CONCRETE);
        meta = itemStack.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.RESET+"Require Materials");
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.AQUA + "Current: " + ChatColor.GREEN + builderTrait.isRequireMaterials());
            meta.setLore(lore);
            itemStack.setItemMeta(meta);
        }
        this.setSlot(4, new Slot(itemStack) {
            @Override
            public void onLeftClick() {
                builderTrait.setRequireMaterials(!builderTrait.isRequireMaterials());
                setContents();
                open();
            }
        });

        itemStack = new ItemStack(Material.BOOK);meta = itemStack.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.RESET+"Choose your schematic");
            itemStack.setItemMeta(meta);
        }
        this.setSlot(5, new Slot(itemStack) {
            @Override
            public void onLeftClick() {
                openSubMenu(new SchematicMenu(player));
            }
        });
    }
}
