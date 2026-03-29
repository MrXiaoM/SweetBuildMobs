package top.mrxiaom.sweet.buildmobs.data.match;

import org.bukkit.World;
import top.mrxiaom.sweet.buildmobs.data.Build;
import top.mrxiaom.sweet.buildmobs.enums.EnumFacing;

import java.util.Collections;
import java.util.List;

public class BuildMatchResult {
    private final Build build;
    private final World world;
    private final MatchBlock clickedBlock;
    private final List<MatchBlock> allBlocks;
    private final EnumFacing checkFacing;

    public BuildMatchResult(Build build, MatchBlock clickedBlock, List<MatchBlock> allBlocks, EnumFacing checkFacing) {
        this.build = build;
        this.world = clickedBlock.block().getWorld();
        this.clickedBlock = clickedBlock;
        this.allBlocks = allBlocks;
        this.checkFacing = checkFacing;
    }

    /**
     * 获取构筑实例
     */
    public Build build() {
        return build;
    }

    /**
     * 获取解析的构筑所在的世界
     */
    public World world() {
        return world;
    }

    /**
     * 获取玩家点击的方块
     */
    public MatchBlock clickedBlock() {
        return clickedBlock;
    }

    /**
     * 获取解析构筑时是按哪个朝向来解析的
     */
    public EnumFacing checkFacing() {
        return checkFacing;
    }

    /**
     * 获取构筑已解析的所有方块列表
     */
    public List<MatchBlock> allBlocks() {
        return Collections.unmodifiableList(allBlocks);
    }
}
