package com.promcteam.blueprint;

import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;

public class Config {
    private final String  schematicsFolder;
    private final double  moveTimeoutTicks;
    private final boolean ignoreProtection, holdItems, requireMaterials;

    private final String
            startedMessage,
            completeMessage,
            cancelMessage,
            surveyMessage,
            supplyListMessage,
            supplyNeedMessage,
            supplyDontNeedMessage,
            supplyTakenMessage,
            collectingMessage,
            cantWhileBuilding,
            noSchematicSelected,
            cantMoveSchematic;

    Config() {
        Blueprint plugin = Blueprint.getInstance();
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        FileConfiguration config = plugin.getConfig();
        schematicsFolder = config.getString("SchematicsFolder",
                plugin.getDataFolder() + File.separator + "schematics" + File.separator);
        moveTimeoutTicks = config.getDouble("DefaultOptions.MoveTimeoutSeconds", 1);
        ignoreProtection = config.getBoolean("DefaultOptions.IgnoreProtection");
        holdItems = config.getBoolean("DefaultOptions.HoldItems");
        requireMaterials = config.getBoolean("DefaultOptions.RequireMaterials");

        completeMessage = config.getString("DefaultTexts.BuildComplete", "");
        cancelMessage = config.getString("DefaultTexts.BuildCanceled", "");
        startedMessage = config.getString("DefaultTexts.BuildStarted", "");
        collectingMessage = config.getString("DefaultTexts.BuildCollecting", "");
        surveyMessage = config.getString("DefaultTexts.Survey", "");
        supplyListMessage = config.getString("DefaultTexts.Supply_List", "");
        supplyNeedMessage = config.getString("DefaultTexts.Supply_Need_Item", "");
        supplyDontNeedMessage = config.getString("DefaultTexts.Supply_Dont_Need_Item", "");
        supplyTakenMessage = config.getString("DefaultTexts.Supply_Item_Taken", "");
        cantWhileBuilding = config.getString("DefaultTexts.CantWhileBuilding", "");
        noSchematicSelected = config.getString("DefaultTexts.NoSchematicSelected", "");
        cantMoveSchematic = config.getString("DefaultTexts.CantMoveSchematic", "");
    }

    public String getSchematicsFolder() {return schematicsFolder;}

    public double getMoveTimeoutTicks() {return moveTimeoutTicks;}

    public boolean isIgnoreProtection() {return ignoreProtection;}

    public boolean isHoldItems() {return holdItems;}

    public boolean isRequireMaterials() {return requireMaterials;}

    public String getStartedMessage() {return startedMessage;}

    public String getCompleteMessage() {return completeMessage;}

    public String getCancelMessage() {return cancelMessage;}

    public String getSurveyMessage() {return surveyMessage;}

    public String getSupplyListMessage() {return supplyListMessage;}

    public String getSupplyNeedMessage() {return supplyNeedMessage;}

    public String getSupplyDontNeedMessage() {return supplyDontNeedMessage;}

    public String getSupplyTakenMessage() {return supplyTakenMessage;}

    public String getCollectingMessage() {return collectingMessage;}

    public String getCantWhileBuildingMessage() {return cantWhileBuilding;}

    public String getNoSchematicSelectedMessage() {return noSchematicSelected;}

    public String getCantMoveSchematicMessage() {return cantMoveSchematic;}
}
