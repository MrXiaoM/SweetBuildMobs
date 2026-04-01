package top.mrxiaom.sweet.buildmobs;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
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
import top.mrxiaom.sweet.buildmobs.api.*;
import top.mrxiaom.sweet.buildmobs.builtin.block.BlockVanilla;
import top.mrxiaom.sweet.buildmobs.builtin.item.ItemVanilla;
import top.mrxiaom.sweet.buildmobs.builtin.loc.LocCenterBottom;
import top.mrxiaom.sweet.buildmobs.builtin.mob.MobNone;
import top.mrxiaom.sweet.buildmobs.builtin.mob.MobVanilla;

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
    private final List<IMobSpawner.Provider> mobSpawnerRegistry = new ArrayList<>();
    private final List<ILocProvider.Provider> locProviderRegistry = new ArrayList<>();
    private final List<IBlockProtectChecker> protectRegistry = new ArrayList<>();

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

    @Nullable
    @Contract("null -> null")
    public String parseBlockKey(@Nullable Block block) {
        if (block == null || block.getType().equals(Material.AIR)) {
            return null;
        }
        for (IBlockDefine.Provider provider : blockDefineRegistry) {
            String key = provider.key(block);
            if (key != null) {
                return key;
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
    @Contract("null -> null")
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

    public void registerMobSpawner(IMobSpawner.Provider provider) {
        this.mobSpawnerRegistry.add(provider);
        this.mobSpawnerRegistry.sort(Comparator.comparingInt(IMobSpawner.Provider::priority));
    }

    public void unregisterMobSpawner(IMobSpawner.Provider provider) {
        this.mobSpawnerRegistry.remove(provider);
        this.mobSpawnerRegistry.sort(Comparator.comparingInt(IMobSpawner.Provider::priority));
    }

    @Nullable
    public IMobSpawner parseMobSpawner(@Nullable ConfigurationSection config) {
        if (config == null) {
            return null;
        }
        for (IMobSpawner.Provider provider : mobSpawnerRegistry) {
            IMobSpawner result = provider.parse(config);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    public void registerLocProvider(ILocProvider.Provider provider) {
        this.locProviderRegistry.add(provider);
        this.locProviderRegistry.sort(Comparator.comparingInt(ILocProvider.Provider::priority));
    }

    public void unregisterLocProvider(ILocProvider.Provider provider) {
        this.locProviderRegistry.remove(provider);
        this.locProviderRegistry.sort(Comparator.comparingInt(ILocProvider.Provider::priority));
    }

    @Nullable
    public ILocProvider parseLocProvider(@Nullable ConfigurationSection config) {
        if (config == null) {
            return null;
        }
        for (ILocProvider.Provider provider : locProviderRegistry) {
            ILocProvider result = provider.parse(config);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    public void registerBlockProtectChecker(IBlockProtectChecker checker) {
        this.protectRegistry.add(checker);
        this.protectRegistry.sort(Comparator.comparingInt(IBlockProtectChecker::priority));
    }

    public void unregisterBlockProtectChecker(IBlockProtectChecker checker) {
        this.protectRegistry.remove(checker);
        this.protectRegistry.sort(Comparator.comparingInt(IBlockProtectChecker::priority));
    }

    public boolean isProtectedBlock(@NotNull Player player, @NotNull Block block) {
        // 检查方块是否在领地等保护区域内
        for (IBlockProtectChecker checker : protectRegistry) {
            if (checker.isProtected(player, block)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void beforeLoad() {
        registerBlockDefine(BlockVanilla.PROVIDER);

        registerTriggerItem(ItemVanilla.PROVIDER);

        registerMobSpawner(MobNone.PROVIDER);
        registerMobSpawner(MobVanilla.PROVIDER);

        registerLocProvider(LocCenterBottom.PROVIDER);
    }

    @Override
    protected void afterEnable() {
        getLogger().info("SweetBuildMobs 加载完毕");
    }
}
