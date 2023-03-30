package fr.weefle.constructor.NMS;

import fr.weefle.constructor.essentials.ConstructorSchematic;

import java.io.File;

public class SchematicChooser implements fr.weefle.constructor.API.SchematicChooser {

    public ConstructorSchematic setSchematic(File path, String filename) throws Exception {


        MCEditSchematicFormat format = new MCEditSchematicFormat();
        //entitieslist = format.getEntitieslist();
        return format.load(path, filename);

    }

}
