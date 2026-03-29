package top.mrxiaom.sweet.buildmobs.data.runtime;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.mrxiaom.sweet.buildmobs.api.IBlockDefine;
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

public class BlockGroupByBlock {
    private final Map<EnumFacing, List<LayerBlock>> blockMap = new HashMap<>();

    public BlockGroupByBlock() {
        for (EnumFacing facing : EnumFacing.values()) {
            blockMap.put(facing, new ArrayList<>());
        }
    }

    public void addBlock(LayerBlock block) {
        Build build = block.build();
        // 将方块按允许使用的朝向，添加到列表中
        for (EnumFacing facing : EnumFacing.values()) {
            if (!build.isAvailableFacing(facing)) continue;
            blockMap.get(facing).add(block);
        }
    }

    @Nullable
    public BuildMatchResult matchBuild(@NotNull Block block) {
        for (EnumFacing facing : EnumFacing.values()) {
            BuildMatchResult result = matchBuild(block, facing);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    @Nullable
    public BuildMatchResult matchBuild(@NotNull Block block, @NotNull EnumFacing facing) {
        World world = block.getWorld();
        List<LayerBlock> list = blockMap.get(facing);
        for (LayerBlock layerBlock : list) {
            Build build = layerBlock.build();
            if (build.isAvailableWorld(world)) {
                BuildMatchResult result = layerBlock.match(block, facing);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }
}
