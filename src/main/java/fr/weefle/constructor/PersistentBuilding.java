package fr.weefle.constructor;

import fr.weefle.constructor.schematic.YAMLSchematic;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class PersistentBuilding implements ConfigurationSerializable {
    private final UUID        uuid;
    private final String      path;
    private       int         tier;
    private final World       world;
    private final BoundingBox boundingBox; // TODO prevent overlap

    public PersistentBuilding(UUID uuid, YAMLSchematic schematic, int tier, Location origin) {
        this.uuid = uuid;
        this.path = new File(SchematicBuilder.getInstance().config().getSchematicsFolder()).toPath().relativize(new File(schematic.getPath()).toPath()).toString();
        this.tier = tier;
        this.world = Objects.requireNonNull(origin.getWorld(), "provided origin location does not refer to a world");
        this.boundingBox = BoundingBox.of(origin, origin.clone().add(
                schematic.getWidth(),
                schematic.getHeight(),
                schematic.getLength()));
        SchematicBuilder.getInstance().getBuildingRegistry().registerBuilding(this);
    }

    PersistentBuilding(String uuid, ConfigurationSection config) {
        this(UUID.fromString(uuid),
                (YAMLSchematic) Objects.requireNonNull(SchematicBuilder.getSchematic(config.getString("path", null))),
                config.getInt("tier"),
                new Location(Bukkit.getWorld(Objects.requireNonNull(config.getString("world", null))),
                        config.getInt("x"),
                        config.getInt("y"),
                        config.getInt("z")));
    }

    public UUID getUUID() {return uuid;}

    public String getPath() {return path;}


    public YAMLSchematic getSchematic() {
        YAMLSchematic schematic = (YAMLSchematic) Objects.requireNonNull(SchematicBuilder.getSchematic(this.path));
        schematic.setNextTier(this.tier+1);
        return schematic;
    }

    public World getWorld() {return world;}

    public BoundingBox getBoundingBox() {return boundingBox.clone();}

    public int getTier() {return tier;}

    public void setTier(int tier) {
        this.tier = tier;
    }

    @Override
    @NotNull
    public Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();
        data.put("path", this.path);
        data.put("tier", this.tier);
        data.put("world", this.world.getName());
        data.put("x", (int) this.boundingBox.getMinX());
        data.put("y", (int) this.boundingBox.getMinY());
        data.put("z", (int) this.boundingBox.getMinZ());
        return data;
    }
}
