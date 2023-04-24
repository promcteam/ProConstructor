package fr.weefle.constructor.citizens.persistence;

import fr.weefle.constructor.citizens.BuilderTrait;

public class BuilderStatePersistenceLoader extends EnumPersistenceLoader<BuilderTrait.BuilderState> {
    public BuilderStatePersistenceLoader() {super(BuilderTrait.BuilderState.class);}

    @Override
    protected BuilderTrait.BuilderState getDefault() {return BuilderTrait.BuilderState.IDLE;}
}
