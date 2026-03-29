package top.mrxiaom.sweet.buildmobs.builtin.item;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.mrxiaom.sweet.buildmobs.api.ITriggerItem;
import top.mrxiaom.sweet.buildmobs.depend.mythic.IMythic;

public class ItemMythicMobs implements ITriggerItem {
    private final @NotNull IMythic mythicApi;
    private final @NotNull String mythicId;
    private final @NotNull String key;
    public ItemMythicMobs(@NotNull IMythic mythicApi, @NotNull String mythicId) {
        this.mythicApi = mythicApi;
        this.mythicId = mythicId;
        this.key = "mythic:" + mythicId;
    }

    @Override
    public @NotNull String key() {
        return key;
    }

    public @NotNull String mythicId() {
        return mythicId;
    }

    @Override
    public boolean isMatch(@NotNull ItemStack item) {
        String mythicId = mythicApi.getMythicId(item);
        if (mythicId != null) {
            return key.equals(mythicId);
        }
        return false;
    }

    public static class Provider implements ITriggerItem.Provider {
        private final @NotNull IMythic mythicApi;
        public Provider(@NotNull IMythic mythicApi) {
            this.mythicApi = mythicApi;
        }

        @Override
        public @Nullable ITriggerItem parse(@NotNull ConfigurationSection config) {
            if ("MythicMobs".equalsIgnoreCase(config.getString("type"))) {
                String mythicId = config.getString("mythic");
                if (mythicId != null) {
                    return new ItemMythicMobs(mythicApi, mythicId);
                }
            }
            return null;
        }

        @Override
        public @Nullable String key(@NotNull ItemStack item) {
            String mythicId = mythicApi.getMythicId(item);
            if (mythicId != null) {
                return "mythic:" + mythicId;
            }
            return null;
        }
    }
}
