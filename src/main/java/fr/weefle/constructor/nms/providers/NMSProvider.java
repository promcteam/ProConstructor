package fr.weefle.constructor.nms.providers;

import org.bukkit.World;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.AbstractList;
import java.util.Set;
import java.util.function.Function;

public abstract class NMSProvider {

    public String[] getNBTCompressedStreamToolsNames() {
        return new String[]{"net.minecraft.nbt.NBTCompressedStreamTools", "net.minecraft.nbt.NbtIo"};
    }

    public String[] getNBTCompressedStreamTools_nbtFromInputStreamMethodNames() {
        return new String[]{"a", "readCompressed"};
    }

    public String[] getNBTTagByteClassNames() {
        return new String[]{"net.minecraft.nbt.NBTTagByte", "net.minecraft.nbt.ByteTag"};
    }

    public String[] getNBTTagByte_valueOfMethodNames() {
        return new String[]{"a", "valueOf"};
    }

    public String[] getNBTTagByteArrayClassNames() {
        return new String[]{"net.minecraft.nbt.NBTTagByteArray", "net.minecraft.nbt.ByteArrayTag"};
    }

    public String[] getNBTTagDoubleClassNames() {
        return new String[]{"net.minecraft.nbt.NBTTagDouble", "net.minecraft.nbt.DoubleTag"};
    }

    public String[] getNBTTagDouble_valueOfMethodNames() {
        return new String[]{"a", "valueOf"};
    }

    public String[] getNBTTagDouble_getAsDoubleMethodNames() {
        return new String[]{"j", "getAsDouble"};
    }

    public String[] getNBTTagFloatClassNames() {
        return new String[]{"net.minecraft.nbt.NBTTagFloat", "net.minecraft.nbt.FloatTag"};
    }

    public String[] getNBTTagFloat_getAsFloatMethodNames() {
        return new String[]{"k", "getAsFloat"};
    }

    public String[] getNBTTagFloat_valueOfMethodNames() {
        return new String[]{"a", "valueOf"};
    }

    public String[] getNBTTagIntClassNames() {
        return new String[]{"net.minecraft.nbt.NBTTagInt", "net.minecraft.nbt.IntTag"};
    }

    public String[] getNBTTagInt_valueOfMethodNames() {
        return new String[]{"a", "valueOf"};
    }

    public String[] getNBTTagIntArrayClassNames() {
        return new String[]{"net.minecraft.nbt.NBTTagIntArray", "net.minecraft.nbt.IntArrayTag"};
    }

    public String[] getNBTTagListClassNames() {
        return new String[]{"net.minecraft.nbt.NBTTagList", "net.minecraft.nbt.ListTag"};
    }

    public String[] getNBTTagLongClassNames() {
        return new String[]{"net.minecraft.nbt.NBTTagLong", "net.minecraft.nbt.LongTag"};
    }

    public String[] getNBTTagLong_valueOfMethodNames() {
        return new String[]{"a", "valueOf"};
    }

    public String[] getNBTTagShortClassNames() {
        return new String[]{"net.minecraft.nbt.NBTTagShort", "net.minecraft.nbt.ShortTag"};
    }

    public String[] getNBTTagShort_valueOfMethodNames() {
        return new String[]{"a", "valueOf"};
    }

    public String[] getNBTTagStringClassNames() {
        return new String[]{"net.minecraft.nbt.NBTTagString", "net.minecraft.nbt.StringTag"};
    }

    public String[] getNBTTagString_valueOfMethodNames() {
        return new String[]{"a", "valueOf"};
    }

    public String[] getNBTTagCompoundClassNames() {
        return new String[]{"net.minecraft.nbt.NBTTagCompound", "net.minecraft.nbt.CompoundTag"};
    }

    public String[] getNBTTagCompound_getIntMethodNames() {
        return new String[]{"h", "getInt"};
    }

    public String[] getNBTTagCompound_getShortMethodNames() {
        return new String[]{"g", "getShort"};
    }

    public String[] getNBTTagCompound_getIntArrayMethodNames() {
        return new String[]{"n", "getIntArray"};
    }

    public String[] getNBTTagCompound_getCompoundMethodNames() {
        return new String[]{"p", "getCompound"};
    }

    public String[] getNBTTagCompound_getMethodNames() {
        return new String[]{"c", "get"};
    }

