package top.mrxiaom.sweet.buildmobs.depend.mythic;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface IMythic {
    @Nullable String getMythicId(@Nullable ItemStack item);
}
