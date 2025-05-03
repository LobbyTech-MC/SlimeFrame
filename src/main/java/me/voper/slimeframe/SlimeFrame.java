package me.voper.slimeframe;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;
import org.geysermc.geyser.api.GeyserApi;

import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import me.voper.slimeframe.core.datatypes.MerchantRecipeListDataType;
import me.voper.slimeframe.core.managers.CommandsManager;
import me.voper.slimeframe.core.managers.RelicInventoryManager;
import me.voper.slimeframe.core.managers.SettingsManager;
import me.voper.slimeframe.core.managers.SupportedPluginManager;
import me.voper.slimeframe.implementation.SFrameItems;
import me.voper.slimeframe.implementation.listeners.AdvancedMobDropListener;
import me.voper.slimeframe.implementation.listeners.BlockPlaceListener;
import me.voper.slimeframe.implementation.listeners.CoolantRaknoidsListener;
import me.voper.slimeframe.implementation.listeners.RelicInventoryListener;
import me.voper.slimeframe.implementation.listeners.RelicsListener;
import me.voper.slimeframe.implementation.listeners.VillagerTradeListener;
import me.voper.slimeframe.implementation.researches.Researches;
import me.voper.slimeframe.implementation.tasks.ArmorMonitorTask;
import me.voper.slimeframe.implementation.tasks.CoolantRaknoidsTask;
import net.guizhanss.guizhanlib.updater.UpdaterConfig;
import net.guizhanss.guizhanlibplugin.updater.GuizhanUpdater;

public class SlimeFrame extends JavaPlugin implements SlimefunAddon {

    private static SlimeFrame instance;

    private SettingsManager settingsManager;
    private RelicInventoryManager relicInventoryManager;
    private CommandsManager commandsManager;
    private SupportedPluginManager supportedPluginManager;
    private GeyserApi geyserApi;

    private final Set<SlimefunItem> freezingItems = new HashSet<>();
    private final Set<Block> placedBlocks = new HashSet<>();

    static {
        ConfigurationSerialization.registerClass(MerchantRecipeListDataType.SerializableSlimefunItemStack.class);
    }

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        if (!getServer().getPluginManager().isPluginEnabled("GuizhanLibPlugin")) {
            getLogger().log(Level.SEVERE, "本插件需要 鬼斩前置库插件(GuizhanLibPlugin) 才能运行!");
            getLogger().log(Level.SEVERE, "从此处下载: https://50l.cc/gzlib");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        new Metrics(this, 19212);

        this.supportedPluginManager = new SupportedPluginManager();

        if (supportedPluginManager.isGeyser()) {
            geyserApi = GeyserApi.api();
        }

        this.settingsManager = new SettingsManager(SlimeFrame.getInstance());
        this.relicInventoryManager = new RelicInventoryManager(SlimeFrame.getInstance());
        this.commandsManager = new CommandsManager();

        this.relicInventoryManager.setup();
        this.commandsManager.setup();

        new SFrameItems(SlimeFrame.getInstance()).setup();

        Researches.setup();

        this.setupEvents();
        this.startTasks();
        this.logStart();

        if (getPluginVersion().startsWith("Build")) {
            UpdaterConfig updaterConfig =
                UpdaterConfig.builder().checkOnly(!settingsManager.getBoolean(SettingsManager.ConfigField.AUTO_UPDATE)).build();
            GuizhanUpdater.start(this, getFile(), "SlimefunGuguProject", "SlimeFrame", "main", updaterConfig);
        }
    }

    @Override
    public void onDisable() {
    	if (relicInventoryManager != null) {
    		relicInventoryManager.saveConfig();
    	}
        Bukkit.getScheduler().cancelTasks(SlimeFrame.getInstance());
    }

    private void logStart() {
        getLogger().info("   _____ _ _                ______                        ");
        getLogger().info("  / ____| (_)              |  ____|                       ");
        getLogger().info(" | (___ | |_ _ __ ___   ___| |__ _ __ __ _ _ __ ___   ___ ");
        getLogger().info("  \\___ \\| | | '_ ` _ \\ / _ \\  __| '__/ _` | '_ ` _ \\ / _ \\");
        getLogger().info("  ____) | | | | | | | |  __/ |  | | | (_| | | | | | |  __/");
        getLogger().info(" |_____/|_|_|_| |_| |_|\\___|_|  |_|  \\__,_|_| |_| |_|\\___|");
        getLogger().info("==========================================================");
    }

    @Override
    public String getBugTrackerURL() {
        return "https://github.com/SlimefunGuguProject/SlimeFrame/issues";
    }

    @Nonnull
    @Override
    public JavaPlugin getJavaPlugin() {
        return this;
    }

    private void setupEvents() {
        new BlockPlaceListener(this);
        new RelicsListener(this);
        new RelicInventoryListener(this);
        new CoolantRaknoidsListener(this);
        new AdvancedMobDropListener(this);
        new VillagerTradeListener(this);
    }

    private void startTasks() {
        new ArmorMonitorTask(this);
        new CoolantRaknoidsTask(this);
    }

    public static SlimeFrame getInstance() {
        return instance;
    }

    public static SettingsManager getSettingsManager() {
        return instance.settingsManager;
    }

    public static RelicInventoryManager getRelicInventoryManager() {
        return instance.relicInventoryManager;
    }

    public static @Nullable GeyserApi getGeyserApi() {
        return instance.supportedPluginManager.isGeyser() ? instance.geyserApi : null;
    }

    public static SupportedPluginManager getSupportedPluginManager() {
        return instance.supportedPluginManager;
    }

    public static Set<SlimefunItem> getFreezingItems() {
        return instance.freezingItems;
    }

    public static Set<Block> getPlacedBlocks() {
        return instance.placedBlocks;
    }

}
