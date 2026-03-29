package top.mrxiaom.sweet.buildmobs.builtin.mob;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.mrxiaom.sweet.buildmobs.api.IMobSpawner;
import top.mrxiaom.sweet.buildmobs.depend.mythic.IMythic;

public class MobMythicMobs implements IMobSpawner {
    private final @NotNull IMythic mythicApi;
    private final @NotNull String mythicId;
    private final double level;

    public MobMythicMobs(@NotNull IMythic mythicApi, @NotNull String mythicId, double level) {
        this.mythicApi = mythicApi;
        this.mythicId = mythicId;
        this.level = level;
    }

    @NotNull
    public String mythicId() {
        return mythicId;
    }

    public double level() {
        return level;
    }

    @Override
    public void spawn(@NotNull Location location) {
        World world = location.getWorld();
        if (location.getWorld() != null) {
            mythicApi.spawn(location, mythicId, level);
        }
    }

    public static class Provider implements IMobSpawner.Provider {
        private final @NotNull IMythic mythicApi;
        public Provider(@NotNull IMythic mythicApi) {
            this.mythicApi = mythicApi;
        }

        @Override
        public @Nullable IMobSpawner parse(@NotNull ConfigurationSection config) {
            if ("MythicMobs".equalsIgnoreCase(config.getString("type"))) {
                String mythicId = config.getString("mythic");
                double level = config.getDouble("level", 1.0);
                if (mythicId != null) {
                    return new MobMythicMobs(mythicApi, mythicId, level);
                }
            }
            return null;
        }
    }
}
