package top.mrxiaom.sweet.buildmobs.builtin.mob;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.mrxiaom.sweet.buildmobs.api.IMobSpawner;

public class MobNone implements IMobSpawner {
    public static final IMobSpawner INSTANCE = new MobNone();
    public static final IMobSpawner.Provider PROVIDER = new Provider();
    private MobNone() {}

    @Override
    public void spawn(@NotNull Location location) {
        // do nothing
    }

    public static class Provider implements IMobSpawner.Provider {
        private Provider() {}

        @Override
        public @Nullable IMobSpawner parse(@NotNull ConfigurationSection config) {
            if ("None".equalsIgnoreCase(config.getString("type"))) {
                return INSTANCE;
            }
            return null;
        }
    }
}
