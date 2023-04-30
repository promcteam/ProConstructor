package fr.weefle.constructor.hooks.citizens.persistence;

import net.citizensnpcs.api.persistence.Persister;
import net.citizensnpcs.api.util.DataKey;
import org.bukkit.Material;
import org.bukkit.configuration.MemorySection;

import java.util.Map;
import java.util.TreeMap;

public class MaterialIntegerMapPersistenceLoader implements Persister<MaterialMapWrapper> {

    @Override
    public MaterialMapWrapper create(DataKey dataKey) {
        Map<Material, Integer> map       = new TreeMap<>();
        MaterialMapWrapper     wrapper   = new MaterialMapWrapper(map);
        MemorySection          materials = (MemorySection) dataKey.getRaw("");
        if (materials == null) {return wrapper;}
        for (String key : materials.getKeys(false)) {
            Material material;
            try {
                material = Material.valueOf(key.toUpperCase());
            } catch (IllegalArgumentException e) {continue;}
            int amount = map.getOrDefault(material, 0) + materials.getInt(key, 0);
            if (amount > 0) {map.put(material, amount);}
        }
        return wrapper;
    }

    @Override
    public void save(MaterialMapWrapper map, DataKey dataKey) {
        Map<Material, Integer> handle = map.getHandle();
        if (handle.isEmpty()) {
            dataKey.removeKey("");
        } else {
            for (Map.Entry<Material, Integer> entry : handle.entrySet()) {
                int amount = entry.getValue();
                if (amount > 0) {dataKey.setInt(entry.getKey().name(), amount);}
            }
        }
    }
}
