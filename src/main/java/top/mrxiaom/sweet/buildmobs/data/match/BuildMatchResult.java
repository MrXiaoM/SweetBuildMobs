package top.mrxiaom.sweet.buildmobs.data.match;

import top.mrxiaom.sweet.buildmobs.data.Build;

import java.util.Collections;
import java.util.List;

public class BuildMatchResult {
    private final Build build;
    private final MatchBlock clickedBlock;
    private final List<MatchBlock> allBlocks;

    public BuildMatchResult(Build build, MatchBlock clickedBlock, List<MatchBlock> allBlocks) {
        this.build = build;
        this.clickedBlock = clickedBlock;
        this.allBlocks = allBlocks;
    }

    /**
     * 获取构筑实例
     */
    public Build build() {
        return build;
    }

    /**
     * 获取玩家点击的方块
     */
    public MatchBlock clickedBlock() {
        return clickedBlock;
    }

    /**
     * 获取构筑已解析的所有方块列表
     */
    public List<MatchBlock> allBlocks() {
        return Collections.unmodifiableList(allBlocks);
    }
}
