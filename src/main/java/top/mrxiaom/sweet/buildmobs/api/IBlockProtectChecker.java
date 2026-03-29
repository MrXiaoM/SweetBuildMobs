package top.mrxiaom.sweet.buildmobs.api;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public interface IBlockProtectChecker {
    default int priority() {
        return 1000;
    }

    boolean isProtected(Player player, Block block);
}
