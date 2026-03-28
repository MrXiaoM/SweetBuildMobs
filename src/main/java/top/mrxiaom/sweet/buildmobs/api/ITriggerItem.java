package top.mrxiaom.sweet.buildmobs.api;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ITriggerItem {

    @NotNull String key();

    interface Provider {
        default int priority() {
            return 1000;
        }
        @Nullable ITriggerItem parse(@NotNull ConfigurationSection config);
        @Nullable String key(@NotNull ItemStack item);
    }
}
