package top.mrxiaom.sweet.buildmobs.builtin.block;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.mrxiaom.pluginbase.utils.Util;
import top.mrxiaom.sweet.buildmobs.api.IBlockDefine;
import top.mrxiaom.sweet.buildmobs.enums.EnumFacing;
import top.mrxiaom.sweet.buildmobs.enums.EnumRelativeFacing;
import top.mrxiaom.sweet.buildmobs.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class BlockVanilla implements IBlockDefine {
    public static final IBlockDefine.Provider PROVIDER = new Provider();
    private final @NotNull Material material;
    private final @NotNull List<EnumRelativeFacing> relativeFacing;
    public BlockVanilla(@NotNull Material material, @NotNull List<EnumRelativeFacing> relativeFacing) {
        this.material = material;
        this.relativeFacing = relativeFacing;
    }

    @Override
    public boolean isMatch(Block block, EnumFacing facing) {
        if (!material.equals(block.getType())) {
            return false;
        }
        if (!relativeFacing.isEmpty()) {
            BlockFace direction = Utils.getDirection(block);
            if (direction != null) {
                boolean disallow = true;
                BlockFace targetFront = facing.toBukkit();
                for (EnumRelativeFacing relative : relativeFacing) {
                    if (relative.isMatch(direction, targetFront)) {
                        disallow = false;
                        break;
                    }
                }
                //noinspection RedundantIfStatement
                if (disallow) return false;
            }
        }
        return true;
    }

    public static class Provider implements IBlockDefine.Provider {
        private Provider() {}
        @Override
        public @Nullable IBlockDefine parse(@NotNull ConfigurationSection config) {
            if ("Vanilla".equalsIgnoreCase(config.getString("type"))) {
                Material material = Util.valueOrNull(Material.class, config.getString("material"));
                if (material == null) return null;
                List<EnumRelativeFacing> relativeFacings = new ArrayList<>();
                for (String s : config.getStringList("relative-facings")) {
                    EnumRelativeFacing facing = Util.valueOrNull(EnumRelativeFacing.class, s);
                    if (facing != null) {
                        relativeFacings.add(facing);
                    }
                }
                return new BlockVanilla(material, relativeFacings);
            }
            return null;
        }
    }
}
