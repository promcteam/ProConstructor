package fr.weefle.constructor.hooks.citizens.persistence;

import fr.weefle.constructor.SchematicBuilder;
import fr.weefle.constructor.schematic.Schematic;
import net.citizensnpcs.api.persistence.Persister;
import net.citizensnpcs.api.util.DataKey;

import java.util.logging.Level;

public class SchematicPersistenceLoader implements Persister<Schematic> {

    @Override
    public Schematic create(DataKey dataKey) {
        String name = dataKey.getString("", null);
        if (name == null) {return null;}
        try {
            return SchematicBuilder.getSchematic(name);
        } catch (Exception e) {
            SchematicBuilder.getInstance().getLogger().log(Level.WARNING, "Failed to load schematic: " + name);
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void save(Schematic schematic, DataKey dataKey) {} // Manually done in BuilderTrait.save()
}
