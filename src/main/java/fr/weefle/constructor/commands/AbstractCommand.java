package fr.weefle.constructor.commands;

import fr.weefle.constructor.SchematicBuilder;
import fr.weefle.constructor.essentials.BuilderTrait;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Owner;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Entity;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public abstract class AbstractCommand implements CommandExecutor, TabCompleter {
    protected final String                              command;
    protected final AbstractCommand                     parent;
    protected final Map<String,AbstractCommand>         subCommands    = new HashMap<>();
    protected final Set<Class<? extends CommandSender>> allowedSenders = new HashSet<>();
    protected String permission;

    public AbstractCommand(@NotNull String command, @Nullable AbstractCommand parent) {
        this.command = command;
        this.parent = parent;
    }

    public AbstractCommand(@NotNull String command) { this(command, null); }

    public final String getCommand() { return command; }

    public final String getFullCommand() { return parent == null ? command + " [id]" : parent.getFullCommand()+' '+command; }

    protected final void registerSubCommand(AbstractCommand subCommand) {
        subCommands.put(subCommand.getCommand(), subCommand);
    }

    protected final void addAllowedSender(Class<? extends CommandSender> senderClass) { allowedSenders.add(senderClass); }

    @Nullable
    public String getPermission() { return this.permission; }

    @Override
    public final boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        boolean validSender = false;
        if (allowedSenders.isEmpty()) {
            validSender = true;
        } else {
            for (Class<? extends CommandSender> allowedSender : allowedSenders) {
                if (allowedSender.isAssignableFrom(sender.getClass())) {
                    validSender = true;
                    break;
                }
            }
        }
        if (!validSender) {
            sender.sendMessage(ChatColor.RED+"Command not runnable from here.");
            return true;
        }
        if (this.permission != null && !sender.hasPermission(this.permission)) {
            sender.sendMessage(ChatColor.RED+"You lack the permission \""+this.permission+"\" to run that command.");
            return true;
        }

        List<String> arguments = new ArrayList<>(args.length);
        arguments.addAll(Arrays.asList(args));
        if (arguments.size() != 0) {
            try {
                int id = Integer.parseInt(arguments.get(0));
                NPC npc = CitizensAPI.getNPCRegistry().getById(id);
                arguments.remove(0);
                if (npc == null) {
                    sender.sendMessage(ChatColor.RED+"No NPC with id "+id+" found.");
                    return true;
                }
                CitizensAPI.getDefaultNPCSelector().select(sender, npc);
            } catch (NumberFormatException ignored) { }
        }

        if (args.length > 0) {
            AbstractCommand subCommand = subCommands.get(arguments.get(0));
            if (subCommand != null) {
                arguments.remove(0);
                subCommand.onCommand(sender, command, label, arguments.toArray(new String[0]));
                return true;
            }
        }

        execute(sender, command, label, arguments);
        return true;
    }

    public void execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull List<String> args) {
        sendUsage(sender);
    }

    public List<String> getArguments(CommandSender sender) {
        List<String> list = new ArrayList<>();
        for (Map.Entry<String,AbstractCommand> entry : subCommands.entrySet()) {
            String permission = entry.getValue().getPermission();
            if (permission == null || sender.hasPermission(permission)) {
                list.add(entry.getKey());
            }
        }
        return list;
    }

    public List<String> getUsages(CommandSender sender) {
        List<String> arguments = getArguments(sender);
        List<String> usages;
        if (arguments.isEmpty()) {
            usages = new ArrayList<>(1);
            usages.add('/'+getFullCommand());
        } else {
            List<String> regularArguments = new ArrayList<>();
            List<String> optionalArguments = new ArrayList<>();
            for (String argument : arguments) {
                (argument.startsWith("-") ?
                        optionalArguments :
                        regularArguments).add(argument);
            }
            usages = new ArrayList<>(arguments.size());
            for (String argument : regularArguments) {
                StringBuilder builder = new StringBuilder('/'+getFullCommand()+' '+argument);
                for (String optionalArgument : optionalArguments) {
                    builder.append(' ').append(optionalArgument);
                    if (optionalArgument.endsWith("=")) {
                        builder.append('<').append(optionalArgument).append('>');
                    }
                }
                usages.add(builder.toString());
            }
        }
        return usages;
    }

    public final void sendUsage(@NotNull CommandSender sender) {
        List<String> usages = getUsages(sender);
        if (usages.isEmpty()) {
            sender.sendMessage(ChatColor.RED+"Incorrect command syntax");
        } else {
            sender.sendMessage(ChatColor.RED+"Command syntax:");
            for (String usage : usages) { sender.sendMessage(ChatColor.RED+"  "+usage); }
        }
    }

    @Override
    @Nullable
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            return getArguments(sender);
        } else {
            AbstractCommand subcommand = subCommands.get(args[0]);
            if (subcommand == null) {
                List<String> completions = new ArrayList<>();
                StringUtil.copyPartialMatches(args[args.length-1], getArguments(sender), completions);
                for (int i = 0, last = args.length-1; i < last; i++) {
                    String arg = args[i];
                    completions.removeIf(arg::startsWith);
                }
                return completions;
            } else {
                return subcommand.onTabComplete(sender, command, label, Arrays.copyOfRange(args, 1, args.length));
            }
        }
    }

    @Nullable
    protected BuilderTrait getSelectedBuilder(CommandSender sender) {
        NPC npc = CitizensAPI.getDefaultNPCSelector().getSelected(sender);
        if (npc == null) {
            sender.sendMessage(ChatColor.RED + "You must have a NPC selected to use this command");
            return null;
        }
        BuilderTrait builderTrait = SchematicBuilder.getInstance().getBuilder(npc);
        if (builderTrait == null) {
            sender.sendMessage(ChatColor.RED + "The selected NPC is not a builder");
            return null;
        }
        if (!(sender instanceof ConsoleCommandSender) && !sender.hasPermission("citizens.admin")) {
            Owner ownerTrait = npc.getTraitNullable(Owner.class);
            if (ownerTrait != null && (!(sender instanceof Entity) || !((Entity) sender).getUniqueId().equals(ownerTrait.getOwnerId()))) {
                sender.sendMessage(ChatColor.RED + "You must be the owner of this NPC to execute this command");
                return null;
            }
        }
        return builderTrait;
    }

    @Nullable
    protected String getOptionalValue(String name, String arg) {
        if (!arg.substring(0, name.length()).equalsIgnoreCase(name)) { return null; }
        return arg.substring(name.length());
    }
}
