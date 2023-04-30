package fr.weefle.constructor.menu.menus;

import com.google.common.base.Preconditions;
import fr.weefle.constructor.SchematicBuilder;
import fr.weefle.constructor.hooks.citizens.BuilderTrait;
import fr.weefle.constructor.menu.Menu;
import fr.weefle.constructor.menu.Slot;
import fr.weefle.constructor.schematic.Schematic;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.Objects;

public class MaterialsMenu extends Menu {
    protected final NPC npc;

    public MaterialsMenu(Player player, NPC npc) {
        super(player, 6, "SchematicBuilder - Materials");
        Preconditions.checkArgument(SchematicBuilder.getBuilder(npc) != null, npc.getName()+" is not a builder");
        this.npc = npc;
    }

    @Override
    public void setContents() {
        BuilderTrait builder = Objects.requireNonNull(SchematicBuilder.getBuilder(npc), npc.getName()+" is not a builder");
        Map<Material,Integer> obtainedMaterials = builder.getStoredMaterials();
        int i = 0;
        for (Map.Entry<Material,Integer> entry : Objects.requireNonNull(builder.getSchematic(), npc.getName()+" has no schematic loaded").getMaterials().entrySet()) {
            Material material = entry.getKey();
            int total = entry.getValue()-obtainedMaterials.getOrDefault(material, 0);
            while (total > 0) {
                i++;
                if (i%this.inventory.getSize() == 53) {
                    this.setSlot(i, getNextButton());
                    i++;
                } else if (i%9 == 8) { i++; }
                if (i%this.inventory.getSize() == 45) {
                    this.setSlot(i, getPrevButton());
                    i++;
                } else if (i%9 == 0) { i++; }
                int amount = Math.min(total, 64);
                this.setSlot(i, new MaterialSlot(material, amount));
                total -= amount;
            }
        }
        this.setSlot(this.getPages()*this.inventory.getSize()-9, getPrevButton());
        this.setSlot(this.getPages()*this.inventory.getSize()-1, getNextButton());
    }

    private static class MaterialSlot extends Slot {
        public MaterialSlot(Material material, int amount) {
            super(new ItemStack(material, amount));
        }
    }
}
