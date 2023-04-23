package fr.weefle.constructor.commands;

import fr.weefle.constructor.essentials.BuilderTrait;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class BuildSubCommand extends AbstractCommand {
    public BuildSubCommand(@Nullable SchematicBuilderCommand parent) {
        super("build", parent);
        this.permission = "schematicbuilder.build";
    }

    @Override
    public List<String> getArguments(CommandSender sender) {
        List<String> list = new ArrayList<>();
        list.add("-silent");
        list.add("-onComplete=");
        list.add("-onCancel=");
        list.add("-onStart=");
        list.add("-layers=");
        list.add("-yOffset=");
        list.add("-groupAll");
        list.add("-ignoreAir");
        list.add("-ignoreLiquid");
        list.add("-excavate");
        list.add("-spiral");
        list.add("-reverseSpiral");
        list.add("-linear");
        list.add("-reverseLinear");
        list.add("-offset");
        return list;
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull List<String> args) {
        BuilderTrait builder = getSelectedBuilder(sender);
        if (builder == null) {return;}
        NPC npc = builder.getNPC();
        if (builder.State == BuilderTrait.BuilderState.building) {
            if (!builder.Silent) {
                sender.sendMessage(ChatColor.RED + npc.getName() + " is already building");
            }
            return;
        }

        builder.oncancel = null;
        builder.oncomplete = null;
        builder.onStart = null;
        builder.ContinueLoc = null;
        builder.IgnoreAir = false;
        builder.IgnoreLiquid = false;
        builder.Excavate = false;
        builder.GroupByLayer = true;
        builder.BuildYLayers = 1;
        builder.Silent = false;
        builder.BuildPatternXY = BuilderTrait.BuildPatternsXZ.spiral;

        String value;
        for (String arg : args) {
            if (arg.equalsIgnoreCase("silent")) {
                builder.Silent = true;
            } else if ((value = getOptionalValue("onComplete=", arg)) != null) {
                builder.oncomplete = value;
                sender.sendMessage(ChatColor.GREEN + npc.getName() + " will run task " + value + " on build completion");
            } else if ((value = getOptionalValue("onCancel=", arg)) != null) {
                builder.oncancel = value;
                sender.sendMessage(ChatColor.GREEN + npc.getName() + " will run task " + value + " on build cancellation");
            } else if ((value = getOptionalValue("onStart=", arg)) != null) {
                builder.onStart = value;
                sender.sendMessage(ChatColor.GREEN + npc.getName() + " will run task " + value + " on build start");
            } else if ((value = getOptionalValue("layers=", arg)) != null) {
                int layers;
                try {
                    layers = Integer.parseInt(value);
                    if (layers < 1) {
                        sender.sendMessage(ChatColor.RED +"Number of layers must me positive");
                    }
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + arg + " is not a valid number of layers");
                    return;
                }
                builder.BuildYLayers = layers;
            } else if ((value = getOptionalValue("yoffset=", arg)) != null) {
                int yOffset;
                try {
                    yOffset = Integer.parseInt(value);
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + arg + " is not a valid yOffset");
                    return;
                }
                builder.Yoffset = yOffset;
            } else if (arg.equalsIgnoreCase("groupAll")) {
                builder.GroupByLayer = true;
            } else if (arg.equalsIgnoreCase("ignoreAir")) {
                builder.IgnoreAir = true;
            } else if (arg.equalsIgnoreCase("ignoreLiquid")) {
                builder.IgnoreLiquid = true;
            } else if (arg.equalsIgnoreCase("excavate")) {
                builder.Excavate = true;
                if (!builder.Silent) {
                    sender.sendMessage(ChatColor.GREEN + npc.getName() + " will excavate first");
                }
            } else if (arg.equalsIgnoreCase("spiral")) {
                builder.BuildPatternXY= BuilderTrait.BuildPatternsXZ.spiral;
            } else if (arg.equalsIgnoreCase("reversEspiral")) {
                builder.BuildPatternXY = BuilderTrait.BuildPatternsXZ.reversespiral;
            } else if (arg.equalsIgnoreCase("linear")) {
                builder.BuildPatternXY = BuilderTrait.BuildPatternsXZ.linear;
            } else if (arg.equalsIgnoreCase("reverseLinear")) {
                builder.BuildPatternXY = BuilderTrait.BuildPatternsXZ.reverselinear;
            } else if (arg.equalsIgnoreCase("offset")) {
                builder.offset = true;
            }
        }

        if (builder.RequireMaterials) {
            builder.GetMatsList(builder.Excavate);
        }

        builder.TryBuild(sender);
    }
}
