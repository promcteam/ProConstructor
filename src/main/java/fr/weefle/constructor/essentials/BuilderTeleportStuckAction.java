package fr.weefle.constructor.essentials;

import org.bukkit.Location;

import net.citizensnpcs.api.ai.Navigator;
import net.citizensnpcs.api.ai.StuckAction;
import net.citizensnpcs.api.npc.NPC;

public class BuilderTeleportStuckAction implements StuckAction{

	@Override
	public boolean run(NPC npc, Navigator navigator) {
		if (!npc.isSpawned())
			return false;
		Location base = navigator.getTargetAsLocation();
		npc.getEntity().teleport(base);
		//npc.faceLocation(base);
		/*if (base.getWorld() == npc.getEntity().getLocation().getWorld()){
			if (npc.getEntity().getLocation().distanceSquared(base) <= 4)
				return true;
		}*/
		//npc.getNavigator().setTarget(base);
		//npc.getNavigator().setTarget(null, true);
		//npc.getNavigator().getLocalParameters().useNewPathfinder();
		//npc.getNavigator().setPaused(false);
		//npc.getNavigator().getPathStrategy().clearCancelReason();
		//npc.getNavigator().getPathStrategy().update();
		return false;
	}

	public static BuilderTeleportStuckAction INSTANCE = new BuilderTeleportStuckAction();

}