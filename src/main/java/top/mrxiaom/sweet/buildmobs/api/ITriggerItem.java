package top.mrxiaom.sweet.buildmobs.api;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ITriggerItem {

    interface Provider {
        default int priority() {
            return 1000;
        }
        @Nullable ITriggerItem parse(@NotNull ConfigurationSection config);
    }
}
