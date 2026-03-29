package top.mrxiaom.sweet.buildmobs.func;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Shulker;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import top.mrxiaom.pluginbase.func.AutoRegister;
import top.mrxiaom.pluginbase.utils.CollectionUtils;
import top.mrxiaom.pluginbase.utils.ConfigUtils;
import top.mrxiaom.pluginbase.utils.Util;
import top.mrxiaom.sweet.buildmobs.SweetBuildMobs;
import top.mrxiaom.sweet.buildmobs.api.ITriggerItem;
import top.mrxiaom.sweet.buildmobs.data.LayerBlock;
import top.mrxiaom.sweet.buildmobs.data.match.MatchBlock;
import top.mrxiaom.sweet.buildmobs.data.runtime.BlockGroupByBlock;
import top.mrxiaom.sweet.buildmobs.data.runtime.BlockGroupByItem;
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
    private final Map<String, BlockGroupByItem> buildsByItemKeys = new HashMap<>();
    private final Map<String, BlockGroupByBlock> buildsByBlockKeys = new HashMap<>();
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
        buildsByBlockKeys.clear();
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
                        if (loaded.noItemsNeeded()) {
                            for (LayerBlock layerBlock : loaded.layerBlocks()) {
                                String blockKey = layerBlock.blockKey();
                                BlockGroupByBlock group = CollectionUtils.getOrPut(buildsByBlockKeys, blockKey, (key1) -> new BlockGroupByBlock());
                                group.addBlock(layerBlock);
                            }
                        } else {
                            ITriggerItem item = loaded.triggerItem();
                            String itemKey = item.key();

                            BlockGroupByItem group = CollectionUtils.getOrPut(buildsByItemKeys, itemKey, (key1) -> new BlockGroupByItem());
                            group.addBlocks(loaded.layerBlocks());
                        }
                    }
                } catch (RuntimeException e) {
                    warn("[builds/" + id + "] 错误: " + e.getMessage());
                }
            });
        }
        info("加载了 " + loadedBuilds.size() + " 个构筑配置");
    }

    private void doSpawnBuild(Player player, Block block, BuildMatchResult result, @Nullable ItemStack item) {

        // 检查方块是否在其它插件的保护区内
        for (MatchBlock matchBlock : result.allBlocks()) {
            if (plugin.isProtectedBlock(player, matchBlock.block())) {
                return;
            }
        }

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

        Build build = result.build();

        if (item != null) {
            // 检查并扣除物品
            int costCount = build.spawnCostItemsCount();
            if (costCount > 0) {
                int amount = item.getAmount();
                if (amount < costCount) {
                    build.spawnCostItemsDeny(player);
                    return;
                } else {
                    item.setAmount(Math.max(0, amount - costCount));
                    Util.submitInvUpdate(player);
                }
            }
        }

        // 移除方块
        for (MatchBlock matchBlock : result.allBlocks()) {
            if (build.shouldRemoveBlock(matchBlock)) {
                matchBlock.block().setType(Material.AIR);
            }
        }

        // 计算生成位置
        Location mobLoc = build.spawnLocType().read(result)
                .add(build.spawnLocOffset());
        mobLoc.setDirection(result.checkFacing().toBukkit().getDirection());

        // 执行生成操作
        build.spawnPreActions(player, mobLoc);
        build.spawnMobType().spawn(mobLoc);
        build.spawnPostActions(player, mobLoc);
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
        Player player = e.getPlayer();
        Block block = e.getClickedBlock();
        if (block == null) return;
        EnumAction action = EnumAction.fromEvent(e);
        if (action == null || isOffHand(e)) return;

        ItemStack item = e.getItem();
        String itemKey = plugin.parseItemKey(item);
        if (itemKey == null) return;

        // 获取物品对应的方块分组
        BlockGroupByItem group = buildsByItemKeys.get(itemKey);
        if (group == null) return;

        // 通过分组配置，解析并匹配构筑
        BuildMatchResult result = group.matchBuild(block, action);
        if (result == null) return;

        doSpawnBuild(player, block, result, item);
    }
}
