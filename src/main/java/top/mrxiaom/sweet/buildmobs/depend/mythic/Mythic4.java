package top.mrxiaom.sweet.buildmobs.depend.mythic;

import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.adapters.AbstractLocation;
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter;
import io.lumine.xikage.mythicmobs.mobs.entities.SpawnReason;
import io.lumine.xikage.mythicmobs.util.jnbt.CompoundTag;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class Mythic4 implements IMythic {
    final MythicMobs mythic = MythicMobs.inst();
    @Override
    public @Nullable String getMythicId(@Nullable ItemStack item) {
        if (item == null || item.getType().equals(Material.AIR) || item.getAmount() <= 0) {
            return null;
        }
        CompoundTag data = mythic.getVolatileCodeHandler().getItemHandler().getNBTData(item);
        if (data != null && data.containsKey("MYTHIC_TYPE")) {
            return data.getString("MYTHIC_TYPE");
        }
        return null;
    }

    @Override
    public void spawn(Location loc, String mythicId, double level) {
        AbstractLocation location = BukkitAdapter.adapt(loc);
        mythic.getMobManager().spawnMob(mythicId, location, SpawnReason.SUMMON, level);
    }
}
