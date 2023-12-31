package fr.weefle.constructor.menus;

import fr.weefle.constructor.SchematicBuilder;
import mc.promcteam.engine.manager.api.menu.FileExplorerMenu;
import mc.promcteam.engine.manager.api.menu.Slot;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SchematicMenu extends FileExplorerMenu {
    public SchematicMenu(Player player) {
        super(player, SchematicBuilder.getInstance().config().getSchematicsFolder(), 6, "SchematicBuilder - Schematics", file -> {
            String name = file.getName();
            if (!name.endsWith(".schem") && !name.endsWith(".nbt") && !name.endsWith(".yml")) {return null;}
            return new SchematicSlot(name);
        });
    }

    private static class SchematicSlot extends Slot {
        private final String fileName;

        public SchematicSlot(String fileName) {
            super(new ItemStack(fileName.endsWith(".yml") ? Material.PAPER : Material.MAP));
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
