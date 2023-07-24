package fr.weefle.constructor.commands;

import fr.weefle.constructor.SchematicBuilder;
import fr.weefle.constructor.hooks.citizens.BuilderTrait;
import fr.weefle.constructor.schematic.Schematic;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class OriginSubCommand extends AbstractCommand {
    public OriginSubCommand(@Nullable SchematicBuilderCommand parent) {
        super("origin", "Sets the build origin", parent);
        this.permission = "schematicbuilder.origin";
        addAllowedSender(Player.class);
        registerSubCommand(new AbstractCommand("clear", OriginSubCommand.this) {
            @Override
            public void execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull List<String> args) {
                BuilderTrait builder = getSelectedBuilder(sender);
                if (builder == null) {return;}
                builder.setOrigin(null);
                sender.sendMessage(ChatColor.GREEN + builder.getNPC().getName() + " build origin has been cleared");
            }
        });
        registerSubCommand(new AbstractCommand("schematic", OriginSubCommand.this) {
            @Override
            public void execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull List<String> args) {
                BuilderTrait builder = getSelectedBuilder(sender);
                if (builder == null) {return;}
                NPC npc = builder.getNPC();
                Schematic schematic = builder.getSchematic();
                if (schematic == null) {
                    sender.sendMessage(SchematicBuilder.format(
                            SchematicBuilder.getInstance().config().getNoSchematicSelectedMessage(),
                            builder.getNPC(),
                            builder.getSchematic(),
                            sender,
                            null, "0"));
                    return;
                }
                Vector absolutePosition = schematic.getAbsolutePosition();
                if (absolutePosition == null) {
                    sender.sendMessage(schematic.getDisplayName() + ChatColor.RED + " has no origin data");
                    return;
                }
                builder.setOrigin(absolutePosition.toLocation(builder.getNPC().getEntity().getWorld()));
                sender.sendMessage(ChatColor.GREEN + npc.getName() + " build origin has been set to:" + ChatColor.WHITE + builder.getOrigin());
            }
        });
        registerSubCommand(new AbstractCommand("me", OriginSubCommand.this) {
            @Override
            public void execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull List<String> args) {
                BuilderTrait builder = getSelectedBuilder(sender);
                if (builder == null) {return;}
                NPC npc = builder.getNPC();
                builder.setOrigin(((Player) sender).getLocation());
                sender.sendMessage(ChatColor.GREEN + npc.getName() + " build origin has been set to your location");
            }
        });
        registerSubCommand(new AbstractCommand("current", OriginSubCommand.this) {
            @Override
            public void execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull List<String> args) {
                BuilderTrait builder = getSelectedBuilder(sender);
                if (builder == null) {return;}
                NPC npc = builder.getNPC();
                if (builder.getState() == BuilderTrait.BuilderState.BUILDING) {
                    builder.setOrigin(builder.getContinueLoc());
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
                builder.setOrigin(builder.getNPC().getEntity().getLocation());
                sender.sendMessage(ChatColor.GREEN + npc.getName() + " build origin has been set to its current location.");
            } else sender.sendMessage(ChatColor.RED + npc.getName() + " not spawned.");
        } else {
            String[] coordinates = args.get(0).split(",");
            if (coordinates.length == 3) {
                try {
                    int x = Integer.parseInt(coordinates[0]);
                    int y = Integer.parseInt(coordinates[1]);
                    int z = Integer.parseInt(coordinates[2]);

                    builder.setOrigin(new Location(builder.getNPC().getEntity().getWorld(), x, y, z));

                    sender.sendMessage(ChatColor.GREEN + npc.getName() + " build origin has been set to " + ChatColor.WHITE + builder.getOrigin());
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
