package fr.weefle.constructor.API;

import fr.weefle.constructor.NMS.NMS;
import fr.weefle.constructor.citizens.BuilderTrait;
import fr.weefle.constructor.util.Structure;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.io.File;

public class SchematicBuilderAPI {

    public static boolean npcBuild(int npcId, Location location, Float speed, boolean ignoreAir, boolean ignoreLiquid, boolean excavate, BuilderTrait.BuildPatternXZ buildPattern, String schematic, Player player) throws Exception {

        NPCRegistry registry = CitizensAPI.getNPCRegistry();
        NPC         npc      = registry.getById(npcId);
        npc.addTrait(BuilderTrait.class);
        npc.teleport(location, null);
        if (speed != null) {
            npc.getNavigator().getDefaultParameters().baseSpeed(speed);
        }
        BuilderTrait bt = npc.getTrait(BuilderTrait.class);
        bt.setIgnoreAir(ignoreAir);
        bt.setIgnoreAir(ignoreLiquid);
        bt.setExcavate(excavate);
        bt.setBuildPatternXZ(buildPattern);
        File file = new File("plugins/ProSchematicBuilder/schematics/");
        try {
            bt.setSchematic(new Structure(file, schematic.trim().replace("\"", "")).load(file, schematic.trim().replace("\"", "")));
        } catch (Exception exception) {
            try {
                bt.setSchematic(new Structure(file, schematic.trim().replace("\"", "")).load(file, schematic.trim().replace("\"", "")));
                bt.setSchematic(NMS.getInstance().getChooser().setSchematic(file, schematic.trim().replace("\"", "")));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return bt.TryBuild(player);

    }

}
