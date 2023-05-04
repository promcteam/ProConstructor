package fr.weefle.constructor;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Config {
    private       String         schematicsFolder;
    private final List<Material> markMats         = new ArrayList<>();
    private       double         moveTimeoutTicks;
    private       boolean        ignoreProtection, holdItems, requireMaterials;

    private String
            startedMessage,
            completeMessage,
            cancelMessage,
            markMessage,
            surveyMessage,
            supplyListMessage,
            supplyNeedMessage,
            supplyDontNeedMessage,
            supplyTakenMessage,
            collectingMessage;

    Config() {reload();}

    public void reload() {
        SchematicBuilder plugin = SchematicBuilder.getInstance();
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        FileConfiguration config = plugin.getConfig();
        schematicsFolder = config.getString("SchematicsFolder", plugin.getDataFolder() + File.separator + "schematics" + File.separator);
        moveTimeoutTicks = config.getDouble("DefaultOptions.MoveTimeoutSeconds", 1);
        ignoreProtection = config.getBoolean("DefaultOptions.IgnoreProtection");
        holdItems = config.getBoolean("DefaultOptions.HoldItems");
        requireMaterials = config.getBoolean("DefaultOptions.RequireMaterials");

        completeMessage = config.getString("DefaultTexts.BuildComplete", "");
        cancelMessage = config.getString("DefaultTexts.BuildCanceled", "");
        startedMessage = config.getString("DefaultTexts.BuildStarted", "");
        collectingMessage = config.getString("DefaultTexts.BuildCollecting", "");
        markMessage = config.getString("DefaultTexts.Mark", "");
        surveyMessage = config.getString("DefaultTexts.Survey", "");
        supplyListMessage = config.getString("DefaultTexts.Supply_List", "");
        supplyNeedMessage = config.getString("DefaultTexts.Supply_Need_Item", "");
        supplyDontNeedMessage = config.getString("DefaultTexts.Supply_Dont_Need_Item", "");
        supplyTakenMessage = config.getString("DefaultTexts.Supply_Item_Taken", "");
        for (String material : config.getStringList("MarkMaterials")) {
            markMats.add(Material.valueOf(material));
        }
        if (markMats.isEmpty()) markMats.add(Material.GLASS);

    }

    public String getSchematicsFolder() {return schematicsFolder;}

    public List<Material> getMarkMats() {return markMats;}

    public double getMoveTimeoutTicks() {return moveTimeoutTicks;}

    public boolean isIgnoreProtection() {return ignoreProtection;}

    public boolean isHoldItems() {return holdItems;}

    public boolean isRequireMaterials() {return requireMaterials;}

    public String getStartedMessage() {return startedMessage;}

    public String getCompleteMessage() {return completeMessage;}

    public String getCancelMessage() {return cancelMessage;}

    public String getMarkMessage() {return markMessage;}

    public String getSurveyMessage() {return surveyMessage;}

    public String getSupplyListMessage() {return supplyListMessage;}

    public String getSupplyNeedMessage() {return supplyNeedMessage;}

    public String getSupplyDontNeedMessage() {return supplyDontNeedMessage;}

    public String getSupplyTakenMessage() {return supplyTakenMessage;}

    public String getCollectingMessage() {return collectingMessage;}
}
