package top.mrxiaom.sweet.buildmobs.api;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IMobSpawner {

    void spawn(@NotNull Location location);

    interface Provider {
        default int priority() {
            return 1000;
        }
        @Nullable IMobSpawner parse(@NotNull ConfigurationSection config);
    }
}
