package top.mrxiaom.sweet.buildmobs.api;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.mrxiaom.sweet.buildmobs.data.match.BuildMatchResult;

public interface ILocProvider {

    @NotNull Location read(@NotNull BuildMatchResult result);

    interface Provider {
        default int priority() {
            return 1000;
        }
        @Nullable ILocProvider parse(@NotNull ConfigurationSection config);
    }
}
