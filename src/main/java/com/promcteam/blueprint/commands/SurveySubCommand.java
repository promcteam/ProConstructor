package com.promcteam.blueprint.commands;

import com.promcteam.blueprint.Blueprint;
import com.promcteam.blueprint.hooks.citizens.BuilderTrait;
import com.promcteam.blueprint.util.Util;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SurveySubCommand extends AbstractCommand {
    public SurveySubCommand(@Nullable SchematicBuilderCommand parent) {
        super("survey",
                "View the list of materials required to build the loaded schematic at the current origin with the specified options",
                parent);
        this.permission = "schematicbuilder.survey";
        addAllowedSender(Player.class);
        registerHyphenArgument(new HyphenArgument("excavate", "true", "false"));
    }

    @Override
    public void execute(@NotNull CommandSender sender,
                        @NotNull Command command,
                        @NotNull String label,
                        @NotNull List<String> args) {
        BuilderTrait builder = getSelectedBuilder(sender);
        if (builder == null) {
            return;
        }
        NPC npc = builder.getNPC();
        if (builder.getState() != BuilderTrait.BuilderState.COLLECTING) {
            sender.sendMessage(npc.getName() + ChatColor.GREEN + " is not collecting any materials");
            return;
        }
        sender.sendMessage(Blueprint.format(Blueprint.getInstance().config().getSurveyMessage(),
                npc,
                builder.getSchematic(),
                sender,
                null,
                "0"));
        sender.sendMessage(Util.printMaterials(builder.getMissingMaterials()));
    }
}
