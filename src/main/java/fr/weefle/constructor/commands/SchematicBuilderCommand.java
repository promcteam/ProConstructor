package fr.weefle.constructor.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SchematicBuilderCommand extends AbstractCommand {
    public SchematicBuilderCommand() {
        super("schematicbuilder", "Main plugin command");
        registerSubCommand(new ReloadSubCommand(this));
        registerSubCommand(new NPCSubCommand(this));
        registerSubCommand(new StructureSubCommand(this));
        registerSubCommand(new BuildSubCommand(this));
        registerSubCommand(new CancelSubCommand(this));
        registerSubCommand(new ExcavatedSubCommand(this));
        registerSubCommand(new PreviewSubCommand(this));
        registerSubCommand(new SurveySubCommand(this));
        registerSubCommand(new LoadSubCommand(this));
        registerSubCommand(new InfoSubCommand(this));
    }

    @Override
    public List<String> getUsages(CommandSender sender) {
        List<String> usages = new ArrayList<>();
        for (AbstractCommand subCommand : subCommands.values()) {
            usages.add(ChatColor.GREEN+"  /"+subCommand.getFullCommand());
            String description;
            if ((description = subCommand.getDescription()) != null) {usages.add("  "+description);}
            usages.add("");
        }
        return usages;
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull List<String> args) {
        super.execute(sender, command, label, args);
    }
}
