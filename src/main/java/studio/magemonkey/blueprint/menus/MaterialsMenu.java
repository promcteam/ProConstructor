package studio.magemonkey.blueprint.menus;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import studio.magemonkey.blueprint.hooks.citizens.BuilderTrait;
import studio.magemonkey.codex.manager.api.menu.Menu;
import studio.magemonkey.codex.manager.api.menu.Slot;

import java.util.Map;
import java.util.Objects;

public class MaterialsMenu extends Menu {
    protected final BuilderTrait builder;

    public MaterialsMenu(Player player, @NotNull BuilderTrait builder) {
        super(player, 6, "SchematicBuilder - Materials");
        this.builder = Objects.requireNonNull(builder);
    }

    @Override
    public void setContents() {
        Map<Material, Integer> obtainedMaterials = this.builder.getStoredMaterials();
        int                    i                 = 0;
        for (Map.Entry<Material, Integer> entry : Objects.requireNonNull(this.builder.getSchematic(),
                this.builder.getName() + " has no schematic loaded").getMaterials().entrySet()) {
            Material material = entry.getKey();
            int      total    = entry.getValue() - obtainedMaterials.getOrDefault(material, 0);
            while (total > 0) {
                i++;
                if (i % this.inventory.getSize() == 53) {
                    this.setSlot(i, getNextButton());
                    i++;
                } else if (i % 9 == 8) {
                    i++;
                }
                if (i % this.inventory.getSize() == 45) {
                    this.setSlot(i, getPrevButton());
                    i++;
                } else if (i % 9 == 0) {
                    i++;
                }
                int amount = Math.min(total, 64);
                this.setSlot(i, new MaterialSlot(material, amount));
                total -= amount;
            }
        }
        this.setSlot(this.getPages() * this.inventory.getSize() - 9, getPrevButton());
        this.setSlot(this.getPages() * this.inventory.getSize() - 1, getNextButton());
    }

    private static class MaterialSlot extends Slot {
        public MaterialSlot(Material material, int amount) {
            super(new ItemStack(material, amount));
        }
    }
}
