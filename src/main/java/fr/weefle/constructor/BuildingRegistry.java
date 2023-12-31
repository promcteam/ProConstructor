package fr.weefle.constructor;

import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class BuildingRegistry {
    private static final String FILE_NAME = "persistent-buildings.yml";

    private final Map<UUID,PersistentBuilding> buildings = new HashMap<>();
    private       YamlConfiguration            config;

    void load() {
        SchematicBuilder plugin = SchematicBuilder.getInstance();
        File file = new File(plugin.getDataFolder(), FILE_NAME);
        if (!file.exists()) {plugin.saveResource(FILE_NAME, false);}
        this.config = YamlConfiguration.loadConfiguration(file);
        for (String key : this.config.getKeys(false)) {
            try {
                PersistentBuilding building = new PersistentBuilding(key, Objects.requireNonNull(this.config.getConfigurationSection(key)));
                this.buildings.put(building.getUUID(), building);
            } catch (Exception e) {
                SchematicBuilder.getInstance().getLogger().warning("Failed to load building '"+key+'\'');
                e.printStackTrace();
            }
        }
    }

    void save() {
        try {
            this.config.save(new File(SchematicBuilder.getInstance().getDataFolder(), FILE_NAME));
        } catch (IOException e) {throw new RuntimeException(e);}
    }

    @Nullable
    public PersistentBuilding getPersistentBuilding(UUID uuid) {return this.buildings.get(uuid);}

    void registerBuilding(PersistentBuilding building) {
        this.buildings.put(building.getUUID(), building);
        this.config.set(building.getUUID().toString(), building.serialize());
    }

    @Nullable
    public PersistentBuilding getPersistentBuilding(Location location) {
        Vector vector = location.toVector();
        for (PersistentBuilding persistentBuilding : this.buildings.values()) {
            if (!persistentBuilding.getWorld().equals(location.getWorld())) {continue;}
            if (persistentBuilding.getBoundingBox().contains(vector)) {return persistentBuilding;}
        }
        return null;
    }
}
