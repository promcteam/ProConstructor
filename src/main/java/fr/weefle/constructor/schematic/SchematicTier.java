package fr.weefle.constructor.schematic;

import fr.weefle.constructor.SchematicBuilder;
import fr.weefle.constructor.hooks.citizens.BuilderTrait;
import fr.weefle.constructor.schematic.blocks.EmptyBuildBlock;
import fr.weefle.constructor.util.Util;
import mc.promcteam.engine.utils.StringUT;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.*;

public class SchematicTier extends Schematic {
    public static final  String PATH         = "path";
    public static final  String DISPLAY_NAME = "display-name";
    public static final  String OFFSET       = "offset";
    public static final  String MATERIALS    = "materials";
    public static final  String LORE  = "lore";
    private static final Vector ZERO      = new Vector(0, 0, 0);

    private final RawSchematic           handle;
    private final Vector                 offset;
    private final Map<Material, Integer> materials;
    private final List<String>           lore;

    public SchematicTier(Path path, ConfigurationSection config) {
        super(path);
        Schematic schematic = SchematicBuilder.getSchematic(path);
        if (!(schematic instanceof RawSchematic)) {
            throw new ClassCastException("Referenced schematics must be .schem or .nbt files");
        }
        this.handle = (RawSchematic) schematic;
        this.displayName = config.getString(DISPLAY_NAME);
        if (this.displayName != null) {this.displayName = StringUT.color(this.displayName);}

        ConfigurationSection section = config.getConfigurationSection(OFFSET);
        this.offset = section == null ? ZERO : new Vector(
                section.getInt("x", 0),
                section.getInt("y", 0),
                section.getInt("z", 0));

        section = config.getConfigurationSection(MATERIALS);
        if (section == null) {
            this.materials = null;
        } else {
            this.materials = new TreeMap<>();
            for (String key : section.getKeys(false)) {
                Material material = Material.valueOf(key.toUpperCase());
                this.materials.put(material, this.materials.getOrDefault(material, 0) + section.getInt(key));
            }
        }

        this.lore = Collections.unmodifiableList(StringUT.color(config.getStringList(LORE)));
    }

    @Override
    @Nullable
    public Vector getAbsolutePosition() {return this.handle.getAbsolutePosition();}

    @Override
    public int getWidth() {return this.handle.getWidth();}

    @Override
    public int getHeight() {return this.handle.getHeight();}

    @Override
    public int getLength() {return this.handle.getLength();}

    @Override
    @NotNull
    public EmptyBuildBlock getBlockAt(int x, int y, int z) {return this.handle.getBlockAt(x, y, z);}

    @Override
    @NotNull
    public Map<Material, Integer> getMaterials() {
        return this.materials == null
                ? this.handle.getMaterials()
                : this.materials;
    }

    @Override
    public Location offset(Location origin, double x, double y, double z, int emptyLayers, int rotation) {
        return this.handle.offset(origin, x, y, z, emptyLayers, rotation).add(Util.rotateVector(this.offset, rotation));
    }

    public List<String> getLore() {return lore;}

    @Override
    @NotNull
    public Queue<EmptyBuildBlock> buildQueue(BuilderTrait builder) {return this.handle.buildQueue(builder);}

    @Override
    public @NotNull Queue<SchematicEntity> getEntities() {
        return handle.getEntities();
    }
}
