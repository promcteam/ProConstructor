package fr.weefle.constructor.hooks.citizens.persistence;

import fr.weefle.constructor.hooks.citizens.BuilderTrait;

public class PatternXZPersistenceLoader extends EnumPersistenceLoader<BuilderTrait.BuildPatternXZ> {
    public PatternXZPersistenceLoader() {super(BuilderTrait.BuildPatternXZ.class);}

    @Override
    protected BuilderTrait.BuildPatternXZ getDefault() {return BuilderTrait.BuildPatternXZ.SPIRAL;}

    ;
}
