package top.mrxiaom.sweet.buildmobs.depend.item;

import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.mrxiaom.pluginbase.func.AutoRegister;
import top.mrxiaom.sweet.buildmobs.SweetBuildMobs;
import top.mrxiaom.sweet.buildmobs.api.ITriggerItem;
import top.mrxiaom.sweet.buildmobs.func.AbstractModule;

@AutoRegister(requirePlugins = {"ItemsAdder"})
public class ItemItemsAdder extends AbstractModule implements ITriggerItem.Provider {
    public ItemItemsAdder(SweetBuildMobs plugin) {
        super(plugin);
        plugin.registerTriggerItem(this);
    }

    @Override
    public @Nullable ITriggerItem parse(@NotNull ConfigurationSection config) {
        if ("ItemsAdder".equalsIgnoreCase(config.getString("type"))) {
            String id = config.getString("id");
            if (id != null) {
                return new Impl(id);
            }
        }
        return null;
    }

    @Override
    public @Nullable String key(@NotNull ItemStack item) {
        CustomStack customStack = CustomStack.byItemStack(item);
        if (customStack != null) {
            String id = customStack.getNamespacedID();
            return "itemsadder:" + id;
        }
        return null;
    }

    public static class Impl implements ITriggerItem {
        private final String id;
        private final String key;
        public Impl(String id) {
            this.id = id;
            this.key = "itemsadder:" + id;
        }

        @Override
        public @NotNull String key() {
            return key;
        }

        @Override
        public boolean isMatch(@NotNull ItemStack item) {
            CustomStack customStack = CustomStack.byItemStack(item);
            if (customStack != null) {
                String id = customStack.getNamespacedID();
                return this.id.equals(id);
            }
            return false;
        }
    }
}
