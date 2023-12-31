package fr.weefle.constructor.menus;

import fr.weefle.constructor.SchematicBuilder;
import fr.weefle.constructor.schematic.Schematic;
import fr.weefle.constructor.schematic.SchematicTier;
import fr.weefle.constructor.schematic.YAMLSchematic;
import mc.promcteam.engine.manager.api.menu.Menu;
import mc.promcteam.engine.manager.api.menu.Slot;
import mc.promcteam.engine.manager.api.menu.YAMLListMenu;
import mc.promcteam.engine.utils.ItemUT;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TiersMenu extends Menu {
    public static final YAMLListMenu<Schematic> CONFIG = new YAMLListMenu<>(SchematicBuilder.getInstance(), "menus/tiers.yml") {

        @Override
        protected String getTitle(String yamlTitle, Schematic schematic) {return yamlTitle;}

        @Override
        public List<Slot> getEntries(Schematic schematic) {
            List<Slot> slots;
            if (schematic instanceof YAMLSchematic) {
                YAMLSchematic yamlSchematic = (YAMLSchematic) schematic;
                List<SchematicTier> tierList = yamlSchematic.getTiers();
                slots = new ArrayList<>(yamlSchematic.getTotalTiers());
                for (int tier = 0, tiers = tierList.size(), currentTier = yamlSchematic.getTier(); tier < tiers; tier++) {
                    ItemStack itemStack;
                    if (tier <= currentTier) {
                        itemStack = this.getItem("tier-completed");
                    } else if (tier == currentTier + 1) {
                        itemStack = this.getItem("tier-in-progress");
                    } else {
                        itemStack = this.getItem("tier-unattained");
                    }
                    SchematicTier schematicTier = tierList.get(tier);
                    String    displayName = schematicTier.getDisplayName();
                    String    tierString  = String.valueOf(tier+1);
                    ItemUT.replaceLore(itemStack, "%name%", displayName);
                    ItemUT.replaceLore(itemStack, "%tier%", tierString);
                    ItemUT.replaceLore(itemStack, "%width%", String.valueOf(schematicTier.getWidth()));
                    ItemUT.replaceLore(itemStack, "%length%", String.valueOf(schematicTier.getLength()));
                    ItemUT.replaceLore(itemStack, "%height%", String.valueOf(schematicTier.getHeight()));
                    ItemUT.replaceLore(itemStack, "%lore%", schematicTier.getLore());
                    ItemMeta meta = itemStack.getItemMeta();
                    if (meta != null) {
                        meta.setDisplayName(meta.getDisplayName()
                                .replace("%name%", displayName)
                                .replace("%tier%", tierString));
                        itemStack.setItemMeta(meta);
                    }
                    slots.add(new Slot(itemStack));
                }
            } else {
                slots = new ArrayList<>(1);
                ItemStack itemStack = this.getItem("tier-in-progress");
                String    displayName = schematic.getDisplayName();
                String    tierString  = "1";
                ItemUT.replaceLore(itemStack, "%name%", displayName);
                ItemUT.replaceLore(itemStack, "%tier%", tierString);
                ItemUT.replaceLore(itemStack, "%width%", String.valueOf(schematic.getWidth()));
                ItemUT.replaceLore(itemStack, "%length%", String.valueOf(schematic.getLength()));
                ItemUT.replaceLore(itemStack, "%height%", String.valueOf(schematic.getHeight()));
                ItemUT.replaceLore(itemStack, "%lore%", new ArrayList<>());
                ItemMeta meta = itemStack.getItemMeta();
                if (meta != null) {
                    meta.setDisplayName(meta.getDisplayName()
                            .replace("%name%", displayName)
                            .replace("%tier%", tierString));
                    itemStack.setItemMeta(meta);
                }
                slots.add(new Slot(itemStack));
            }
            return slots;
        }

        @Override
        @Nullable
        public Slot getSlot(String function, Schematic schematic, Player player) {
            switch (function) {
                case "prev-page": {
                    return new PreviousPageButton(this.getItem(function));
                }
                case "next-page": {
                    return new NextPageButton(this.getItem(function));
                }
            }
            return null;
        }
    };

    private final Schematic schematic;

    public TiersMenu(Player player, Schematic schematic) {
        super(player, CONFIG.getRows(), CONFIG.getTitle(schematic));
        this.schematic = schematic;
    }

    @Override
    public void setContents() {CONFIG.setSlots(this, this.schematic);}
}
