package me.voper.slimeframe.implementation.items.tools;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;

import javax.annotation.Nonnull;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.EnergyNetComponent;
import io.github.thebusybiscuit.slimefun4.core.attributes.EnergyNetProvider;
import io.github.thebusybiscuit.slimefun4.core.networks.energy.EnergyNet;
import io.github.thebusybiscuit.slimefun4.libraries.dough.blocks.BlockPosition;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import io.github.thebusybiscuit.slimefun4.utils.HeadTexture;

import me.voper.slimeframe.implementation.items.abstracts.AbstractTickingContainer;
import me.voper.slimeframe.utils.Colors;
import me.voper.slimeframe.utils.Utils;

import lombok.Getter;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import net.md_5.bungee.api.ChatColor;

@Getter
public class EnergyCentral extends AbstractTickingContainer {

    private static final Map<BlockPosition, Integer> PROGRESS_MAP = new HashMap<>();
    private static final int TIME = 10;

    private static final DecimalFormat format = new DecimalFormat("###,###,###.##", DecimalFormatSymbols.getInstance(Locale.ROOT));
    private static final CustomItemStack capacitors = new CustomItemStack(HeadTexture.CAPACITOR_100.getAsItemStack(),
        ChatColor.BLUE + "电容");
    private static final CustomItemStack generators = new CustomItemStack(Material.DAYLIGHT_DETECTOR,
        ChatColor.GREEN + "发电机");
    private static final CustomItemStack consumers = new CustomItemStack(Material.FURNACE, ChatColor.RED + "机器");
    private static final CustomItemStack noEnergyNet = new CustomItemStack(Material.BARRIER, ChatColor.DARK_RED +
        "未找到能源网络");

    private final int generatorsSlot = 19;
    private final int capacitorsSlot = 22;
    private final int consumersSlot = 25;

    public EnergyCentral(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);
    }

    @Override
    protected void createMenu(@Nonnull BlockMenuPreset preset) {
        preset.drawBackground(new int[]{
                0, 1, 2, 3, 4, 5, 6, 7, 8,
                36, 37, 38, 39, 40, 41, 42, 43, 44
        });

        preset.drawBackground(new CustomItemStack(Material.GREEN_STAINED_GLASS_PANE, " "),
                new int[]{9, 10, 11, 18, 20, 27, 28, 29});
        preset.drawBackground(new CustomItemStack(Material.BLUE_STAINED_GLASS_PANE, " "),
                new int[]{12, 13, 14, 21, 23, 30, 31, 32});
        preset.drawBackground(new CustomItemStack(Material.RED_STAINED_GLASS_PANE, " "),
                new int[]{15, 16, 17, 24, 26, 33, 34, 35});

        preset.addItem(getCapacitorsSlot(), capacitors, ChestMenuUtils.getEmptyClickHandler());
        preset.addItem(getGeneratorsSlot(), generators, ChestMenuUtils.getEmptyClickHandler());
        preset.addItem(getConsumersSlot(), consumers, ChestMenuUtils.getEmptyClickHandler());
    }

    private int getConsumersSlot() {
		return consumersSlot;
	}

	private int getCapacitorsSlot() {
		return capacitorsSlot;
	}

	private int getGeneratorsSlot() {
		return generatorsSlot;
	}

	@Override
    protected void tick(BlockMenu menu, Block b) {
        final BlockPosition blockPosition = new BlockPosition(b);
        int progress = PROGRESS_MAP.getOrDefault(blockPosition, 0);

        if (progress >= TIME) {
            progress = 0;
            EnergyNet energyNet = EnergyNet.getNetworkFromLocation(menu.getLocation());

            if (energyNet == null) {
                menu.replaceExistingItem(getCapacitorsSlot(), noEnergyNet);
                menu.replaceExistingItem(getGeneratorsSlot(), noEnergyNet);
                menu.replaceExistingItem(getConsumersSlot(), noEnergyNet);
                return;
            }

            Map<Location, EnergyNetComponent> netCapacitors = energyNet.getCapacitors();
            Map<Location, EnergyNetProvider> netGenerators = energyNet.getGenerators();
            Map<Location, EnergyNetComponent> netConsumers = energyNet.getConsumers();

            List<EnergyNetComponent> allComponents = new ArrayList<>(netCapacitors.values());
            allComponents.addAll(netGenerators.values());
            allComponents.addAll(netConsumers.values());

            long capacitorsCapacity = netCapacitors.values().stream()
                    .mapToLong(EnergyNetComponent::getCapacity)
                    .sum();

            long totalCapacity = allComponents.stream()
                    .mapToLong(EnergyNetComponent::getCapacity)
                    .sum();

            long energyGenerated = netGenerators.entrySet().stream()
                    .mapToLong(entry -> entry.getValue().getGeneratedOutput(entry.getKey(), BlockStorage.getLocationInfo(entry.getKey())))
                    .map(operand -> Utils.energyPerTickToSeconds((int) operand))
                    .sum();

            menu.replaceExistingItem(getCapacitorsSlot(), new CustomItemStack(capacitors, (meta) -> {
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.AQUA + "累计：" + Colors.CRAYOLA_BLUE + netCapacitors.size());
                lore.add(ChatColor.AQUA + "来自电容的电容：" + Colors.CRAYOLA_BLUE + format.format(capacitorsCapacity) + " J");
                lore.add(ChatColor.AQUA + "累计电容：" + Colors.CRAYOLA_BLUE + format.format(totalCapacity) + " J");
                meta.setLore(lore);
            }));

            menu.replaceExistingItem(getGeneratorsSlot(), new CustomItemStack(generators, (meta) -> {
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.AQUA + "累计：" + Colors.CRAYOLA_BLUE + netGenerators.size());
                lore.add(ChatColor.AQUA + "能量生产：" + Colors.CRAYOLA_BLUE + format.format(energyGenerated) + " J/s");
                meta.setLore(lore);
            }));

            menu.replaceExistingItem(getConsumersSlot(), new CustomItemStack(consumers, (meta) -> {
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.AQUA + "累计：" + Colors.CRAYOLA_BLUE + netConsumers.size());
                meta.setLore(lore);
            }));

        } else {
            progress++;
        }

        PROGRESS_MAP.put(blockPosition, progress);
    }

    @Override
    public int getStatusSlot() {
        return 4;
    }

    @Nonnull
    @Override
    public int[] getInputSlots() {
        return new int[0];
    }

    @Nonnull
    @Override
    public int[] getOutputSlots() {
        return new int[0];
    }

}
