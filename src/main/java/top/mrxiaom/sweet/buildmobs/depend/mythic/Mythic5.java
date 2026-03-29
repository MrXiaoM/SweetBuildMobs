package top.mrxiaom.sweet.buildmobs.depend.mythic;

import io.lumine.mythic.bukkit.MythicBukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class Mythic5 implements IMythic {
    final MythicBukkit mythic = MythicBukkit.inst();
    @Override
    public @Nullable String getMythicId(@Nullable ItemStack item) {
        if (item == null || item.getType().equals(Material.AIR) || item.getAmount() <= 0) {
            return null;
        }
        return mythic.getItemManager().getMythicTypeFromItem(item);
    }
}
