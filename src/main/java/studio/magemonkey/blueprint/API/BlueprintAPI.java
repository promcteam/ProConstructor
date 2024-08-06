package studio.magemonkey.blueprint.API;

import studio.magemonkey.blueprint.Blueprint;
import studio.magemonkey.blueprint.hooks.citizens.BuilderTrait;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class BlueprintAPI {

    public static boolean npcBuild(int npcId,
                                   Location location,
                                   Float speed,
                                   boolean ignoreAir,
                                   boolean ignoreLiquid,
                                   boolean excavate,
                                   BuilderTrait.BuildPatternXZ buildPattern,
                                   String schematic,
                                   Player player) throws Exception {

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
        bt.setSchematic(Blueprint.getSchematic(schematic.trim()));
        return bt.TryBuild(player);

    }

}
