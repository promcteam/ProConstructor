package fr.weefle.constructor.API;

import fr.weefle.constructor.essentials.BuilderSchematic;

import java.io.File;

public interface SchematicChooser {

    BuilderSchematic setSchematic(File path, String filename) throws Exception;


}
