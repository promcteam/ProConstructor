package fr.weefle.constructor.NMS.Version_1_14_R1;

import java.io.File;
import java.io.IOException;

import fr.weefle.constructor.API.SchematicChooser;
import fr.weefle.constructor.essentials.ConstructorSchematic;

public class SchematicChooser_1_14_R1 implements SchematicChooser{
	
public ConstructorSchematic setSchematic(File path, String filename) throws Exception {
		
		
		MCEditSchematicFormat_1_14_R1 format = new MCEditSchematicFormat_1_14_R1();
		//entitieslist = format.getEntitieslist();
		return format.load(path, filename);
		
	}

}
