package fr.weefle.constructor.citizens.persistence;

import net.citizensnpcs.api.persistence.Persister;
import net.citizensnpcs.api.util.DataKey;
import org.bukkit.Material;
import org.bukkit.configuration.MemorySection;

import java.util.HashMap;
import java.util.Map;

public class MaterialIntegerPersistenceLoader implements Persister<Map<Material, Integer>> {

    @Override
    public Map<Material, Integer> create(DataKey dataKey) {
        Map<Material, Integer> map       = new HashMap<>();
        MemorySection          materials = (MemorySection) dataKey.getRaw("");
        if (materials == null) {return map;}
        for (String key : materials.getKeys(false)) {
            Material material;
            try {
                material = Material.valueOf(key.toUpperCase());
            } catch (IllegalArgumentException e) {continue;}
            map.put(material, map.getOrDefault(material, 0) + materials.getInt(key, 0));
        }
        return map;
    }

    @Override
    public void save(Map<Material, Integer> map, DataKey dataKey) {
        if (map.isEmpty()) {
            dataKey.removeKey("");
        } else {
            for (Map.Entry<Material, Integer> entry : map.entrySet()) {
                dataKey.setInt(entry.getKey().name(), entry.getValue());
            }
        }
    }
}
