package top.mrxiaom.sweet.buildmobs.api;

import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IBlockDefine {

    boolean isMatch(Block block);

    interface Provider {
        default int priority() {
            return 1000;
        }
        @Nullable IBlockDefine parse(@NotNull ConfigurationSection config);
    }
}
