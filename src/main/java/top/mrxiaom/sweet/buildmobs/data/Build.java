package top.mrxiaom.sweet.buildmobs.data;

import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.mrxiaom.pluginbase.utils.Util;
import top.mrxiaom.sweet.buildmobs.SweetBuildMobs;
import top.mrxiaom.sweet.buildmobs.enums.EnumAction;
import top.mrxiaom.sweet.buildmobs.enums.EnumFacing;
import top.mrxiaom.sweet.buildmobs.api.IBlockDefine;
import top.mrxiaom.sweet.buildmobs.api.ITriggerItem;

import java.util.*;

/**
 * 构筑配置
 */
public class Build {
    private final SweetBuildMobs plugin;
    private final String id;
    private final boolean enable;
    private final List<String> worldsWhiteList;
    private final List<String> worldsBlackList;
    private final List<char[][]> layerList;
    private final Map<Character, IBlockDefine> layerDefines;
    private final List<EnumFacing> layerRequireFacings;
    private final ITriggerItem triggerItem;
    private final List<Character> triggerItemRequireBlocks;
    private final List<EnumAction> triggerItemRequireActions;

    private final List<LayerBlock> layerBlockList;
    private final LayerBlock[][][] layerBlockByLoc;
    private Build(SweetBuildMobs plugin, String id, ConfigurationSection config) {
        ConfigurationSection section;
        this.plugin = plugin;
        this.id = id;
        this.enable = config.getBoolean("enable", false);

        this.worldsWhiteList = config.getStringList("worlds.whitelist");
        this.worldsBlackList = config.getStringList("worlds.blacklist");

        this.layerDefines = new HashMap<>();
        section = config.getConfigurationSection("block-layers.defines");
        if (section != null) for (String key : section.getKeys(false)) {
            if (key.length() > 1) {
                warn("block-layers.defines 出现过长的名字定义: '" + key + "'");
                continue;
            }
            ConfigurationSection section1 = section.getConfigurationSection(key);
            if (section1 == null) {
                warn("block-layers.defines." + key + " 输入的值无效");
                continue;
            }
            char defineId = key.charAt(0);
            IBlockDefine define = plugin.parseBlockDefine(section1);
            if (define == null) {
                warn("无法从 block-layers.defines." + key + " 输入的值中解析指定的方块定义");
                continue;
            }
            this.layerDefines.put(defineId, define);
        }
        if (this.layerDefines.isEmpty()) {
            throw new IllegalArgumentException("block-layers.defines 中没有任何有效的方块定义");
        }

        this.layerList = new ArrayList<>();
        char[][] sampleLayer = null;
        Map<Integer, char[][]> layerMap = new HashMap<>();
        section = config.getConfigurationSection("block-layers.layers");
        if (section != null) for (String key : section.getKeys(false)) {
            Integer layerNumber = Util.parseInt(key).orElse(null);
            if (layerNumber == null) {
                warn("block-layers.layers 中的层数 '" + key + "' 无法解析为整数");
                continue;
            }
            List<String> list = section.getStringList(key);
            if (list.isEmpty()) {
                warn("block-layers.layers." + key + " 配置的列表为空");
                continue;
            }
            char[][] layer = new char[list.size()][];
            Integer width = null;
            for (int i = 0; i < list.size(); i++) {
                char[] chars = list.get(i).toCharArray();
                if (chars.length == 0) {
                    throw new IllegalArgumentException("block-layers.layers." + key + "[" + i + "] 是一个空行");
                }
                if (width == null) {
                    width = chars.length;
                } else if (chars.length != width) {
                    throw new IllegalArgumentException("block-layers.layers." + key + "[" + i + "] 的列数有误，" +
                            "该行有 " + chars.length + " 列方块，预期中应有 " + width + " 列方块");
                }
                layer[i] = chars;
            }
            if (sampleLayer == null) {
                sampleLayer = layer;
            } else {
                if (layer.length != sampleLayer.length) {
                    throw new IllegalArgumentException("block-layers.layers." + key + " 的行数有误，" +
                            "该层级有 " + layer.length + " 行方块，预期中应有 " + sampleLayer.length + " 行方块");
                }
                for (int i = 0; i < layer.length; i++) {
                    int actualLength = layer[i].length;
                    int expectLength = sampleLayer[i].length;
                    if (actualLength != expectLength) {

                        throw new IllegalArgumentException("block-layers.layers." + key + "[" + i + "] 的列数有误，" +
                                "该行有 " + actualLength + " 列方块，预期中应有 " + expectLength + " 列方块");
                    }
                }
            }
            layerMap.put(layerNumber, layer);
        }
        List<Integer> layerKeys = new ArrayList<>(layerMap.keySet());
        layerKeys.sort(Comparator.comparingInt(Integer::intValue));
        for (Integer key : layerKeys) {
            char[][] chars = layerMap.get(key);
            if (chars != null) {
                this.layerList.add(chars);
            }
        }
        if (this.layerList.isEmpty()) {
            throw new IllegalArgumentException("block-layers.layers 中没有任何有效的构筑布局");
        }

        this.layerRequireFacings = new ArrayList<>();
        for (String s : config.getStringList("block-layers.require-facing")) {
            EnumFacing facing = Util.valueOrNull(EnumFacing.class, s);
            if (facing == null) {
                warn("block-layers.require-facing 中包含无效的值 '" + s + "'");
                continue;
            }
            this.layerRequireFacings.add(facing);
        }

        ITriggerItem triggerItem = plugin.parseTriggerItem(config.getConfigurationSection("trigger-item.item"));
        if (triggerItem == null) {
            throw new IllegalArgumentException("无法解析 trigger-item.item 指定的物品");
        }
        this.triggerItem = triggerItem;

        this.triggerItemRequireBlocks = new ArrayList<>();
        for (String s : config.getStringList("trigger-item.require-blocks")) {
            if (s.length() != 1 || !layerDefines.containsKey(s.charAt(0))) {
                warn("trigger-item.require-blocks 指定的方块 '" + s + "' 未定义");
                continue;
            }
            this.triggerItemRequireBlocks.add(s.charAt(0));
        }

        this.triggerItemRequireActions = new ArrayList<>();
        for (String s : config.getStringList("trigger-item.require-actions")) {
            EnumAction action = Util.valueOrNull(EnumAction.class, s);
            if (action == null) {
                warn("trigger-item.require-actions 中包含无效的值 '" + s + "'");
                continue;
            }
            this.triggerItemRequireActions.add(action);
        }

        this.layerBlockList = new ArrayList<>();
        this.layerBlockByLoc = new LayerBlock[this.layerList.size()][][];
        this.postLoad();
    }
    private void warn(String m) {
        plugin.warn("[builds/" + id + "] " + m);
    }
    private void postLoad() {
        for (int layerNumber = 0; layerNumber < layerList.size(); layerNumber++) {
            char[][] layer = layerList.get(layerNumber);
            int lines = layer.length;
            this.layerBlockByLoc[layerNumber] = new LayerBlock[lines][];
            for (int i = 0; i < lines; i++) {
                int y = lines - i - 1;
                int width = layer[i].length;
                this.layerBlockByLoc[layerNumber][y] = new LayerBlock[width];
                for (int x = 0; x < width; x++) {
                    char defineId = layer[i][x];
                    // 需要忽略半角空格和全角空格，将那个位置当作空气
                    if (defineId == ' ' || defineId == '\u3000') continue;

                    IBlockDefine blockDefine = layerDefines.get(defineId);
                    if (blockDefine == null) {
                        throw new IllegalArgumentException("预料中的错误，无法找到 layers[" + layerNumber + "][" + i + "][" + x + "] 对应的方块定义");
                    }
                    LayerBlock block = new LayerBlock(this, defineId, blockDefine, layerNumber, x, y);
                    this.layerBlockList.add(block);
                    this.layerBlockByLoc[layerNumber][y][x] = block;
                }
            }
        }
    }

