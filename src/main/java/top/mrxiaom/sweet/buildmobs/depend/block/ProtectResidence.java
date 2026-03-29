package top.mrxiaom.sweet.buildmobs.depend.block;

import com.bekvon.bukkit.residence.containers.Flags;
import com.bekvon.bukkit.residence.protection.FlagPermissions;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import top.mrxiaom.pluginbase.func.AutoRegister;
import top.mrxiaom.sweet.buildmobs.SweetBuildMobs;
import top.mrxiaom.sweet.buildmobs.api.IBlockProtectChecker;
import top.mrxiaom.sweet.buildmobs.func.AbstractModule;

@AutoRegister(requirePlugins = {"Residence"})
public class ProtectResidence extends AbstractModule implements IBlockProtectChecker {
    public ProtectResidence(SweetBuildMobs plugin) {
        super(plugin);
        plugin.registerBlockProtectChecker(this);
        info("已挂钩 Residence");
    }

    @Override
    public boolean isProtected(Player player, Block block) {
        FlagPermissions perms = FlagPermissions.getPerms(block.getLocation(), player);
        return !perms.playerHas(player, Flags.place, perms.playerHas(player, Flags.build, true));
    }
}
