package studio.magemonkey.blueprint.menus;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import studio.magemonkey.blueprint.Blueprint;
import studio.magemonkey.blueprint.commands.LoadSubCommand;
import studio.magemonkey.codex.manager.api.menu.FileExplorerMenu;
import studio.magemonkey.codex.manager.api.menu.Slot;

public class SchematicListMenu extends FileExplorerMenu {
    public SchematicListMenu(Player player) {
        super(player,
                Blueprint.getInstance().config().getSchematicsFolder(),
                6,
                "SchematicBuilder - Schematics",
                file -> {
                    String name = file.getName();
                    if (!name.endsWith(".schem") && !name.endsWith(".nbt") && !name.endsWith(".yml")) {
                        return null;
                    }
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
                meta.setDisplayName(ChatColor.RESET + fileName);
                this.itemStack.setItemMeta(meta);
            }
        }

        @Override
        public void onLeftClick() {
            Player player = menu.getPlayer();
            LoadSubCommand.execute(((FileExplorerMenu) this.menu).getPath() + this.fileName,
                    player,
                    player::closeInventory);
        }
    }
}
