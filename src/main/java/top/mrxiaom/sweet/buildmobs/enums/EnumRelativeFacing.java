package top.mrxiaom.sweet.buildmobs.enums;

import org.bukkit.block.BlockFace;

/**
 * 相对朝向枚举
 */
public enum EnumRelativeFacing {
    /**
     * 前方
     */
    FRONT,
    /**
     * 后方
     */
    BACK,
    /**
     * 左方，即逆时针旋转90度
     */
    LEFT,
    /**
     * 右方，即顺时针旋转90度
     */
    RIGHT
    ;

    /**
     * 获取是否匹配相对朝向，仅支持 NORTH, WEST, SOUTH, EAST 方向，其它方向不作特殊处理
     * @param current 当前朝向
     * @param targetFront 目标的前方朝向
     */
    public boolean isMatch(BlockFace current, BlockFace targetFront) {
        switch (this) {
            case FRONT:
                return current.equals(targetFront);
            case BACK:
                return current.equals(targetFront.getOppositeFace());
            case LEFT:
                // 向量逆时针旋转 90 度
                return current.getModX() == -targetFront.getModZ()
                        && current.getModZ() == targetFront.getModX();
            case RIGHT:
                // 向量顺时针旋转 90 度
                return current.getModX() == targetFront.getModZ()
                        && current.getModZ() == -targetFront.getModX();
            default:
                break;
        }
        return false;
    }
}