    /**
     * 获取构筑配置 ID
     */
    public String id() {
        return id;
    }

    /**
     * 获取插件实例
     */
    public SweetBuildMobs plugin() {
        return plugin;
    }

    /**
     * 获取该构筑配置是否启用
     */
    public boolean enable() {
        return enable;
    }

    /**
     * 或者这个构筑的长度，即层级数量
     */
    public int getLayerLength() {
        return this.layerBlockByLoc.length;
    }

    /**
     * 获取这个构筑的高度，即 本地Y轴 方向有多长
     */
    public int getLayerHeight() {
        return this.layerBlockByLoc[0].length;
    }

    /**
     * 获取这个构筑的宽度，即 本地X轴 方向有多长
     */
    public int getLayerWidth() {
        return this.layerBlockByLoc[0][0].length;
    }

    /**
     * 按本地坐标获取该构筑的某个方块的信息
     * @param layer 第几层，从 0 开始
     * @param x 本地X轴坐标，从 0 开始
     * @param y 本地Y轴坐标，从 0 开始
     * @return 如果超出范围，或者方块不存在，返回 <code>null</code>
     */
    @Nullable
    public LayerBlock getLayerBlock(int layer, int x, int y) {
        if (layer < 0 || layer >= this.layerBlockByLoc.length) {
            return null;
        }
        LayerBlock[][] array1 = this.layerBlockByLoc[layer];
        if (y < 0 || y >= array1.length) {
            return null;
        }
        LayerBlock[] array2 = array1[y];
        if (x < 0 || x >= array2.length) {
            return null;
        }
        return array2[x];
    }

