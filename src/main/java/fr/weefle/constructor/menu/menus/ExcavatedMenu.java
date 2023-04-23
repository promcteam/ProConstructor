package fr.weefle.constructor.menu.menus;

import fr.weefle.constructor.SchematicBuilder;
import fr.weefle.constructor.menu.Menu;
import fr.weefle.constructor.menu.Slot;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.Objects;

public class ExcavatedMenu extends Menu {
    protected final NPC npc;

    public ExcavatedMenu(Player player, NPC npc) {
        super(player, 6, "SchematicBuilder - Excavated");
        this.npc = npc;
    }

    @Override
    public void setContents() {
        int i = 0;
        for (Map.Entry<Material, Integer> entry : Objects.requireNonNull(SchematicBuilder.getInstance().getBuilder(npc),
                npc.getName() + " is not a builder").ExcavateMaterials.entrySet()) {
            int total = entry.getValue();
            while (total > 0) {
                i++;
                if (i % this.inventory.getSize() == 53) {
                    this.setSlot(i, getNextButton());
                    i++;
                } else if (i % 9 == 8) {i++;}
                if (i % this.inventory.getSize() == 45) {
                    this.setSlot(i, getPrevButton());
                    i++;
                } else if (i % 9 == 0) {i++;}
                int amount = Math.min(total, 64);
                this.setSlot(i, new Slot(new ItemStack(entry.getKey(), amount)));
                total -= amount;
            }
        }
        this.setSlot(this.getPages() * this.inventory.getSize() - 9, getPrevButton());
        this.setSlot(this.getPages() * this.inventory.getSize() - 1, getNextButton());
    }
}
