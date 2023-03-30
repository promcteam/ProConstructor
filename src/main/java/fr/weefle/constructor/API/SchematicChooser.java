package fr.weefle.constructor.API;

import java.io.File;

import fr.weefle.constructor.essentials.BuilderSchematic;

public interface SchematicChooser {

	BuilderSchematic setSchematic(File path, String filename) throws Exception;
	
	

}
