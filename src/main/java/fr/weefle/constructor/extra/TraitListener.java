package fr.weefle.constructor.extra;

import fr.weefle.constructor.SchematicBuilder;
import fr.weefle.constructor.essentials.BuilderListener;
import fr.weefle.constructor.essentials.BuilderTrait;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.trait.TraitInfo;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;

public class TraitListener implements Listener {

    @EventHandler
    public void onLoad(ServerLoadEvent e) {

        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(BuilderTrait.class).withName("builder"));
        SchematicBuilder.instance.getServer().getPluginManager().registerEvents(new BuilderListener(SchematicBuilder.instance), SchematicBuilder.instance);
        SchematicBuilder.getInstance().getCommand("schematicbuilder").setExecutor(new SchematicBuilderCommand());


    }

}
