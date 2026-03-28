package top.mrxiaom.sweet.buildmobs;

import de.tr7zw.changeme.nbtapi.utils.MinecraftVersion;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import top.mrxiaom.pluginbase.BukkitPlugin;
import top.mrxiaom.pluginbase.paper.PaperFactory;
import top.mrxiaom.pluginbase.utils.inventory.InventoryFactory;
import top.mrxiaom.pluginbase.utils.item.ItemEditor;
import top.mrxiaom.pluginbase.utils.scheduler.FoliaLibScheduler;
import top.mrxiaom.pluginbase.utils.ClassLoaderWrapper;
import top.mrxiaom.pluginbase.utils.ConfigUtils;
import top.mrxiaom.pluginbase.resolver.DefaultLibraryResolver;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import top.mrxiaom.sweet.buildmobs.api.IBlockDefine;
import top.mrxiaom.sweet.buildmobs.api.ITriggerItem;

public class SweetBuildMobs extends BukkitPlugin {
    public static SweetBuildMobs getInstance() {
        return (SweetBuildMobs) BukkitPlugin.getInstance();
    }
    public SweetBuildMobs() throws Exception {
        super(options()
                .bungee(false)
                .adventure(true)
                .database(false)
                .reconnectDatabaseWhenReloadConfig(false)
                .scanIgnore("top.mrxiaom.sweet.buildmobs.libs")
        );
        this.scheduler = new FoliaLibScheduler(this);

        try {
            //noinspection ResultOfMethodCallIgnored
            getDescription().getLibraries();
        } catch (LinkageError ignored) {
            info("正在检查依赖库状态");
            File librariesDir = ClassLoaderWrapper.isSupportLibraryLoader
                    ? new File("libraries")
                    : new File(this.getDataFolder(), "libraries");
            DefaultLibraryResolver resolver = new DefaultLibraryResolver(getLogger(), librariesDir);

            YamlConfiguration overrideLibraries = ConfigUtils.load(resolve("./.override-libraries.yml"));
            for (String key : overrideLibraries.getKeys(false)) {
                resolver.getStartsReplacer().put(key, overrideLibraries.getString(key));
            }
            resolver.addResolvedLibrary(BuildConstants.RESOLVED_LIBRARIES);

            List<URL> libraries = resolver.doResolve();
            info("正在添加 " + libraries.size() + " 个依赖库到类加载器");
            for (URL library : libraries) {
                this.classLoader.addURL(library);
            }
        }
    }

    @Override
    public @NotNull ItemEditor initItemEditor() {
        return PaperFactory.createItemEditor();
    }

    @Override
    public @NotNull InventoryFactory initInventoryFactory() {
        return PaperFactory.createInventoryFactory();
    }

    private final List<IBlockDefine.Provider> blockDefineRegistry = new ArrayList<>();
    private final List<ITriggerItem.Provider> triggerItemRegistry = new ArrayList<>();

    public void registerBlockDefine(IBlockDefine.Provider provider) {
        this.blockDefineRegistry.add(provider);
        this.blockDefineRegistry.sort(Comparator.comparingInt(IBlockDefine.Provider::priority));
    }

    public void unregisterBlockDefine(IBlockDefine.Provider provider) {
        this.blockDefineRegistry.remove(provider);
        this.blockDefineRegistry.sort(Comparator.comparingInt(IBlockDefine.Provider::priority));
    }

    @Nullable
    public IBlockDefine parseBlockDefine(@Nullable ConfigurationSection config) {
        if (config == null) {
            return null;
        }
        for (IBlockDefine.Provider provider : blockDefineRegistry) {
            IBlockDefine result = provider.parse(config);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    public void registerTriggerItem(ITriggerItem.Provider provider) {
        this.triggerItemRegistry.add(provider);
        this.triggerItemRegistry.sort(Comparator.comparingInt(ITriggerItem.Provider::priority));
    }

    public void unregisterTriggerItem(ITriggerItem.Provider provider) {
        this.triggerItemRegistry.remove(provider);
        this.triggerItemRegistry.sort(Comparator.comparingInt(ITriggerItem.Provider::priority));
    }

    @Nullable
    public ITriggerItem parseTriggerItem(@Nullable ConfigurationSection config) {
        if (config == null) {
            return null;
        }
        for (ITriggerItem.Provider provider : triggerItemRegistry) {
            ITriggerItem result = provider.parse(config);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    @Nullable
    public String parseItemKey(@Nullable ItemStack item) {
        if (item == null || item.getType().equals(Material.AIR) || item.getAmount() <= 0) {
            return null;
        }
        for (ITriggerItem.Provider provider : triggerItemRegistry) {
            String key = provider.key(item);
            if (key != null) {
                return key;
            }
        }
        return null;
    }

    @Override
    protected void beforeLoad() {
        MinecraftVersion.replaceLogger(getLogger());
        MinecraftVersion.disableUpdateCheck();
        MinecraftVersion.disableBStats();
        MinecraftVersion.getVersion();
    }

    @Override
    protected void afterEnable() {
        getLogger().info("SweetBuildMobs 加载完毕");
    }
}
