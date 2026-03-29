package top.mrxiaom.sweet.buildmobs.func;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Shulker;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import top.mrxiaom.pluginbase.func.AutoRegister;
import top.mrxiaom.pluginbase.utils.CollectionUtils;
import top.mrxiaom.pluginbase.utils.ConfigUtils;
import top.mrxiaom.pluginbase.utils.Util;
import top.mrxiaom.sweet.buildmobs.SweetBuildMobs;
import top.mrxiaom.sweet.buildmobs.api.ITriggerItem;
import top.mrxiaom.sweet.buildmobs.data.match.MatchBlock;
import top.mrxiaom.sweet.buildmobs.data.runtime.BlockGroup;
import top.mrxiaom.sweet.buildmobs.data.Build;
import top.mrxiaom.sweet.buildmobs.data.match.BuildMatchResult;
import top.mrxiaom.sweet.buildmobs.enums.EnumAction;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AutoRegister
public class BuildManager extends AbstractModule implements Listener {
    private final Map<String, Build> loadedBuilds = new HashMap<>();
    private final Map<String, BlockGroup> buildsByItemKeys = new HashMap<>();
    private boolean debugHighlightBlocks = false;
    private boolean debugDisableSpawn = false;
    public BuildManager(SweetBuildMobs plugin) {
        super(plugin);
        registerEvents();
    }

    @Override
    public int priority() {
        return 1010;
    }

    @Override
    public void reloadConfig(MemoryConfiguration pluginConfig) {
        this.debugHighlightBlocks = pluginConfig.getBoolean("debug.highlight-blocks", false);
        this.debugDisableSpawn = pluginConfig.getBoolean("debug.disable-spawn", false);
        loadedBuilds.clear();
        buildsByItemKeys.clear();
        for (String folderPath : pluginConfig.getStringList("builds-folders")) {
            File folder = plugin.resolve(folderPath);
            if (!folder.exists()) {
                Util.mkdirs(folder);
                if (folderPath.equals("./builds")) {
                    plugin.saveResource("builds/example.yml", new File(folder, "example.yml"));
                }
            }
            Util.reloadFolder(folder, false, (id, file) -> {
                YamlConfiguration config = ConfigUtils.load(file);
                try {
                    Build loaded = Build.load(plugin, id, config);
                    loadedBuilds.put(id, loaded);
                    if (loaded.enable()) {
                        ITriggerItem item = loaded.triggerItem();
                        String itemKey = item.key();

                        BlockGroup group = CollectionUtils.getOrPut(buildsByItemKeys, itemKey, (key1) -> new BlockGroup(item));
                        group.addBlocks(loaded.layerBlocks());
                    }
                } catch (RuntimeException e) {
                    warn("[builds/" + id + "] 错误: " + e.getMessage());
                }
            });
        }
        info("加载了 " + loadedBuilds.size() + " 个构筑配置");
    }

    private static boolean isOffHand(PlayerInteractEvent e) {
        try {
            return e.getHand() == EquipmentSlot.OFF_HAND;
        } catch (LinkageError ignored) {
            return false;
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.useItemInHand() == Event.Result.DENY) return;
        if (e.useInteractedBlock() == Event.Result.DENY) return;
        Block block = e.getClickedBlock();
        if (block == null) return;
        EnumAction action = EnumAction.fromEvent(e);
        if (action == null || isOffHand(e)) return;

        String itemKey = plugin.parseItemKey(e.getItem());
        if (itemKey == null) return;

        BlockGroup group = buildsByItemKeys.get(itemKey);
        if (group == null) return;

        BuildMatchResult result = group.matchBuild(block, action);
        if (result == null) return;

        if (debugHighlightBlocks) {
            // 高亮显示所有匹配的方块，持续 5 秒
            World world = block.getWorld();
            List<Entity> highlight = new ArrayList<>();
            for (MatchBlock matchBlock : result.allBlocks()) {
                Location loc = matchBlock.block().getLocation();
                highlight.add(world.spawn(loc, Shulker.class, shulker -> {
                    shulker.setInvisible(true);
                    shulker.setInvulnerable(true);
                    shulker.setGlowing(true);
                    shulker.setGravity(false);
                    shulker.setCollidable(false);
                    shulker.setAI(false);
                    shulker.setSilent(true);
                }));
            }
            plugin.getScheduler().runTaskLater(() -> {
                for (Entity entity : highlight) {
                    entity.remove();
                }
            }, 5 * 20L);
        }
        if (debugDisableSpawn) return;

        // TODO: 执行生成操作等等
    }
}
