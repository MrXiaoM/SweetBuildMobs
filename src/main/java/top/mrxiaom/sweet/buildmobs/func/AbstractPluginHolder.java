package top.mrxiaom.sweet.buildmobs.func;

import top.mrxiaom.sweet.buildmobs.SweetBuildMobs;

@SuppressWarnings({"unused"})
public abstract class AbstractPluginHolder extends top.mrxiaom.pluginbase.func.AbstractPluginHolder<SweetBuildMobs> {
    public AbstractPluginHolder(SweetBuildMobs plugin) {
        super(plugin);
    }

    public AbstractPluginHolder(SweetBuildMobs plugin, boolean register) {
        super(plugin, register);
    }
}
