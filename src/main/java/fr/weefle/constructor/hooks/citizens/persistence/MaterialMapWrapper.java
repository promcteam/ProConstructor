package fr.weefle.constructor.hooks.citizens.persistence;

import org.bukkit.Material;

import java.util.Map;

public class MaterialMapWrapper { // Citizens' persistence doesn't seem to support parameterized types
    private final Map<Material, Integer> handle;

    public MaterialMapWrapper(Map<Material, Integer> handle) {this.handle = handle;}

    public Map<Material, Integer> getHandle() {return handle;}
}
