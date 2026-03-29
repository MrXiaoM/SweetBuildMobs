package top.mrxiaom.sweet.buildmobs.depend.block;

import dev.lone.itemsadder.api.CustomBlock;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.mrxiaom.pluginbase.func.AutoRegister;
import top.mrxiaom.sweet.buildmobs.SweetBuildMobs;
import top.mrxiaom.sweet.buildmobs.api.IBlockDefine;
import top.mrxiaom.sweet.buildmobs.enums.EnumFacing;
import top.mrxiaom.sweet.buildmobs.func.AbstractModule;

@AutoRegister(requirePlugins = {"ItemsAdder"})
public class BlockItemsAdder extends AbstractModule implements IBlockDefine.Provider {
    public BlockItemsAdder(SweetBuildMobs plugin) {
        super(plugin);
        plugin.registerBlockDefine(this);
        info("已挂钩 ItemsAdder");
    }

    @Override
    public @Nullable IBlockDefine parse(@NotNull ConfigurationSection config) {
        if ("ItemsAdder".equalsIgnoreCase(config.getString("type"))) {
            String id = config.getString("id");
            if (id != null) {
                return new Impl(id);
            }
        }
        return null;
    }

    @Override
    public @Nullable String key(@NotNull Block block) {
        CustomBlock state = CustomBlock.byAlreadyPlaced(block);
        if (state != null) {
            return "itemsadder:" + state.getNamespacedID();
        }
        return null;
    }

    public static class Impl implements IBlockDefine {
        private final String id;
        private final String key;
        public Impl(String id) {
            this.id = id;
            this.key = "itemsadder:" + id;
        }

        public String id() {
            return id;
        }

        @Override
        public @NotNull String key() {
            return key;
        }

        @Override
        public boolean isMatch(Block block, EnumFacing facing) {
            CustomBlock state = CustomBlock.byAlreadyPlaced(block);
            if (state != null) {
                return id.equals(state.getNamespacedID());
            }
            return false;
        }
    }
}
