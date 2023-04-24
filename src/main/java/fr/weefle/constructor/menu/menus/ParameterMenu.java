package fr.weefle.constructor.menu.menus;

import com.google.common.base.Preconditions;
import fr.weefle.constructor.SchematicBuilder;
import fr.weefle.constructor.essentials.BuilderTrait;
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
            if (builderTrait.State == BuilderTrait.BuilderState.building) {
                lore.add(ChatColor.RESET+npc.getName() + " is building...");
                lore.add(ChatColor.GOLD+"Left-Click: "+ChatColor.YELLOW+"Cancel building");
            } else {
                lore.add(ChatColor.RESET+npc.getName() + " isn't building.");
                lore.add(ChatColor.GOLD+"Left-Click: "+ChatColor.YELLOW+"Start building!");
            }
            meta.setLore(lore);
            itemStack.setItemMeta(meta);
        }
        this.setSlot(0, new Slot(itemStack) {
            @Override
            public void onLeftClick() {
                if (builderTrait.State == BuilderTrait.BuilderState.building) {
                    builderTrait.CancelBuild();
                    player.sendMessage(ChatColor.RED + npc.getName() + " isn't building anymore.");
                    player.closeInventory();
                } else {
                    if (builderTrait.RequireMaterials) {
                        builderTrait.GetMatsList(builderTrait.Excavate);
                    }
                    //Bukkit.getLogger().warning(plugin.getBuilder(npc).RequireMaterials.toString());
                    if (!builderTrait.TryBuild(player)) {
                        player.sendMessage(ChatColor.RED + npc.getName() + " needs a structure to build first!");
                    }
                }
            }
        });

        itemStack = new ItemStack(builderTrait.Excavate ? Material.GREEN_CONCRETE : Material.RED_CONCRETE) ;
        meta = itemStack.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.RESET+"Excavate");
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.AQUA+"Current: "+ChatColor.YELLOW+builderTrait.Excavate);
            meta.setLore(lore);
            itemStack.setItemMeta(meta);
        }
        this.setSlot(1, new Slot(itemStack) {
            @Override
            public void onLeftClick() {
                builderTrait.Excavate = !builderTrait.Excavate;
                setContents();
                open();
            }
        });

        itemStack = new ItemStack(builderTrait.IgnoreAir ? Material.GREEN_CONCRETE : Material.RED_CONCRETE) ;
        meta = itemStack.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.RESET+"Ignore Air");
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.AQUA+"Current: "+ChatColor.GREEN+builderTrait.IgnoreAir);
            meta.setLore(lore);
            itemStack.setItemMeta(meta);
        }
        this.setSlot(2, new Slot(itemStack) {
            @Override
            public void onLeftClick() {
                builderTrait.IgnoreAir = !builderTrait.IgnoreAir;
                setContents();
                open();
            }
        });

        itemStack = new ItemStack(builderTrait.IgnoreLiquid ? Material.GREEN_CONCRETE : Material.RED_CONCRETE) ;
        meta = itemStack.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.RESET+"Ignore Liquids");
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.AQUA+"Current: "+ChatColor.GREEN+builderTrait.IgnoreLiquid);
            meta.setLore(lore);
            itemStack.setItemMeta(meta);
        }
        this.setSlot(3, new Slot(itemStack) {
            @Override
            public void onLeftClick() {
                builderTrait.IgnoreLiquid = !builderTrait.IgnoreLiquid;
                setContents();
                open();
            }
        });

        itemStack = new ItemStack(builderTrait.RequireMaterials ? Material.GREEN_CONCRETE : Material.RED_CONCRETE) ;
        meta = itemStack.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.RESET+"Require Materials");
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.AQUA+"Current: "+ChatColor.GREEN+builderTrait.RequireMaterials);
            meta.setLore(lore);
            itemStack.setItemMeta(meta);
        }
        this.setSlot(4, new Slot(itemStack) {
            @Override
            public void onLeftClick() {
                builderTrait.RequireMaterials = !builderTrait.RequireMaterials;
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
