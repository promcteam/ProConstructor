package com.promcteam.blueprint.listener;

import com.promcteam.blueprint.Blueprint;
import com.promcteam.blueprint.commands.SchematicBuilderCommand;
import com.promcteam.blueprint.hooks.citizens.BuilderTrait;
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
        Bukkit.getServer().getPluginManager().registerEvents(new BuilderListener(), Blueprint.getInstance());
        Blueprint.getInstance().getCommand("schematicbuilder").setExecutor(new SchematicBuilderCommand());
        HandlerList.unregisterAll(this);
    }
}
