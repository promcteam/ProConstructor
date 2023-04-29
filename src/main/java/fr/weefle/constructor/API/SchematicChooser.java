package fr.weefle.constructor.api;

import fr.weefle.constructor.schematic.BuilderSchematic;

import java.io.File;

public interface SchematicChooser {

    BuilderSchematic setSchematic(File path, String filename) throws Exception;


}
