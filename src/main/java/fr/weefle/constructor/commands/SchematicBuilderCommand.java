package fr.weefle.constructor.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SchematicBuilderCommand extends AbstractCommand {
    public SchematicBuilderCommand() {
        super("schematicbuilder");
        registerSubCommand(new ReloadSubCommand(this));
        registerSubCommand(new NPCSubCommand(this));
        registerSubCommand(new ListSubCommand(this));
        registerSubCommand(new StructureSubCommand(this));
        registerSubCommand(new BuildSubCommand(this));
        registerSubCommand(new CancelSubCommand(this));
        registerSubCommand(new ExcavatedSubCommand(this));
        registerSubCommand(new PreviewSubCommand(this));
        registerSubCommand(new SurveySubCommand(this));
        registerSubCommand(new OriginSubCommand(this));
        registerSubCommand(new MarkSubCommand(this));
        registerSubCommand(new LoadSubCommand(this));
        registerSubCommand(new TimeoutSubCommand(this));
        registerSubCommand(new SupplySubCommand(this));
        registerSubCommand(new HoldSubCommand(this));
        registerSubCommand(new InfoSubCommand(this));
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull List<String> args) {
        super.execute(sender, command, label, args);
    }
}
