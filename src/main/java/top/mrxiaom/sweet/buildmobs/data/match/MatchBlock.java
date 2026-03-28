package top.mrxiaom.sweet.buildmobs.data.match;

import org.bukkit.block.Block;
import top.mrxiaom.sweet.buildmobs.data.Build;
import top.mrxiaom.sweet.buildmobs.data.LayerBlock;

public class MatchBlock {
    private final Build build;
    private final LayerBlock layerBlock;
    private final Block block;

    public MatchBlock(LayerBlock layerBlock, Block block) {
        this.build = layerBlock.build();
        this.layerBlock = layerBlock;
        this.block = block;
    }

    /**
     * 获取构筑实例
     */
    public Build build() {
        return build;
    }

    /**
     * 获取层级方块配置
     */
    public LayerBlock layerBlock() {
        return layerBlock;
    }

    /**
     * 获取方块坐标信息
     */
    public Block block() {
        return block;
    }
}
