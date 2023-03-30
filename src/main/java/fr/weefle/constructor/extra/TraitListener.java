package fr.weefle.constructor.extra;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;

import fr.weefle.constructor.Constructor;
import fr.weefle.constructor.essentials.ConstructorListener;
import fr.weefle.constructor.essentials.ConstructorTrait;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.trait.TraitInfo;

public class TraitListener implements Listener {
	
	@EventHandler
	public void onLoad(ServerLoadEvent e) {

		CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(ConstructorTrait.class).withName("constructor"));
	Constructor.instance.getServer().getPluginManager().registerEvents(new ConstructorListener(Constructor.instance), Constructor.instance);
		Constructor.getInstance().getCommand("constructor").setExecutor(new ConstructorCommand());


	}

}
