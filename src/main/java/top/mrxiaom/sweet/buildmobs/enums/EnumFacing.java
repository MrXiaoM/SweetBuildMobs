package top.mrxiaom.sweet.buildmobs.enums;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

/**
 * 方向轴置换枚举
 */
public enum EnumFacing {

    /*
     Minecraft Wiki 定义参考
     X 轴的正方向为东，其坐标反映了玩家距离原点在东（+）西（-）方向上的距离。在Java版中，调试屏幕中红色指标指向东方。
     Z 轴的正方向为南，其坐标反映了玩家距离原点在南（+）北（-）方向上的距离。在Java版中，调试屏幕中蓝色指标指向南方。
     Y 轴的正方向为上，其坐标反映了玩家位置的高低程度（其中海平面为63），另见海拔高度。在Java版中，调试屏幕中绿色指标指向上方。
     */

    /**
     * 面向北边时
     * <ul>
     *   <li><code>layer轴</code>的方向是<code>Z轴负方向</code></li>
     *   <li>则<code>本地X轴</code> 的方向为<code>X轴负方向</code>
     * </ul>
     */
    NORTH(Axis.Z_NEGATIVE, Axis.X_NEGATIVE, Axis.Y_POSITIVE, BlockFace.NORTH),
    /**
     * 面向西边时
     * <ul>
     *   <li><code>layer轴</code>的方向是<code>X轴负方向</code></li>
     *   <li>则<code>本地X轴</code> 的方向为<code>Z轴正方向</code>
     * </ul>
     */
    WEST(Axis.X_NEGATIVE, Axis.Z_POSITIVE, Axis.Y_POSITIVE, BlockFace.WEST),
    /**
     * 面向南边时
     * <ul>
     *   <li><code>layer轴</code>的方向是<code>Z轴正方向</code></li>
     *   <li>则<code>本地X轴</code> 的方向为<code>X轴正方向</code>
     * </ul>
     */
    SOUTH(Axis.Z_POSITIVE, Axis.X_POSITIVE, Axis.Y_POSITIVE, BlockFace.SOUTH),
    /**
     * 面向东边时
     * <ul>
     *   <li><code>layer轴</code>的方向是<code>X轴正方向</code></li>
     *   <li>则<code>本地X轴</code> 的方向为<code>Z轴负方向</code>
     * </ul>
     */
    EAST(Axis.X_POSITIVE, Axis.Z_NEGATIVE, Axis.Y_POSITIVE, BlockFace.EAST),

    ;
    private final Axis axisLayer, axisLocalX, axisLocalY;
    private final BlockFace bukkitFace;
    EnumFacing(Axis axisLayer, Axis axisLocalX, Axis axisLocalY, BlockFace bukkitFace) {
        this.axisLayer = axisLayer;
        this.axisLocalX = axisLocalX;
        this.axisLocalY = axisLocalY;
        this.bukkitFace = bukkitFace;
    }

    public BlockFace toBukkit() {
        return bukkitFace;
    }

    public Block getRelative(Block block, int layer, int localX, int localY) {
        int[] offset = toWorldOffset(layer, localX, localY);
        return block.getRelative(offset[0], offset[1], offset[2]);
    }

    public int[] toWorldOffset(int layer, int localX, int localY) {
        int[] offset = new int[3];
        offset[axisLayer.index] = layer * axisLayer.multiple;
        offset[axisLocalX.index] = localX * axisLocalX.multiple;
        offset[axisLocalY.index] = localY * axisLocalY.multiple;
        return offset;
    }

    private enum Axis {
        X_POSITIVE(0, 1),
        X_NEGATIVE(0, -1),
        Y_POSITIVE(1, 1),
        Y_NEGATIVE(1, -1),
        Z_POSITIVE(2, 1),
        Z_NEGATIVE(2, -1),
        ;
        private final int index;
        private final int multiple;
        Axis(int index, int multiple) {
            this.index = index;
            this.multiple = multiple;
        }
    }
}
