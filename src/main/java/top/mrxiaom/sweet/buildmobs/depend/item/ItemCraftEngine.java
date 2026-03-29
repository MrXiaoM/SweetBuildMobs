package top.mrxiaom.sweet.buildmobs.depend.item;

import net.momirealms.craftengine.bukkit.api.CraftEngineItems;
import net.momirealms.craftengine.core.item.CustomItem;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.mrxiaom.pluginbase.func.AutoRegister;
import top.mrxiaom.sweet.buildmobs.SweetBuildMobs;
import top.mrxiaom.sweet.buildmobs.api.ITriggerItem;
import top.mrxiaom.sweet.buildmobs.func.AbstractModule;

@AutoRegister(requirePlugins = {"CraftEngine"})
public class ItemCraftEngine extends AbstractModule implements ITriggerItem.Provider {
    public ItemCraftEngine(SweetBuildMobs plugin) {
        super(plugin);
        plugin.registerTriggerItem(this);
    }

    @Override
    public @Nullable ITriggerItem parse(@NotNull ConfigurationSection config) {
        if ("CraftEngine".equalsIgnoreCase(config.getString("type"))) {
            String id = config.getString("id");
            if (id != null) {
                return new Impl(id);
            }
        }
        return null;
    }

    @Override
    public @Nullable String key(@NotNull ItemStack item) {
        CustomItem<ItemStack> customItem = CraftEngineItems.byItemStack(item);
        if (customItem != null) {
            String id = ((Object) customItem.id()).toString();
            return "craftengine:" + id;
        }
        return null;
    }

    public static class Impl implements ITriggerItem {
        private final String id;
        private final String key;
        public Impl(String id) {
            this.id = id;
            this.key = "craftengine:" + id;
        }

        @Override
        public @NotNull String key() {
            return key;
        }

        @Override
        public boolean isMatch(@NotNull ItemStack item) {
            CustomItem<ItemStack> customItem = CraftEngineItems.byItemStack(item);
            if (customItem != null) {
                String id = ((Object) customItem.id()).toString();
                return this.id.equals(id);
            }
            return false;
        }
    }
}
