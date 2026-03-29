package top.mrxiaom.sweet.buildmobs.builtin.mob;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.mrxiaom.pluginbase.utils.Util;
import top.mrxiaom.sweet.buildmobs.api.IMobSpawner;

public class MobVanilla implements IMobSpawner {
    public static final IMobSpawner.Provider PROVIDER = new Provider();
    private final EntityType type;
    public MobVanilla(EntityType type) {
        this.type = type;
    }

    public EntityType type() {
        return type;
    }

    @Override
    public void spawn(@NotNull Location location) {
        World world = location.getWorld();
        if (world != null) {
            world.spawnEntity(location, type);
        }
    }

    public static class Provider implements IMobSpawner.Provider {
        private Provider() {}

        @Override
        public @Nullable IMobSpawner parse(@NotNull ConfigurationSection config) {
            if ("Vanilla".equalsIgnoreCase(config.getString("type"))) {
                EntityType type = Util.valueOrNull(EntityType.class, config.getString("entity-type"));
                if (type != null) {
                    return new MobVanilla(type);
                }
            }
            return null;
        }
    }
}