    public String[] getNBTTagCompound_getByteArrayMethodNames() {
        return new String[]{"m", "getByteArray"};
    }

    public String[] getNBTTagCompound_getAllKeysMethodNames() {
        return new String[]{"d", "getAllKeys"};
    }

    public String[] getNBTTagCompound_removeMethodNames() {
        return new String[]{"r", "remove"};
    }

    public String[] getNBTTagCompound_getListMethodNames() {
        return new String[]{"c", "getList"};
    }

    public String[] getNBTTagCompound_putMethodNames() {
        return new String[]{"a", "put"};
    }

    public String[] getNBTBaseClassNames() {
        return new String[]{"net.minecraft.nbt.NBTBase", "net.minecraft.nbt.Tag"};
    }

    public String[] getNBTTagCompound_putIntMethodNames() {
        return new String[]{"a", "putInt"};
    }

    public String[] getNBTTagCompound_mergeMethodNames() {
        return new String[]{"a", "merge"};
    }

    public String[] getBlockPositionClassNames() {
        return new String[]{"net.minecraft.core.BlockPosition", "net.minecraft.core.BlockPos"};
    }

    public String[] getCraftWorld_getHandleMethodNames() {
        return new String[]{"getHandle"};
    }

    public String[] getWorld_getBlockEntityMethodNames() {
        return new String[]{"c_", "getBlockEntity"};
    }

    public String[] getWorld_addFreshEntityWithPassengersMethodNames() {
        return new String[]{"addFreshEntityWithPassengers"};
    }

    public String[] getBlockEntityClassNames() {
        return new String[]{"net.minecraft.world.level.block.entity.TileEntity", "net.minecraft.world.level.block.entity.BlockEntity"};
    }

    public String[] getBlockEntity_loadMethodNames() {
        return new String[]{"a", "load"};
    }

    public String[] getIBlockDataClassNames() {
        return new String[]{"net.minecraft.world.level.block.state.IBlockData"};
    }

    public String[] getEntityTypeClassNames() {
        return new String[]{"net.minecraft.world.entity.EntityType", "net.minecraft.world.entity.EntityTypes"};
    }

    public String[] getLevelClassNames() {
        return new String[]{"net.minecraft.world.level.World", "net.minecraft.world.level.Level"};
    }

    public String[] getEntityType_loadEntityRecursiveMethodNames() {
        return new String[]{"a", "loadEntityRecursive"};
    }

    public String[] getEntityClassNames() {
        return new String[]{"net.minecraft.world.entity.Entity"};
    }

    public String[] getEntity_absMoveToMethodNames() {
        return new String[]{"a", "absMoveTo"};
    }

    public String[] getEntity_getBukkitEntityMethodNames() {
        return new String[]{"getBukkitEntity"};
    }

    public final Class<?> getClassByNames(String[] names) throws ClassNotFoundException {
        if (names.length == 0) {throw new IllegalArgumentException("At least one class name is required");}
        for (String name : names) {
            try {
                return Class.forName(name);
            } catch (ClassNotFoundException ignored) {}
        }
        throw new ClassNotFoundException(names[0]);
    }

    public final Method getMethodByNames(Class<?> clazz, String[] names, Class<?>... parameterTypes) throws NoSuchMethodException {
        if (names.length == 0) {throw new IllegalArgumentException("At least one method name is required");}
        for (String name : names) {
            try {
                return clazz.getMethod(name, parameterTypes);
            } catch (NoSuchMethodException ignored) {}
        }
        throw new NoSuchMethodException(names[0]);
    }

    private Method NBTCompressedStreamTools_nbtFromInputStreamMethod;

