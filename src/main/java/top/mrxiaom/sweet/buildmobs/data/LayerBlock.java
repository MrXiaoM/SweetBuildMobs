package top.mrxiaom.sweet.buildmobs.data;

import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;
import top.mrxiaom.sweet.buildmobs.api.IBlockDefine;
import top.mrxiaom.sweet.buildmobs.data.match.BuildMatchResult;
import top.mrxiaom.sweet.buildmobs.data.match.MatchBlock;
import top.mrxiaom.sweet.buildmobs.enums.EnumFacing;

import java.util.ArrayList;
import java.util.List;

/**
 * 构筑方块配置
 */
public class LayerBlock {
    private final Build build;
    private final char defineId;
    private final IBlockDefine blockDefine;
    private final String blockKey;
    private final int layer, localX, localY;

    protected LayerBlock(Build build, char defineId, IBlockDefine blockDefine, int layer, int localX, int localY) {
        this.build = build;
        this.defineId = defineId;
        this.blockDefine = blockDefine;
        this.blockKey = blockDefine.key();
        this.layer = layer;
        this.localX = localX;
        this.localY = localY;
    }

    /**
     * 获取该方块所处的构筑实例
     */
    public Build build() {
        return build;
    }

    /**
     * 获取方块定义 ID
     */
    public char defineId() {
        return defineId;
    }

    /**
     * 获取方块定义实例
     */
    public IBlockDefine blockDefine() {
        return blockDefine;
    }

    /**
     * 获取方块类型键
     */
    public String blockKey() {
        return blockKey;
    }

    /**
     * 获取这个方块在第几层
     */
    @Range(from = 0, to = Integer.MAX_VALUE)
    public int layer() {
        return layer;
    }

    /**
     * 获取这个方块的本地X轴坐标
     */
    @Range(from = 0, to = Integer.MAX_VALUE)
    public int localX() {
        return localX;
    }

    /**
     * 获取这个方块的本地Y轴坐标
     */
    @Range(from = 0, to = 320)
    public int localY() {
        return localY;
    }

    /**
     * 根据已有方块坐标，获取另一个方块的位置和信息
     * @param block 当前方块坐标
     * @param another 另一个方块
     * @param facing 按什么朝向来判定
     */
    @NotNull
    public Block getRelative(Block block, LayerBlock another, EnumFacing facing) {
        int modLayer = another.layer - layer;
        int modX = another.localX - localX;
        int modY = another.localY - localY;
        return facing.getRelative(block, modLayer, modX, modY);
    }

    /**
     * 根据当前方块匹配构筑
     * @param block 方块坐标信息
     * @param facing 按什么朝向来判定
     * @return 构筑匹配结果，<code>null</code> 代表不匹配
     */
    @Nullable
    public BuildMatchResult match(@NotNull Block block, EnumFacing facing) {
        if (blockDefine.isMatch(block, facing)) {
            MatchBlock self = new MatchBlock(this, block);
            List<MatchBlock> allBlocks = new ArrayList<>();
            for (LayerBlock another : build.layerBlocks()) {
                if (another == this) {
                    allBlocks.add(self);
                } else {
                    Block relative = getRelative(block, another, facing);
                    if (another.blockDefine.isMatch(relative, facing)) {
                        allBlocks.add(new MatchBlock(another, relative));
                    } else {
                        allBlocks.clear();
                        return null;
                    }
                }
            }
            return new BuildMatchResult(build, self, allBlocks, facing);
        }
        return null;
    }
}
