package com.promcteam.blueprint.hooks.citizens.persistence;

import com.promcteam.blueprint.Blueprint;
import com.promcteam.blueprint.PersistentBuilding;
import net.citizensnpcs.api.persistence.Persister;
import net.citizensnpcs.api.util.DataKey;

import java.util.UUID;

public class PersistentBuildingPersistenceLoader implements Persister<PersistentBuilding> {
    @Override
    public PersistentBuilding create(DataKey dataKey) {
        String uuid = dataKey.getString("", null);
        return uuid == null ?
                null :
                Blueprint.getInstance().getBuildingRegistry().getPersistentBuilding(UUID.fromString(uuid));
    }

    @Override
    public void save(PersistentBuilding building, DataKey dataKey) {} // Manually done in BuilderTrait.save()
}
