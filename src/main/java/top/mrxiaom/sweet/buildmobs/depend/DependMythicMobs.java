package top.mrxiaom.sweet.buildmobs.depend;

import top.mrxiaom.pluginbase.func.AutoRegister;
import top.mrxiaom.pluginbase.utils.Util;
import top.mrxiaom.sweet.buildmobs.SweetBuildMobs;
import top.mrxiaom.sweet.buildmobs.builtin.item.ItemMythicMobs;
import top.mrxiaom.sweet.buildmobs.depend.mythic.IMythic;
import top.mrxiaom.sweet.buildmobs.depend.mythic.Mythic4;
import top.mrxiaom.sweet.buildmobs.depend.mythic.Mythic5;
import top.mrxiaom.sweet.buildmobs.func.AbstractModule;

@AutoRegister(requirePlugins = {"MythicMobs"})
public class DependMythicMobs extends AbstractModule {
    private IMythic mythicApi;
    public DependMythicMobs(SweetBuildMobs plugin) {
        super(plugin);
        if (Util.isPresent("io.lumine.mythic.bukkit.MythicBukkit")) {
            mythicApi = new Mythic5();
        } else if (Util.isPresent("io.lumine.xikage.mythicmobs.MythicMobs")) {
            mythicApi = new Mythic4();
        }
        if (mythicApi != null) {
            plugin.registerTriggerItem(new ItemMythicMobs.Provider(mythicApi));
            info("已挂钩 MythicMobs");
        }
    }
}
