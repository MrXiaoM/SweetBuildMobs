package top.mrxiaom.sweet.buildmobs.builtin.item;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.mrxiaom.pluginbase.utils.Util;
import top.mrxiaom.sweet.buildmobs.api.ITriggerItem;

public class ItemVanilla implements ITriggerItem {
    public static final ITriggerItem.Provider PROVIDER = new Provider();
    private final @NotNull Material material;
    private final @NotNull String key;
    public ItemVanilla(@NotNull Material material) {
        this.material = material;
        this.key = Provider.key(material);
    }

    @Override
    public @NotNull String key() {
        return key;
    }

    public @NotNull Material material() {
        return material;
    }

    @Override
    public boolean isMatch(@NotNull ItemStack item) {
        return material.equals(item.getType());
    }

    public static class Provider implements ITriggerItem.Provider {
        private Provider() {}
        @Override
        public @Nullable ITriggerItem parse(@NotNull ConfigurationSection config) {
            if ("Vanilla".equalsIgnoreCase(config.getString("type"))) {
                Material material = Util.valueOrNull(Material.class, config.getString("material"));
                if (material == null) return null;
                return new ItemVanilla(material);
            }
            return null;
        }

        @Override
        public @Nullable String key(@NotNull ItemStack item) {
            return key(item.getType());
        }

        @NotNull
        public static String key(@NotNull Material material) {
            return "minecraft:" + material.name().toLowerCase();
        }
    }
}
