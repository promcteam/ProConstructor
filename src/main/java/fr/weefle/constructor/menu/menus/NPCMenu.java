package fr.weefle.constructor.menu.menus;

import fr.weefle.constructor.SchematicBuilder;
import fr.weefle.constructor.citizens.BuilderTrait;
import fr.weefle.constructor.menu.Menu;
import fr.weefle.constructor.menu.Slot;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;

public class NPCMenu extends Menu {
    public NPCMenu(Player player) {
        super(player, 6, "SchematicBuilder - NPCs");
    }

    @Override
    public void setContents() {
        int i = 0;
        for (NPC npc : CitizensAPI.getNPCRegistry().sorted()) {
            i++;
            if (i%this.inventory.getSize() == 53) {
                this.setSlot(i, getNextButton());
                i++;
            } else if (i%9 == 8) { i++; }
            if (i%this.inventory.getSize() == 45) {
                this.setSlot(i, getPrevButton());
                i++;
            } else if (i%9 == 0) { i++; }
            this.setSlot(i, new NPCSlot(npc));
        }
        this.setSlot(this.getPages()*this.inventory.getSize()-9, getPrevButton());
        this.setSlot(this.getPages()*this.inventory.getSize()-1, getNextButton());
    }

    public static class NPCSlot extends Slot {
        private final NPC npc;

        public NPCSlot(NPC npc) {
            super(new ItemStack(Material.PLAYER_HEAD));
            this.npc = npc;
            ItemMeta meta = this.itemStack.getItemMeta();
            if (meta != null) {
                if (meta instanceof SkullMeta) {
                    SkullMeta skullMeta = (SkullMeta) meta;
                    skullMeta.setOwner(npc.getName());
                }
                meta.setDisplayName(ChatColor.RESET+"<"+npc.getId()+"> "+npc.getName());
                ArrayList<String> lore = new ArrayList<>();
                if (npc.hasTrait(BuilderTrait.class)) {
                    lore.add(ChatColor.GREEN+"This NPC can build.");
                    lore.add(ChatColor.GOLD+"Left-Click: "+ChatColor.YELLOW+"Enter parameters");
                    lore.add(ChatColor.GOLD+"Right-click: "+ChatColor.YELLOW+"Remove builder's trait");
                } else {
                    lore.add(ChatColor.RED+"This NPC can't build!");
                    lore.add(ChatColor.GOLD+"Right-click: "+ChatColor.YELLOW+"Add builder's trait");
                }
                meta.setLore(lore);
                this.itemStack.setItemMeta(meta);
            }
        }

        public NPC getNpc() {return npc;}

        @Override
        public void onLeftClick() {
            BuilderTrait builderTrait = SchematicBuilder.getBuilder(npc);
            Player player = menu.getPlayer();
            if (builderTrait == null) {
                player.sendMessage(ChatColor.GOLD + npc.getName() + ChatColor.RED + " isn't a builder, right-click on it to make it one!");
                return;
            }
            player.performCommand("npc select " + npc.getId());
            menu.openSubMenu(new ParameterMenu(player, npc));
        }

        @Override
        public void onRightClick() {
            if (npc.hasTrait(BuilderTrait.class)) {
                npc.removeTrait(BuilderTrait.class);
            } else {
                npc.addTrait(BuilderTrait.class);
            }
            menu.setContents();
            menu.open(menu.getPage());
        }
    }
}
