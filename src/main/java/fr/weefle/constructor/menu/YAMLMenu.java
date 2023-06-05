package fr.weefle.constructor.menu;

import fr.weefle.constructor.SchematicBuilder;
import mc.promcteam.engine.utils.ItemUT;
import mc.promcteam.engine.utils.StringUT;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class YAMLMenu<T> {
    private final String name;
    private String title;
    private int rows;
    private Map<Integer,String> slots;
    private Map<String,ItemStack> items;

    public YAMLMenu(String name) {
        this.name = name;
        reload();
        SchematicBuilder.getInstance().registerYAMLMenu(this);
    }

    public void reload() {
        SchematicBuilder plugin = SchematicBuilder.getInstance();
        File             file   = new File(plugin.getDataFolder(), "menus/"+this.name+".yml");
        if (!file.exists()) {plugin.saveResource("menus/"+this.name+".yml", false);}
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        this.title = StringUT.color(config.getString("title", ""));
        this.rows = config.getInt("rows", 6);
        this.slots = new HashMap<>();
        this.items = new HashMap<>();
        ConfigurationSection section = config.getConfigurationSection("slots");
        if (section == null) {return;}
        for (String key : section.getKeys(false)) {
            try {
                int i = Integer.parseInt(key);
                if (i < 0) {throw new IllegalArgumentException();}
                String function = section.getString(key);
                if (function == null) {continue;}
                this.slots.put(i, function);
            } catch (NumberFormatException e) {
                plugin.getLogger().warning("Invalid index \""+key+"\" in "+this.name);
            }
        }

        section = config.getConfigurationSection("items");
        if (section == null) {return;}
        for (String key : section.getKeys(false)) {
            ConfigurationSection yamlItem = section.getConfigurationSection(key);
            if (yamlItem == null) {
                plugin.getLogger().warning("Invalid item \""+key+"\" in "+this.name);
                continue;
            }
            String materialString = yamlItem.getString("material", "dirt");
            Material material;
            try {
                material = Material.valueOf(materialString.toUpperCase());
            } catch (IllegalArgumentException e1) {
                plugin.getLogger().warning("Invalid material \""+materialString+"\" in "+this.name);
                material = Material.DIRT;
            }
            ItemStack itemStack = new ItemStack(material, yamlItem.getInt("amount", 1));
            String skullTexture = yamlItem.getString("skull-texture", null);
            if (skullTexture != null) {ItemUT.addSkullTexture(itemStack, skullTexture);}
            ItemMeta meta = itemStack.getItemMeta();
            if (meta != null) {
                String displayName = yamlItem.getString("display-name");
                if (displayName != null) {meta.setDisplayName(StringUT.color(displayName));}
                List<String> lore = StringUT.color(yamlItem.getStringList("lore"));
                if (lore.size() > 0) {meta.setLore(lore);}
                int cmd = yamlItem.getInt("custom-model-data", 0);
                if (cmd != 0) {meta.setCustomModelData(cmd);}
                itemStack.setItemMeta(meta);
            }
            this.items.put(key, itemStack);
        }
    }

    protected abstract String getTitle(String yamlTitle, T parameter);

    public String getTitle(T parameter) {return this.getTitle(this.title, parameter);}

    public int getRows() {return rows;}

    public boolean isEmpty() {return this.slots.isEmpty();}

    @NotNull
    public ItemStack getItem(String name) {return this.items.getOrDefault(name, new ItemStack(Material.AIR)).clone();}

    @Nullable
    public abstract Slot getSlot(String function, T parameter, Player player);

    public void setSlots(Menu menu, T parameter) {
        menu.slots.clear();
        Player player = menu.getPlayer();
        for (Map.Entry<Integer,String> entry : this.slots.entrySet()) {
            menu.setSlot(entry.getKey(), this.getSlot(entry.getValue(), parameter, player));
        }
        ItemStack emptySlot = this.getItem("empty");
        for (int i = 0, size = menu.slots.lastKey()/(this.getRows()*9)+1; i < size; i++) {
            menu.slots.putIfAbsent(i, new Slot(emptySlot));
        }
    }
}
