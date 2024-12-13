package studio.magemonkey.blueprint.commands;

import net.citizensnpcs.api.npc.NPC;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import studio.magemonkey.blueprint.hooks.citizens.BuilderTrait;

import java.util.List;
import java.util.Map;

public class BuildSubCommand extends AbstractCommand {
    public BuildSubCommand(@Nullable SchematicBuilderCommand parent) {
        super("build", "Begin building with the selected options", parent);
        this.permission = "schematicbuilder.build";
        registerHyphenArgument(new HyphenArgument("silent", "true", "false"));
        registerHyphenArgument(new HyphenArgument("onComplete"));
        registerHyphenArgument(new HyphenArgument("onCancel"));
        registerHyphenArgument(new HyphenArgument("onStart"));
        registerHyphenArgument(new HyphenArgument("layers"));
        registerHyphenArgument(new HyphenArgument("groupAll", "true", "false"));
        registerHyphenArgument(new HyphenArgument("ignoreAir", "true", "false"));
        registerHyphenArgument(new HyphenArgument("ignoreLiquid", "true", "false"));
        registerHyphenArgument(new HyphenArgument("excavate", "true", "false"));
        registerHyphenArgument(new HyphenArgument("buildPatternXZ",
                "spiral", "reverse_spiral", "linear", "reverse_linear"));
        registerHyphenArgument(new HyphenArgument("offset", "true", "false"));
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
        if (builder.getState() == BuilderTrait.BuilderState.BUILDING) {
            if (!builder.isSilent()) {
                sender.sendMessage(ChatColor.RED + npc.getName() + " is already building");
            }
            return;
        }

        for (Map.Entry<String, String> entry : getHyphenArguments(args).entrySet()) {
            String arg   = entry.getKey();
            String value = entry.getValue();
            if (arg.equalsIgnoreCase("silent")) {
                builder.setSilent(Boolean.parseBoolean(value));
            } else if (arg.equalsIgnoreCase("onComplete")) {
                builder.setOnComplete(value);
                sender.sendMessage(
                        ChatColor.GREEN + npc.getName() + " will run task " + value + " on build completion");
            } else if (arg.equalsIgnoreCase("onCancel")) {
                builder.setOnCancel(value);
                sender.sendMessage(
                        ChatColor.GREEN + npc.getName() + " will run task " + value + " on build cancellation");
            } else if (arg.equalsIgnoreCase("onStart")) {
                builder.setOnStart(value);
                sender.sendMessage(ChatColor.GREEN + npc.getName() + " will run task " + value + " on build start");
            } else if (arg.equalsIgnoreCase("layers")) {
                int layers;
                try {
                    layers = Integer.parseInt(value);
                    if (layers < 1) {
                        sender.sendMessage(ChatColor.RED + "Number of layers must me positive");
                        return;
                    }
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + arg + " is not a valid number of layers");
                    return;
                }
                builder.setBuildYLayers(layers);
            } else if (arg.equalsIgnoreCase("groupAll")) {
                builder.GroupByLayer = true;
            } else if (arg.equalsIgnoreCase("ignoreAir")) {
                builder.setIgnoreAir(Boolean.parseBoolean(value));
            } else if (arg.equalsIgnoreCase("ignoreLiquid")) {
                builder.setIgnoresLiquids(Boolean.parseBoolean(value));
            } else if (arg.equalsIgnoreCase("excavate")) {
                builder.setExcavate(Boolean.parseBoolean(value));
                if (!builder.isSilent()) {
                    sender.sendMessage(ChatColor.GREEN + npc.getName() + " will excavate first");
                }
            } else if (arg.equalsIgnoreCase("buildPatternXZ")) {
                try {
                    builder.setBuildPatternXZ(BuilderTrait.BuildPatternXZ.valueOf(entry.getValue().toUpperCase()));
                } catch (IllegalArgumentException e) {
                    sender.sendMessage(ChatColor.RED + arg + " is not a valid buildPatternXZ");
                }
            }
        }

        builder.TryBuild(sender);
    }
}
