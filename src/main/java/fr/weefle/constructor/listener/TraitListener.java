package fr.weefle.constructor.listener;

import fr.weefle.constructor.SchematicBuilder;
import fr.weefle.constructor.commands.SchematicBuilderCommand;
import fr.weefle.constructor.hooks.citizens.BuilderTrait;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.trait.TraitInfo;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;

public class TraitListener implements Listener {
    @EventHandler
    public void onLoad(ServerLoadEvent e) {
        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(BuilderTrait.class).withName("builder"));
        Bukkit.getServer().getPluginManager().registerEvents(new BuilderListener(), SchematicBuilder.getInstance());
        SchematicBuilder.getInstance().getCommand("schematicbuilder").setExecutor(new SchematicBuilderCommand());
        HandlerList.unregisterAll(this);
    }
}
