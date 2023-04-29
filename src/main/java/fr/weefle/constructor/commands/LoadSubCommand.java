package fr.weefle.constructor.commands;

import fr.weefle.constructor.SchematicBuilder;
import fr.weefle.constructor.hooks.citizens.BuilderTrait;
import fr.weefle.constructor.schematic.BuilderSchematic;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class LoadSubCommand extends AbstractCommand {
    public LoadSubCommand(@Nullable SchematicBuilderCommand parent) {
        super("load", "Loads a schematic file", parent);
        this.permission = "schematicbuilder.load";
    }

    @Override
    public List<String> getArguments(CommandSender sender) {
        List<String> files = new ArrayList<>();
        try {
            Path dir = new File(SchematicBuilder.getInstance().config().getSchematicsFolder()).toPath();
            Files.walk(dir).forEach(path -> {
                String stringPath = path.toString();
                if (path.toFile().isFile() && (stringPath.endsWith(".schem") || stringPath.endsWith(".nbt") || stringPath.endsWith(".yml"))) {
                    files.add(dir.relativize(path).toString());
                }
            });
        } catch (IOException e) { throw new RuntimeException(e); }
        return files;
    }

    @Override
    public List<String> getUsages(CommandSender sender) {
        List<String> list = new ArrayList<>();
        list.add('/'+getFullCommand()+" <schematic>");
        return list;
    }

    @Override
    @Nullable
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            return getArguments(sender);
        } else {
            StringBuilder stringBuilder = new StringBuilder(args[0]);
            for (int i = 1; i < args.length; i++) {
                stringBuilder.append(" ").append(args[i]);
            }
            String arg = stringBuilder.toString();
            List<String> completions = new ArrayList<>();
            StringUtil.copyPartialMatches(stringBuilder.toString().trim(), getArguments(sender), completions);
            completions.removeIf(arg::startsWith);
            return completions;
        }
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull List<String> args) {
        BuilderTrait builder = getSelectedBuilder(sender);
        if (builder == null) {return;}
        if (args.size() == 0) {
            sendUsage(sender);
            return;
        }

        if (builder.getState() != BuilderTrait.BuilderState.IDLE) {
            sender.sendMessage(ChatColor.RED + "Please cancel current build before loading new schematic.");
            return;
        }

        StringBuilder stringBuilder = new StringBuilder(args.get(0));
        for (int i = 1; i < args.size(); i++) {
            stringBuilder.append(" ").append(args.get(i));
        }
        String arg = stringBuilder.toString().trim();
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    BuilderSchematic schematic = SchematicBuilder.getSchematic(arg);
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (schematic == null) {
                                sender.sendMessage(ChatColor.RED + "no such file " + arg);
                            } else {
                                builder.setSchematic(schematic);
                                sender.sendMessage(ChatColor.GREEN + "Loaded Sucessfully");
                                sender.sendMessage(schematic.getInfo());
                            }
                        }
                    }.runTask(SchematicBuilder.getInstance());
                } catch (Exception e) {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            sender.sendMessage(ChatColor.RED + "Failed to load schematic " + arg + ", check console for more details.");
                            SchematicBuilder.getInstance().getLogger().log(Level.WARNING, "Failed to load schematic: " + arg);
                            e.printStackTrace();
                        }
                    }.runTask(SchematicBuilder.getInstance());
                }
            }
        }.runTaskAsynchronously(SchematicBuilder.getInstance());
    }
}
