package fr.weefle.constructor.NMS;

import fr.weefle.constructor.schematic.BuilderSchematic;

import java.io.File;

public class SchematicChooser implements fr.weefle.constructor.api.SchematicChooser {

    public BuilderSchematic setSchematic(File path, String filename) throws Exception {


        MCEditSchematicFormat format = new MCEditSchematicFormat();
        //entitieslist = format.getEntitieslist();
        return format.load(path, filename);

    }

}
