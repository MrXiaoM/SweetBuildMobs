package top.mrxiaom.sweet.buildmobs.api;

import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.mrxiaom.sweet.buildmobs.enums.EnumFacing;

public interface IBlockDefine {

    @NotNull String key();
    boolean isMatch(Block block, EnumFacing facing);

    interface Provider {
        default int priority() {
            return 1000;
        }
        @Nullable IBlockDefine parse(@NotNull ConfigurationSection config);
        @Nullable String key(@NotNull Block block);
    }
}
