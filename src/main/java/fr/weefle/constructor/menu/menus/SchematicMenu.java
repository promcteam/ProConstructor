package fr.weefle.constructor.menu.menus;

import fr.weefle.constructor.SchematicBuilder;
import fr.weefle.constructor.menu.FileExplorerMenu;
import fr.weefle.constructor.menu.Slot;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SchematicMenu extends FileExplorerMenu {
    public SchematicMenu(Player player) {
        super(player, SchematicBuilder.schematicsFolder, 6, "SchematicBuilder - Schematics", file -> {
            String name = file.getName();
            if (!name.endsWith(".schem") && !name.endsWith(".nbt")) {return null;}
            return new SchematicSlot(name);
        });
    }

    private static class SchematicSlot extends Slot {
        private final String fileName;

        public SchematicSlot(String fileName) {
            super(new ItemStack(Material.MAP));
            this.fileName = fileName;
            ItemMeta meta = this.itemStack.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(ChatColor.RESET+fileName);
                this.itemStack.setItemMeta(meta);
            }
        }

        @Override
        public void onLeftClick() {
            Player player = menu.getPlayer();
            player.performCommand("schematicbuilder load " +((FileExplorerMenu) menu).getPath()+fileName);
        }
    }
}
