package top.mrxiaom.sweet.buildmobs.data.runtime;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.mrxiaom.sweet.buildmobs.api.ITriggerItem;
import top.mrxiaom.sweet.buildmobs.data.Build;
import top.mrxiaom.sweet.buildmobs.data.LayerBlock;
import top.mrxiaom.sweet.buildmobs.data.match.BuildMatchResult;
import top.mrxiaom.sweet.buildmobs.enums.EnumAction;
import top.mrxiaom.sweet.buildmobs.enums.EnumFacing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlockGroup {
    private final ITriggerItem item;
    private final Map<EnumFacing, List<LayerBlock>> blockMap = new HashMap<>();

    public BlockGroup(ITriggerItem item) {
        this.item = item;
        for (EnumFacing facing : EnumFacing.values()) {
            blockMap.put(facing, new ArrayList<>());
        }
    }

    public ITriggerItem item() {
        return item;
    }

    public void addBlocks(List<LayerBlock> blocks) {
        for (LayerBlock block : blocks) {
            Build build = block.build();
            // 添加方块的时候就过滤掉不允许点击的方块
            if (!build.isAvailableBlock(block)) continue;
            // 然后将方块按允许使用的朝向，添加到列表中
            for (EnumFacing facing : EnumFacing.values()) {
                if (!build.isAvailableFacing(facing)) continue;
                blockMap.get(facing).add(block);
            }
        }
    }

    @Nullable
    public BuildMatchResult matchBuild(@NotNull Block block, @NotNull EnumAction action) {
        for (EnumFacing facing : EnumFacing.values()) {
            BuildMatchResult result = matchBuild(block, action, facing);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    @Nullable
    public BuildMatchResult matchBuild(@NotNull Block block, @NotNull EnumAction action, @NotNull EnumFacing facing) {
        World world = block.getWorld();
        List<LayerBlock> list = blockMap.get(facing);
        for (LayerBlock layerBlock : list) {
            Build build = layerBlock.build();
            if (build.isAvailableAction(action) && build.isAvailableWorld(world)) {
                BuildMatchResult result = layerBlock.match(block, facing);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }
}
