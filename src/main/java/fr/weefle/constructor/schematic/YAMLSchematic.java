package fr.weefle.constructor.schematic;

import fr.weefle.constructor.SchematicBuilder;
import fr.weefle.constructor.hooks.citizens.BuilderTrait;
import fr.weefle.constructor.schematic.blocks.EmptyBuildBlock;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.file.Path;
import java.util.*;

public class YAMLSchematic extends Schematic {
    private final List<SchematicTier> tiers;
    private       int                 tier = -1;

    public YAMLSchematic(Path path) {
        super(path);
        YamlConfiguration config      = YamlConfiguration.loadConfiguration(path.toFile());
        Set<String> keys = config.getKeys(false);
        if (keys.size() == 0) {throw new IllegalArgumentException("Must contain at least one tier");}
        this.tiers = new ArrayList<>(keys.size());
        for (String key : keys) {
            ConfigurationSection section = config.getConfigurationSection(key);
            if (section == null) {return;}
            this.tiers.add(new SchematicTier(new File(SchematicBuilder.getInstance().config().getSchematicsFolder(),
                    Objects.requireNonNull(section.getString(SchematicTier.PATH), "Missing 'path' field")).toPath(), section));
        }
    }

    private YAMLSchematic(Path path, List<SchematicTier> tiers) {
        super(path);
        this.tiers = tiers;
    }

    public List<SchematicTier> getTiers() {return Collections.unmodifiableList(this.tiers);}

    /**
     * @return the index of the highest completed tier, or -1 if none is completed
     */
    public int getTier() {return this.tier;}

    public int getTotalTiers() {return this.tiers.size();}

    public int getNextTier() {return Math.min(this.tier+1, this.getTotalTiers()-1);}

    public int setTier(int tier) {return this.tier = Math.min(tier, this.getTotalTiers());}

    public YAMLSchematic copy() {return new YAMLSchematic(new File(this.path).toPath(), this.tiers);}

    @Override
    @NotNull
    public String getDisplayName() {
        return this.tiers.get(this.getNextTier()).getDisplayName();
    }

    @Override
    public @Nullable Vector getAbsolutePosition() {return this.tiers.get(Math.min(this.tier, this.getTotalTiers()-1)).getAbsolutePosition();}

    @Override
    public int getWidth() {return this.tiers.get(Math.min(this.getNextTier(), this.getTotalTiers()-1)).getWidth();}

    @Override
    public int getHeight() {return this.tiers.get(Math.min(this.getNextTier(), this.getTotalTiers()-1)).getHeight();}

    @Override
    public int getLength() {return this.tiers.get(Math.min(this.getNextTier(), this.getTotalTiers()-1)).getLength();}

    @Override
    @NotNull
    public EmptyBuildBlock getBlockAt(int x, int y, int z) {return this.tiers.get(Math.min(this.getNextTier(), this.getTotalTiers()-1)).getBlockAt(x, y, z);}

    @Override
    public Location offset(Location origin, int x, int y, int z, int emptyLayers, int rotation) {
        return this.tiers.get(Math.min(this.getNextTier(), this.getTotalTiers()-1)).offset(origin, x, y, z, emptyLayers, rotation);
    }

    @Override
    public @NotNull Map<Material, Integer> getMaterials() {
        int tier = this.getNextTier();
        if (tier >= getTotalTiers()) { return new HashMap<>(); }
        return this.tiers.get(tier).getMaterials();
    }

    @Override
    @NotNull
    public Queue<EmptyBuildBlock> buildQueue(BuilderTrait builder) {
        int tier = this.getNextTier();
        if (tier >= getTotalTiers()) { return new LinkedList<>(); }
        return this.tiers.get(tier).buildQueue(builder);
    }
}
