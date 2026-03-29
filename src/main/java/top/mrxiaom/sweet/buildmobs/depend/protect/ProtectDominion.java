package top.mrxiaom.sweet.buildmobs.depend.protect;

import cn.lunadeer.dominion.api.DominionAPI;
import cn.lunadeer.dominion.api.dtos.flag.Flags;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import top.mrxiaom.pluginbase.func.AutoRegister;
import top.mrxiaom.sweet.buildmobs.SweetBuildMobs;
import top.mrxiaom.sweet.buildmobs.api.IBlockProtectChecker;
import top.mrxiaom.sweet.buildmobs.func.AbstractModule;

@AutoRegister(requirePlugins = {"Dominion"})
public class ProtectDominion extends AbstractModule implements IBlockProtectChecker {
    DominionAPI api = DominionAPI.getInstance();
    public ProtectDominion(SweetBuildMobs plugin) {
        super(plugin);
        plugin.registerBlockProtectChecker(this);
        info("已挂钩 Dominion");
    }

    @Override
    public boolean isProtected(Player player, Block block) {
        return !api.checkPrivilegeFlagSilence(block.getLocation(), Flags.PLACE, player);
    }
}