    public final Object loadNBTFromInputStream(InputStream in) {
        if (NBTCompressedStreamTools_nbtFromInputStreamMethod == null) {
            try {
                NBTCompressedStreamTools_nbtFromInputStreamMethod = getMethodByNames(getClassByNames(getNBTCompressedStreamToolsNames()), getNBTCompressedStreamTools_nbtFromInputStreamMethodNames(), InputStream.class);
            } catch (ClassNotFoundException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            return NBTCompressedStreamTools_nbtFromInputStreamMethod.invoke(null, in);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private Method nbtTagByte_valueOfMethod;

    public final Object nbtTagByte_valueOf(byte value) {
        if (nbtTagByte_valueOfMethod == null) {
            try {
                nbtTagByte_valueOfMethod = getMethodByNames(getClassByNames(getNBTTagByteClassNames()), getNBTTagByte_valueOfMethodNames(), byte.class);
            } catch (ClassNotFoundException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            return nbtTagByte_valueOfMethod.invoke(null, value);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private Constructor<?> nbtTagByteArrayConstructor;

    public final Object newNBTTagByteArray(byte[] value) {
        if (nbtTagByteArrayConstructor == null) {
            try {
                nbtTagByteArrayConstructor = getClassByNames(getNBTTagByteArrayClassNames()).getConstructor(byte[].class);
            } catch (ClassNotFoundException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            return nbtTagByteArrayConstructor.newInstance(null, value);
        } catch (InvocationTargetException | IllegalAccessException | InstantiationException e) {
            throw new RuntimeException(e);
        }
    }

    private Method nbtTagDouble_getAsDoubleMethod;

    public final double nbtTagDouble_getAsDouble(Object nbtTagDouble) {
        if (nbtTagDouble_getAsDoubleMethod == null) {
            try {
                nbtTagDouble_getAsDoubleMethod = getMethodByNames(getClassByNames(getNBTTagDoubleClassNames()), getNBTTagDouble_getAsDoubleMethodNames());
            } catch (ClassNotFoundException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            return (double) nbtTagDouble_getAsDoubleMethod.invoke(nbtTagDouble);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private Method nbtTagDouble_valueOfMethod;

    public final Object nbtTagDouble_valueOf(double value) {
        if (nbtTagDouble_valueOfMethod == null) {
            try {
                nbtTagDouble_valueOfMethod = getMethodByNames(getClassByNames(getNBTTagDoubleClassNames()), getNBTTagDouble_valueOfMethodNames(), double.class);
            } catch (ClassNotFoundException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            return nbtTagDouble_valueOfMethod.invoke(null, value);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private Method nbtTagFloat_getAsFloatMethod;

    public final float nbtTagFloat_getAsFloat(Object nbtTagFloat) {
        if (nbtTagFloat_getAsFloatMethod == null) {
            try {
                nbtTagFloat_getAsFloatMethod = getMethodByNames(getClassByNames(getNBTTagFloatClassNames()), getNBTTagFloat_getAsFloatMethodNames());
            } catch (ClassNotFoundException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            return (float) nbtTagFloat_getAsFloatMethod.invoke(nbtTagFloat);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private Method nbtTagFloat_valueOfMethod;

    public final Object nbtTagFloat_valueOf(float value) {
        if (nbtTagFloat_valueOfMethod == null) {
            try {
                nbtTagFloat_valueOfMethod = getMethodByNames(getClassByNames(getNBTTagFloatClassNames()), getNBTTagFloat_valueOfMethodNames(), float.class);
            } catch (ClassNotFoundException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            return nbtTagFloat_valueOfMethod.invoke(null, value);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private Method nbtTagInt_valueOfMethod;

    public final Object nbtTagInt_valueOf(int value) {
        if (nbtTagInt_valueOfMethod == null) {
            try {
                nbtTagInt_valueOfMethod = getMethodByNames(getClassByNames(getNBTTagIntClassNames()), getNBTTagInt_valueOfMethodNames(), int.class);
            } catch (ClassNotFoundException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            return nbtTagInt_valueOfMethod.invoke(null, value);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private Constructor<?> nbtTagIntArrayConstructor;

    public final Object newNBTTagIntArray(int[] value) {
        if (nbtTagIntArrayConstructor == null) {
            try {
                nbtTagIntArrayConstructor = getClassByNames(getNBTTagIntArrayClassNames()).getConstructor(int[].class);
            } catch (ClassNotFoundException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            return nbtTagIntArrayConstructor.newInstance(null, value);
        } catch (InvocationTargetException | IllegalAccessException | InstantiationException e) {
            throw new RuntimeException(e);
        }
    }

    private Constructor<AbstractList<Object>> nbtTagListConstructor;

    public final AbstractList<Object> newNBTTagList() {
        if (nbtTagListConstructor == null) {
            try {
                nbtTagListConstructor = (Constructor<AbstractList<Object>>) getClassByNames(getNBTTagListClassNames()).getConstructor();
            } catch (NoSuchMethodException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            return nbtTagListConstructor.newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private Method nbtTagLong_valueOfMethod;

    public final Object nbtTagLong_valueOf(long value) {
        if (nbtTagLong_valueOfMethod == null) {
            try {
                nbtTagLong_valueOfMethod = getMethodByNames(getClassByNames(getNBTTagLongClassNames()), getNBTTagLong_valueOfMethodNames(), long.class);
            } catch (ClassNotFoundException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            return nbtTagLong_valueOfMethod.invoke(null, value);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private Method nbtTagShort_valueOfMethod;

    public final Object nbtTagShort_valueOf(short value) {
        if (nbtTagShort_valueOfMethod == null) {
            try {
                nbtTagShort_valueOfMethod = getMethodByNames(getClassByNames(getNBTTagShortClassNames()), getNBTTagShort_valueOfMethodNames(), short.class);
            } catch (ClassNotFoundException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            return nbtTagShort_valueOfMethod.invoke(null, value);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private Method nbtTagString_valueOfMethod;

    public final Object nbtTagString_valueOf(String value) {
        if (nbtTagString_valueOfMethod == null) {
            try {
                nbtTagString_valueOfMethod = getMethodByNames(getClassByNames(getNBTTagStringClassNames()), getNBTTagString_valueOfMethodNames(), String.class);
            } catch (ClassNotFoundException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            return nbtTagString_valueOfMethod.invoke(null, value);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private Class<?> nbtTagCompoundClass;

    public final Class<?> getNBTTagCompoundClass() {
        if (nbtTagCompoundClass == null) {
            try {
                nbtTagCompoundClass = getClassByNames(getNBTTagCompoundClassNames());
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return nbtTagCompoundClass;
    }

    private Constructor<?> nbtTagCompoundConstructor;

    public final Object newNBTTagCompound() {
        if (nbtTagCompoundConstructor == null) {
            try {
                nbtTagCompoundConstructor = getNBTTagCompoundClass().getConstructor();
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            return nbtTagCompoundConstructor.newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private Method nbtTagCompound_getIntMethod;

    public final int nbtTagCompound_getInt(Object nbtTagCompound, String key) {
        if (nbtTagCompound_getIntMethod == null) {
            try {
                nbtTagCompound_getIntMethod = getMethodByNames(getNBTTagCompoundClass(), getNBTTagCompound_getIntMethodNames(), String.class);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            return (int) nbtTagCompound_getIntMethod.invoke(nbtTagCompound, key);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private Method nbtTagCompound_getShortMethod;

    public final short nbtTagCompound_getShort(Object nbtTagCompound, String key) {
        if (nbtTagCompound_getShortMethod == null) {
            try {
                nbtTagCompound_getShortMethod = getMethodByNames(getNBTTagCompoundClass(), getNBTTagCompound_getShortMethodNames(), String.class);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            return (short) nbtTagCompound_getShortMethod.invoke(nbtTagCompound, key);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private Method nbtTagCompound_getIntArrayMethod;

    public final int[] nbtTagCompound_getIntArray(Object nbtTagCompound, String key) {
        if (nbtTagCompound_getIntArrayMethod == null) {
            try {
                nbtTagCompound_getIntArrayMethod = getMethodByNames(getNBTTagCompoundClass(), getNBTTagCompound_getIntArrayMethodNames(), String.class);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            return (int[]) nbtTagCompound_getIntArrayMethod.invoke(nbtTagCompound, key);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private Method nbtTagCompound_getCompoundMethod;

    public final Object nbtTagCompound_getCompound(Object nbtTagCompound, String key) {
        if (nbtTagCompound_getCompoundMethod == null) {
            try {
                nbtTagCompound_getCompoundMethod = getMethodByNames(getNBTTagCompoundClass(), getNBTTagCompound_getCompoundMethodNames(), String.class);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            return nbtTagCompound_getCompoundMethod.invoke(nbtTagCompound, key);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private Method nbtTagCompound_getMethod;

    public final Object nbtTagCompound_get(Object nbtTagCompound, String key) {
        if (nbtTagCompound_getMethod == null) {
            try {
                nbtTagCompound_getMethod = getMethodByNames(getNBTTagCompoundClass(), getNBTTagCompound_getMethodNames(), String.class);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            return nbtTagCompound_getMethod.invoke(nbtTagCompound, key);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private Method nbtTagCompound_getByteArrayMethod;

    public final byte[] nbtTagCompound_getByteArray(Object nbtTagCompound, String key) {
        if (nbtTagCompound_getByteArrayMethod == null) {
            try {
                nbtTagCompound_getByteArrayMethod = getMethodByNames(getNBTTagCompoundClass(), getNBTTagCompound_getByteArrayMethodNames(), String.class);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            return (byte[]) nbtTagCompound_getByteArrayMethod.invoke(nbtTagCompound, key);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private Method nbtTagCompound_getAllKeysMethod;

    public final Set<String> nbtTagCompound_getAllKeys(Object nbtTagCompound) {
        if (nbtTagCompound_getAllKeysMethod == null) {
            try {
                nbtTagCompound_getAllKeysMethod = getMethodByNames(getNBTTagCompoundClass(), getNBTTagCompound_getAllKeysMethodNames());
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            return (Set<String>) nbtTagCompound_getAllKeysMethod.invoke(nbtTagCompound);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private Method nbtTagCompound_removeMethod;

    public final void nbtTagCompound_remove(Object nbtTagCompound, String key) {
        if (nbtTagCompound_removeMethod == null) {
            try {
                nbtTagCompound_removeMethod = getMethodByNames(getNBTTagCompoundClass(), getNBTTagCompound_removeMethodNames(), String.class);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            nbtTagCompound_removeMethod.invoke(nbtTagCompound, key);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private Method nbtTagCompound_getListMethod;

    public final AbstractList<Object> nbtTagCompound_getList(Object nbtTagCompound, String name, int type) {
        if (nbtTagCompound_getListMethod == null) {
            try {
                nbtTagCompound_getListMethod = getMethodByNames(getNBTTagCompoundClass(), getNBTTagCompound_getListMethodNames(), String.class, int.class);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            return (AbstractList<Object>) nbtTagCompound_getListMethod.invoke(nbtTagCompound, name, type);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private Method nbtTagCompound_putMethod;

    public final Object nbtTagCompound_put(Object nbtTagCompound, String name, Object nbtBase) {
        if (nbtTagCompound_putMethod == null) {
            try {
                nbtTagCompound_putMethod = getMethodByNames(getNBTTagCompoundClass(), getNBTTagCompound_putMethodNames(), String.class, getClassByNames(getNBTBaseClassNames()));
            } catch (NoSuchMethodException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            return nbtTagCompound_putMethod.invoke(nbtTagCompound, name, nbtBase);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private Method nbtTagCompound_putIntMethod;

    public final void nbtTagCompound_putInt(Object nbtTagCompound, String name, int value) {
        if (nbtTagCompound_putIntMethod == null) {
            try {
                nbtTagCompound_putIntMethod = getMethodByNames(getNBTTagCompoundClass(), getNBTTagCompound_putIntMethodNames(), String.class, int.class);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            nbtTagCompound_putIntMethod.invoke(nbtTagCompound, name, value);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private Method nbtTagCompound_mergeMethod;

    public final Object nbtTagCompound_merge(Object nbtTagCompound, Object toMerge) {
        if (nbtTagCompound_mergeMethod == null) {
            try {
                nbtTagCompound_mergeMethod = getMethodByNames(getNBTTagCompoundClass(), getNBTTagCompound_mergeMethodNames(), getNBTTagCompoundClass());
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            return nbtTagCompound_mergeMethod.invoke(nbtTagCompound, toMerge);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private Constructor<?> blockPositionConstructor;

    public final Object newBlockPosition(int x, int y, int z) {
        if (blockPositionConstructor == null) {
            try {
                blockPositionConstructor = getClassByNames(getBlockPositionClassNames()).getConstructor(int.class, int.class, int.class);
            } catch (NoSuchMethodException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            return blockPositionConstructor.newInstance(x, y, z);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private Method craftWorld_getHandleMethod;

    private Object world_getHandle(World world) {
        if (craftWorld_getHandleMethod == null) {
            try {
                craftWorld_getHandleMethod = getMethodByNames(world.getClass(), getCraftWorld_getHandleMethodNames());
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            return craftWorld_getHandleMethod.invoke(world);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private Method world_getBlockEntityMethod;

    public final Object world_getTileEntity(World world, Object blockPosition) {
        Object worldHandle = world_getHandle(world);
        if (world_getBlockEntityMethod == null) {
            try {
                world_getBlockEntityMethod = getMethodByNames(worldHandle.getClass(), getWorld_getBlockEntityMethodNames(), getClassByNames(getBlockPositionClassNames()));
            } catch (NoSuchMethodException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            return world_getBlockEntityMethod.invoke(worldHandle, blockPosition);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private Method world_addFreshEntityWithPassengersMethod;

    public final void world_addFreshEntityWithPassengers(World world, Object entity, CreatureSpawnEvent.SpawnReason spawnReason) {
        Object worldHandle = world_getHandle(world);
        if (world_addFreshEntityWithPassengersMethod == null) {
            try {
                world_addFreshEntityWithPassengersMethod = getMethodByNames(worldHandle.getClass(), getWorld_addFreshEntityWithPassengersMethodNames(), getClassByNames(getEntityClassNames()), CreatureSpawnEvent.SpawnReason.class);
            } catch (NoSuchMethodException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            world_addFreshEntityWithPassengersMethod.invoke(worldHandle, entity, spawnReason);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private Method  blockEntity_loadMethod;
    private boolean blockEntity_loadMethod_old;

    public final void blockEntity_load(Object blockEntity, Object nbtTagCompound) {
        if (blockEntity_loadMethod == null) {
            try {
                blockEntity_loadMethod = getMethodByNames(getClassByNames(getBlockEntityClassNames()), getBlockEntity_loadMethodNames(), getNBTTagCompoundClass());
                blockEntity_loadMethod_old = false;
            } catch (NoSuchMethodException | ClassNotFoundException e) {
                try {
                    blockEntity_loadMethod = getMethodByNames(getClassByNames(getBlockEntityClassNames()), getBlockEntity_loadMethodNames(), getClassByNames(getIBlockDataClassNames()), getNBTTagCompoundClass());
                    blockEntity_loadMethod_old = true;
                } catch (NoSuchMethodException | ClassNotFoundException e1) {
                    throw new RuntimeException(e1);
                }
            }
        }
        try {
            if (blockEntity_loadMethod_old) {
                blockEntity_loadMethod.invoke(blockEntity, null, nbtTagCompound);
            } else {
                blockEntity_loadMethod.invoke(blockEntity, nbtTagCompound);
            }
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private Class<?> entityTypeClass;

    public final Class<?> getEntityTypeClass() {
        if (entityTypeClass == null) {
            try {
                entityTypeClass = getClassByNames(getEntityTypeClassNames());
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return entityTypeClass;
    }

    private Class<?> levelClass;

    public final Class<?> getLevelClass() {
        if (levelClass == null) {
            try {
                levelClass = getClassByNames(getLevelClassNames());
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return levelClass;
    }

    private Method entityType_loadEntityRecursiveMethod;

    public final Object entityType_loadEntityRecursive(Object nbtTagCompound, World world, Function<Object, Object> function) {
        if (entityType_loadEntityRecursiveMethod == null) {
            try {
                entityType_loadEntityRecursiveMethod = getMethodByNames(getEntityTypeClass(), getEntityType_loadEntityRecursiveMethodNames(), getNBTTagCompoundClass(), getClassByNames(getLevelClassNames()), Function.class);
            } catch (NoSuchMethodException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            return entityType_loadEntityRecursiveMethod.invoke(null, nbtTagCompound, world_getHandle(world), function);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private Class<?> entityClass;

    public final Class<?> getEntityClass() {
        if (entityClass == null) {
            try {
                entityClass = getClassByNames(getEntityClassNames());
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return entityClass;
    }

    private Method entity_absMoveToMethod;

    public final void entity_absMoveTo(Object entity, double x, double y, double z, float yaw, float pitch) {
        if (entity_absMoveToMethod == null) {
            try {
                entity_absMoveToMethod = getMethodByNames(getEntityClass(), getEntity_absMoveToMethodNames(), double.class, double.class, double.class, float.class, float.class);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            entity_absMoveToMethod.invoke(entity, x, y, z, yaw, pitch);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private Method entity_getBukkitEntityMethod;

    public final Object entity_getBukkitEntity(Object entity) {
        if (entity_getBukkitEntityMethod == null) {
            try {
                entity_getBukkitEntityMethod = getMethodByNames(getEntityClass(), getEntity_getBukkitEntityMethodNames());
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            return entity_getBukkitEntityMethod.invoke(entity);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
