package fr.weefle.constructor.hooks.citizens.persistence;

import fr.weefle.constructor.PersistentBuilding;
import fr.weefle.constructor.SchematicBuilder;
import net.citizensnpcs.api.persistence.Persister;
import net.citizensnpcs.api.util.DataKey;

import java.util.UUID;

public class PersistentBuildingPersistenceLoader implements Persister<PersistentBuilding> {
    @Override
    public PersistentBuilding create(DataKey dataKey) {
        String uuid = dataKey.getString("", null);
        return uuid == null ?
                null :
                SchematicBuilder.getInstance().getBuildingRegistry().getPersistentBuilding(UUID.fromString(uuid));
    }

    @Override
    public void save(PersistentBuilding building, DataKey dataKey) {
        dataKey.setString("",  building == null ? null :building.getUUID().toString());
    }
}
