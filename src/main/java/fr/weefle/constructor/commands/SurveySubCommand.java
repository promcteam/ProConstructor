package fr.weefle.constructor.commands;

import fr.weefle.constructor.SchematicBuilder;
import fr.weefle.constructor.essentials.BuilderTrait;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SurveySubCommand extends AbstractCommand {
    public SurveySubCommand(@Nullable SchematicBuilderCommand parent) {
        super("survey", parent);
        this.permission = "schematicbuilder.survey";
        addAllowedSender(Player.class);
    }

    @Override
    public List<String> getArguments(CommandSender sender) {
        List<String> list = new ArrayList<>();
        list.add("excavate");
        return list;
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull List<String> args) {
        BuilderTrait builder = getSelectedBuilder(sender);
        if (builder == null) {return;}
        NPC npc = builder.getNPC();
        boolean ex = false;

        for (String arg : args) {
            if (arg.equalsIgnoreCase("excavate")) {
                ex = true;
                break;
            }
        }
        if (builder.schematic == null) {
            sender.sendMessage(ChatColor.RED + "No Schematic Loaded!");
        } else {
            sender.sendMessage(SchematicBuilder.getInstance().format(SchematicBuilder.SurveyMessage + (ex ? " (excavate)" : ""), npc, builder.schematic, sender, null, "0"));
            sender.sendMessage(builder.GetMatsList(ex));   // Talk to the player.
        }
    }
}
