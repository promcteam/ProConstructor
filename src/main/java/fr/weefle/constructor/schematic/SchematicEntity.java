package fr.weefle.constructor.schematic;

import fr.weefle.constructor.nms.NMS;
import fr.weefle.constructor.nms.providers.NMSProvider;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Objects;

public class SchematicEntity {
    private final Location location;
    private final Object nbtCompound;

    public SchematicEntity(Object nbtCompound, Vector absolutePosition) {
        this.nbtCompound = nbtCompound;
        removeUnwantedTags(nbtCompound);
        NMSProvider nms = NMS.getInstance().getNMSProvider();
        nms.nbtTagCompound_put(nbtCompound, "id", nms.nbtTagCompound_get(nbtCompound, "Id"));

        List<Object> pos = nms.nbtTagCompound_getList(nbtCompound, "Pos", 6);
        List<Object> rot = nms.nbtTagCompound_getList(nbtCompound, "Rotation", 5);
        int posTagSize = pos.size();
        int rotTagSize = rot.size();
        this.location = new Location(
                null,
                posTagSize > 0 ? nms.nbtTagDouble_getAsDouble(pos.get(0)) : 0,
                posTagSize > 1 ? nms.nbtTagDouble_getAsDouble(pos.get(1)) : 0,
                posTagSize > 2 ? nms.nbtTagDouble_getAsDouble(pos.get(2)) : 0,
                rotTagSize > 0 ? nms.nbtTagFloat_getAsFloat(rot.get(0)) : 0,
                rotTagSize > 1 ? nms.nbtTagFloat_getAsFloat(rot.get(1)) : 0
        );
        this.location.subtract(absolutePosition);
    }

    private void removeUnwantedTags(Object nbtTagCompound) {
        for (String key : new String[]{
                "UUIDLeast",
                "UUIDMost",
                "UUID",
                "WorldUUIDLeast",
                "WorldUUIDMost",
                "PersistentIDMSB",
                "PersistentIDLSB"}) {
            NMS.getInstance().getNMSProvider().nbtTagCompound_remove(nbtCompound, key);
        }
        for (Object passenger : NMS.getInstance().getNMSProvider().nbtTagCompound_getList(nbtTagCompound, "Passengers", 10))  {
            removeUnwantedTags(passenger);
        }
    }

    public Location getLocation() {
        return location.clone();
    }

    public Entity spawn(Location origin, int rotations) {
        Location location = origin.clone().add(this.location.toVector());
        NMSProvider nms = NMS.getInstance().getNMSProvider();
        Object nmsEntity = nms.entityType_loadEntityRecursive(nbtCompound, Objects.requireNonNull(origin.getWorld()), (loadedEntity) -> {
            nms.entity_absMoveTo(loadedEntity, location.getX(), location.getY(), location.getZ(), location.getYaw()+rotations*90, location.getPitch());
            return loadedEntity;
        });
        if (nmsEntity != null) {
            nms.world_addFreshEntityWithPassengers(origin.getWorld(), nmsEntity, CreatureSpawnEvent.SpawnReason.CUSTOM);
            return (Entity) nms.entity_getBukkitEntity(nmsEntity);
        } else {
            return null;
        }
    }
}
