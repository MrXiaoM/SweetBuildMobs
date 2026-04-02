package top.mrxiaom.sweet.buildmobs.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.mrxiaom.pluginbase.func.AutoRegister;
import top.mrxiaom.pluginbase.utils.Pair;
import top.mrxiaom.sweet.buildmobs.Messages;
import top.mrxiaom.sweet.buildmobs.SweetBuildMobs;
import top.mrxiaom.sweet.buildmobs.enums.EnumFacing;
import top.mrxiaom.sweet.buildmobs.func.AbstractModule;
import top.mrxiaom.sweet.buildmobs.func.SelectionManager;
import top.mrxiaom.sweet.buildmobs.utils.Selection;

import java.io.File;
import java.util.*;

import static top.mrxiaom.pluginbase.utils.CollectionUtils.startsWith;

@AutoRegister
public class CommandMain extends AbstractModule implements CommandExecutor, TabCompleter, Listener {
    public CommandMain(SweetBuildMobs plugin) {
        super(plugin);
        registerCommand("sweetbuildmobs", this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (args.length == 2 && "select".equalsIgnoreCase(args[0]) && sender.isOp()) {
            if (!(sender instanceof Player)) {
                return true;
            }
            Player player = (Player) sender;
            SelectionManager manager = SelectionManager.inst();
            if ("start".equalsIgnoreCase(args[1])) {
                manager.startSelection(player);
                return Messages.Command.select__start.tm(player);
            }
            if ("stop".equalsIgnoreCase(args[1])) {
                manager.resetSelection(player);
                return Messages.Command.select__stop.tm(player);
            }
            if ("save".equalsIgnoreCase(args[1])) {
                if (!manager.isInSelectMode(player)) {
                    return Messages.Command.select__save__not_started.tm(player);
                }
                Selection selection = manager.getSelection(player);
                if (selection == null) {
                    return Messages.Command.select__save__not_selected.tm(player);
                }
                EnumFacing facing = EnumFacing.fromDirection(player, true);
                ConfigurationSection blockLayers = selection.saveBlockLayers(facing)
                        .orElseGet(reason -> {
                            String message = reason.getMessage();
                            Messages.Command.select__save__error.tm(player, Pair.of("%error%", message));
                            return null;
                        });
                if (blockLayers != null) {
                    YamlConfiguration config = new YamlConfiguration();
                    config.set("block-layers", blockLayers);
                    try {
                        config.save(new File(plugin.getDataFolder(), "output.yml"));
                        return Messages.Command.select__save__success.tm(player);
                    } catch (Throwable t) {
                        Messages.Command.select__save__error.tm(player, Pair.of("%error%", t.getMessage()));
                    }
                }
                return true;
            }
        }
        if (args.length == 1 && "reload".equalsIgnoreCase(args[0]) && sender.isOp()) {
            plugin.reloadConfig();
            return Messages.Command.reload.tm(sender);
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String alias, @NotNull String[] args) {
        boolean op = sender.isOp();
        if (args.length == 1) {
            List<String> list = new ArrayList<>();
            if (op) {
                list.add("select");
                list.add("reload");
            }
            return startsWith(args[0], list);
        }
        if (args.length == 2) {
            if (op && "select".equalsIgnoreCase(args[0]) && sender instanceof Player) {
                List<String> list = new ArrayList<>();
                list.add("start");
                list.add("stop");
                list.add("save");
                return startsWith(args[1], list);
            }
        }
        return Collections.emptyList();
    }
}
