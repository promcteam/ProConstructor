package fr.weefle.constructor.commands;

import fr.weefle.constructor.SchematicBuilder;
import fr.weefle.constructor.citizens.BuilderTrait;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class SurveySubCommand extends AbstractCommand {
    public SurveySubCommand(@Nullable SchematicBuilderCommand parent) {
        super("survey", "View the list of materials required to build the loaded schematic at the current origin with the specified options", parent);
        this.permission = "schematicbuilder.survey";
        addAllowedSender(Player.class);
        registerHyphenArgument(new HyphenArgument("excavate", "true", "false"));
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull List<String> args) {
        BuilderTrait builder = getSelectedBuilder(sender);
        if (builder == null) {return;}
        NPC     npc = builder.getNPC();
        boolean ex  = builder.isExcavate();

        for (Map.Entry<String, String> entry : getHyphenArguments(args).entrySet()) {
            String key = entry.getKey();
            if (key.equalsIgnoreCase("excavate")) {
                ex = Boolean.parseBoolean(entry.getValue());
            }
        }
        if (builder.getSchematic() == null) {
            sender.sendMessage(ChatColor.RED + "No Schematic Loaded!");
        } else {
            sender.sendMessage(SchematicBuilder.format(SchematicBuilder.getInstance().config().getSurveyMessage() + (ex
                    ? " (excavate)"
                    : ""), npc, builder.getSchematic(), sender, null, "0"));
            sender.sendMessage(builder.GetMatsList(ex));   // Talk to the player.
        }
    }
}
