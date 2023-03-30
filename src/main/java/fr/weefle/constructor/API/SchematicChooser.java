package fr.weefle.constructor.API;

import java.io.File;
import java.io.IOException;

import fr.weefle.constructor.essentials.ConstructorSchematic;

public interface SchematicChooser {

	ConstructorSchematic setSchematic(File path, String filename) throws Exception;
	
	

}
