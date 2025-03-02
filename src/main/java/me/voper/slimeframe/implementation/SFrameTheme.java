package me.voper.slimeframe.implementation;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import lombok.Getter;
import lombok.With;
import net.md_5.bungee.api.ChatColor;

@Getter
@ParametersAreNonnullByDefault
public class SFrameTheme {

    public static final ChatColor DEFAULT_LORE_COLOR = ChatColor.of("#9da8a3");

    @With
    private ChatColor nameColor;
    private final ChatColor loreColor;
    private final String lastLine;

    public SFrameTheme(ChatColor nameColor) {
        this(nameColor, DEFAULT_LORE_COLOR, " ");
    }

    public SFrameTheme(ChatColor nameColor, String lastLine) {
        this(nameColor, DEFAULT_LORE_COLOR, lastLine);
    }

    public SFrameTheme(ChatColor nameColor, ChatColor loreColor, String lastLine) {
		this.nameColor = nameColor;
		this.loreColor = loreColor;
		this.lastLine = lastLine;
	}

	@Nonnull
    public static SlimefunItemStack sfStackFromTheme(String id, ItemStack itemStack, SFrameTheme theme, String name, String... lore) {
        List<String> coloredLore = new ArrayList<>();
        for (String s : lore) {
            ChatColor.translateAlternateColorCodes('&', s);
            coloredLore.add(theme.loreColor + s);
        }
        coloredLore.add(0, " ");
        if (!theme.lastLine.equals(" ")) coloredLore.add(" ");
        coloredLore.add(theme.lastLine);
        return new SlimefunItemStack(id, itemStack, theme.nameColor + name, coloredLore.toArray(new String[0]));
    }

    @Nonnull
    public static SlimefunItemStack sfStackFromTheme(String id, String texture, SFrameTheme theme, String name, String... lore) {
        List<String> coloredLore = new ArrayList<>();
        for (String s : lore) {
            ChatColor.translateAlternateColorCodes('&', s);
            coloredLore.add(theme.loreColor + s);
        }
        coloredLore.add(0, " ");
        if (!theme.lastLine.equals(" ")) coloredLore.add(" ");
        coloredLore.add(theme.lastLine);
        return new SlimefunItemStack(id, texture, theme.nameColor + name, coloredLore.toArray(new String[0]));
    }

    @Nonnull
    public static SlimefunItemStack sfStackFromTheme(String id, Material material, SFrameTheme theme, String name, String... lore) {
        return sfStackFromTheme(id, new ItemStack(material), theme, name, lore);
    }

	public SFrameTheme withNameColor(ChatColor nameColor) {
		this.nameColor = nameColor;
		return this;
	}

	public ChatColor getLoreColor() {
		return loreColor;
	}

	public ChatColor getNameColor() {
		return nameColor;
	}
}
