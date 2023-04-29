package fr.weefle.constructor.commands;

import fr.weefle.constructor.hooks.citizens.BuilderTrait;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TimeoutSubCommand extends AbstractCommand {
    public TimeoutSubCommand(@Nullable SchematicBuilderCommand parent) {
        super("timeout", "Sets the maximum number of seconds between placed blocks", parent);
        this.permission = "schematicbuilder.timeout";
    }

    @Override
    public List<String> getUsages(CommandSender sender) {
        List<String> list = new ArrayList<>();
        list.add('/'+getFullCommand());
        list.add('/'+getFullCommand()+" <seconds>");
        return list;
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull List<String> args) {
        BuilderTrait builder = getSelectedBuilder(sender);
        if (builder == null) {return;}
        NPC npc = builder.getNPC();

        if (args.size() == 0) {
            sender.sendMessage(ChatColor.GOLD + npc.getName() + "'s Move Timeout is " + builder.getMoveTimeoutSeconds());
            return;
        } else if (args.size() != 1) {
            sendUsage(sender);
            return;
        }

        double timeout;
        try {
            timeout = Double.parseDouble(args.get(0));
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + args.get(0) + " is not a valid number");
            return;
        }
        timeout = Math.min(Math.max(0.1, timeout), 2000000);

        builder.setMoveTimeoutSeconds(timeout);
        sender.sendMessage(ChatColor.GREEN + npc.getName() + " move timeout set to " + timeout + " s.");
    }
}
