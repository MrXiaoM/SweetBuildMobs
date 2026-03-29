package top.mrxiaom.sweet.buildmobs.depend.block;

import net.momirealms.craftengine.bukkit.api.CraftEngineBlocks;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.mrxiaom.pluginbase.func.AutoRegister;
import top.mrxiaom.sweet.buildmobs.SweetBuildMobs;
import top.mrxiaom.sweet.buildmobs.api.IBlockDefine;
import top.mrxiaom.sweet.buildmobs.enums.EnumFacing;
import top.mrxiaom.sweet.buildmobs.func.AbstractModule;

@AutoRegister(requirePlugins = {"CraftEngine"})
public class BlockCraftEngine extends AbstractModule implements IBlockDefine.Provider {
    public BlockCraftEngine(SweetBuildMobs plugin) {
        super(plugin);
        plugin.registerBlockDefine(this);
        info("已挂钩 CraftEngine");
    }

    @Override
    public @Nullable IBlockDefine parse(@NotNull ConfigurationSection config) {
        if ("CraftEngine".equalsIgnoreCase(config.getString("type"))) {
            String id = config.getString("id");
            if (id != null) {
                return new Impl(id);
            }
        }
        return null;
    }

    public static class Impl implements IBlockDefine {
        private final String id;
        public Impl(String id) {
            this.id = id;
        }

        public String id() {
            return id;
        }

        @Override
        public boolean isMatch(Block block, EnumFacing facing) {
            ImmutableBlockState state = CraftEngineBlocks.getCustomBlockState(block);
            if (state != null) {
                String blockId = ((Object) state.owner().value().id()).toString();
                return id.equals(blockId);
            }
            return false;
        }
    }
}
