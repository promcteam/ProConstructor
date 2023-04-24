package fr.weefle.constructor.citizens.persistence;

import net.citizensnpcs.api.persistence.Persister;
import net.citizensnpcs.api.util.DataKey;

public abstract class EnumPersistenceLoader<E extends Enum<E>> implements Persister<Enum<E>> {
    private final Class<E> enumClass;

    public EnumPersistenceLoader(Class<E> enumClass) {this.enumClass = enumClass;}

    @Override
    public Enum<E> create(DataKey dataKey) {
        return Enum.valueOf(enumClass, dataKey.getString(""));
    }

    @Override
    public void save(Enum<E> eEnum, DataKey dataKey) {
        dataKey.setString("", eEnum.name());
    }

    protected E getDefault() {return null;};
}
