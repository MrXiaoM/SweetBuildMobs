package top.mrxiaom.sweet.buildmobs.utils;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.material.MaterialData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Utils {

    /**
     * 获取方块朝向
     * @return 如果方块没有朝向属性，返回 <code>null</code>
     */
    @Nullable
    public static BlockFace getDirection(@NotNull Block block) {
        try {
            // 1.13+
            BlockData data = block.getBlockData();
            if (data instanceof org.bukkit.block.data.Directional) {
                return ((org.bukkit.block.data.Directional) data).getFacing();
            }
        } catch (LinkageError ignored) {
            // noinspection deprecation: 1.8-1.12
            MaterialData data = block.getType().getNewData(block.getData());
            if (data instanceof org.bukkit.material.Directional) {
                return ((org.bukkit.material.Directional) data).getFacing();
            }
        }
        return null;
    }

}
