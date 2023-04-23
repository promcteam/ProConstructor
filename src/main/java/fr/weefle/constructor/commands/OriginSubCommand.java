package fr.weefle.constructor.commands;

import fr.weefle.constructor.essentials.BuilderTrait;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class OriginSubCommand extends AbstractCommand {
    public OriginSubCommand(@Nullable SchematicBuilderCommand parent) {
        super("origin", parent);
        this.permission = "schematicbuilder.origin";
        addAllowedSender(Player.class);
        registerSubCommand(new AbstractCommand("clear", OriginSubCommand.this) {
            @Override
            public void execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull List<String> args) {
                BuilderTrait builder = getSelectedBuilder(sender);
                if (builder == null) {return;}
                builder.Origin = null;
                sender.sendMessage(ChatColor.GREEN + builder.getNPC().getName() + " build origin has been cleared");
            }
        });
        registerSubCommand(new AbstractCommand("schematic", OriginSubCommand.this) {
            @Override
            public void execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull List<String> args) {
                BuilderTrait builder = getSelectedBuilder(sender);
                if (builder == null) {return;}
                NPC npc = builder.getNPC();
                if (builder.schematic == null) {
                    sender.sendMessage(ChatColor.RED + npc.getName() + " has no schematic loaded");
                    return;
                }
                if (builder.schematic.SchematicOrigin == null) {
                    sender.sendMessage(ChatColor.RED + builder.schematic.Name + " has no origin data");
                    return;
                }
                builder.Origin = builder.schematic.getSchematicOrigin(builder);
                sender.sendMessage(ChatColor.GREEN + npc.getName() + " build origin has been set to:" + builder.Origin);
            }
        });
        registerSubCommand(new AbstractCommand("me", OriginSubCommand.this) {
            @Override
            public void execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull List<String> args) {
                BuilderTrait builder = getSelectedBuilder(sender);
                if (builder == null) {return;}
                NPC npc = builder.getNPC();
                builder.Origin = ((Player) sender).getLocation();
                sender.sendMessage(ChatColor.GREEN + npc.getName() + " build origin has been set to your location");
            }
        });
        registerSubCommand(new AbstractCommand("current", OriginSubCommand.this) {
            @Override
            public void execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull List<String> args) {
                BuilderTrait builder = getSelectedBuilder(sender);
                if (builder == null) {return;}
                NPC npc = builder.getNPC();
                if (builder.State == BuilderTrait.BuilderState.building) {
                    builder.Origin = builder.ContinueLoc.clone();
                    sender.sendMessage(ChatColor.GREEN + npc.getName() + " build origin has been set to the origin of the current build");
                } else sender.sendMessage(ChatColor.RED + npc.getName() + " is not currently building!");
            }
        });
    }

    @Override
    public List<String> getUsages(CommandSender sender) {
        List<String> list = super.getUsages(sender);
        list.add(0, '/'+getFullCommand());
        list.add('/'+getFullCommand()+" <x>,<y>,<z>");
        return list;
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull List<String> args) {
        BuilderTrait builder = getSelectedBuilder(sender);
        if (builder == null) {return;}
        NPC npc = builder.getNPC();
        if (args.size() <= 0) {
            if (builder.getNPC().isSpawned()) {
                builder.Origin = builder.getNPC().getEntity().getLocation();
                sender.sendMessage(ChatColor.GREEN + npc.getName() + " build origin has been set to its current location.");
            } else sender.sendMessage(ChatColor.RED + npc.getName() + " not spawned.");
        } else {
            String[] coordinates = args.get(0).split(",");
            if (coordinates.length == 3) {
                try {
                    int x = Integer.parseInt(coordinates[0]);
                    int y = Integer.parseInt(coordinates[1]);
                    int z = Integer.parseInt(coordinates[2]);

                    builder.Origin = new Location(builder.getNPC().getEntity().getWorld(), x, y, z);

                    sender.sendMessage(ChatColor.GREEN + npc.getName() + " build origin has been set to " + builder.Origin.toString());   // Talk to the sender.
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "Invalid Coordinates");
                }
            } else {
                AbstractCommand subCommand = subCommands.get(args.remove(0));
                if (subCommand == null) {
                    sendUsage(sender);
                } else {
                    subCommand.onCommand(sender, command, label, args.toArray(new String[0]));
                }
            }
        }
    }
}