    /**
     * 获取构筑方块列表
     */
    @NotNull
    public List<LayerBlock> layerBlocks() {
        return Collections.unmodifiableList(layerBlockList);
    }

    /**
     * 获取该构筑要求的朝向列表
     */
    @NotNull
    public List<EnumFacing> layerRequireFacings() {
        return Collections.unmodifiableList(layerRequireFacings);
    }

    /**
     * 检查该构筑是否允许按指定朝向进行检查
     * @param facing 指定朝向
     */
    public boolean isAvailableFacing(@NotNull EnumFacing facing) {
        if (layerRequireFacings.isEmpty()) {
            return true;
        }
        return layerRequireFacings.contains(facing);
    }

    /**
     * 获取触发检查该构筑所需的物品
     */
    @NotNull
    public ITriggerItem triggerItem() {
        return triggerItem;
    }

    /**
     * 获取触发检查该构筑需要持有物品点击什么方块
     */
    @NotNull
    public List<Character> triggerItemRequireBlocks() {
        return Collections.unmodifiableList(triggerItemRequireBlocks);
    }

    /**
     * 检查触发检查该构筑是否允许指定方块来触发
     * @param block 指定方块
     */
    public boolean isAvailableBlock(@NotNull LayerBlock block) {
        if (block.build() != this) {
            return false;
        }
        if (triggerItemRequireBlocks.isEmpty()) {
            return true;
        }
        return triggerItemRequireBlocks.contains(block.defineId());
    }

    /**
     * 获取触发检查构筑需要持有物品以什么方式点击方块
     */
    @NotNull
    public List<EnumAction> triggerItemRequireActions() {
        return Collections.unmodifiableList(triggerItemRequireActions);
    }

    /**
     * 检查触发检查该构筑是否允许按指定方式来触发
     * @param action 指定方式
     */
    public boolean isAvailableAction(@NotNull EnumAction action) {
        if (triggerItemRequireActions.isEmpty()) {
            return true;
        }
        return triggerItemRequireActions.contains(action);
    }

    /**
     * 检查触发检查该构筑是否允许在指定世界触发
     * @param world 指定世界
     */
    public boolean isAvailableWorld(World world) {
        String name = world.getName();
        if (!worldsWhiteList.isEmpty() && !worldsWhiteList.contains(name)) {
            return false;
        }
        return !worldsBlackList.contains(name);
    }

    /**
     * 加载构筑配置
     * @param plugin 插件实例
     * @param id 配置ID
     * @param config 配置
     * @throws RuntimeException 当配置解析出错时抛出异常
     */
    @NotNull
    public static Build load(SweetBuildMobs plugin, String id, ConfigurationSection config) {
        return new Build(plugin, id, config);
    }
}
