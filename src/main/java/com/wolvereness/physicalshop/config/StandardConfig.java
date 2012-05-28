package com.wolvereness.physicalshop.config;

import static com.wolvereness.physicalshop.config.ConfigOptions.*;

import java.util.regex.Pattern;

import org.bukkit.plugin.Plugin;

import com.wolvereness.physicalshop.showcase.PlayerHandler;

/**
 * @author Wolfe
 * Licensed under GNU GPL v3
 */
public class StandardConfig {
	private final PatternHandler buyPattern;
	private final Pattern materialPattern;
	private final Plugin plugin;
	private final PatternHandler sellPattern;
	/**
	 * makes a new standard config, loading up defaults
	 * @param plugin Used to get the config
	 */
	public StandardConfig(final Plugin plugin) {
		this.plugin = plugin;
		buyPattern = new PatternHandler(plugin.getConfig().getConfigurationSection(BUY_SECTION));
		materialPattern = Pattern.compile(plugin.getConfig().getString(MATERIAL_PATTERN));
		sellPattern = new PatternHandler(plugin.getConfig().getConfigurationSection(SELL_SECTION));
		if(!plugin.getConfig().isConfigurationSection(CURRENCIES)) {
			plugin.getConfig().createSection(CURRENCIES).set("g", "Gold Ingot");
		}
	}
	/**
	 * Pattern for 'buy-from-shop' (second line on signs).
	 *
	 * @return the pattern handler for the Buy line
	 */
	public PatternHandler getBuyPatternHandler() {
		return buyPattern;
	}
	/**
	 * Pattern for material match (first line on signs)
	 *
	 * @return the pattern matching the Material line
	 */
	public Pattern getMaterialPattern() {
		return materialPattern;
	}
	/**
	 * Pattern for 'sell-to-shop' (third line on signs).
	 *
	 * @return the pattern handler for the Sell line
	 */
	public PatternHandler getSellPatternHandler() {
		return sellPattern;
	}
	/**
	 * Checks config to get the 'auto-fill-name' setting.
	 *
	 * @return if names should be auto-set
	 */
	public boolean isAutoFillName() {
		return plugin.getConfig().getBoolean(AUTO_FILL_NAME, true);
	}
	/**
	 * Checks config to get the 'auto-fill-buy' setting.
	 * 
	 * @return if buy item and price should be detected from chest contents
	 */
	public boolean isAutoFillBuy() {
		return plugin.getConfig().getBoolean(AUTO_FILL_BUY, false);
	}
	public String getAutoFillBuyItemString() {
		return plugin.getConfig().getString(AUTO_FILL_BUY_ITEM_STRING, "[auto]");
	}
	/**
	 * Checks config to get the 'detailed-output' setting
	 *
	 * @return the config option for printing detailed chest output
	 */
	public boolean isDetailedOutput() {
		return plugin.getConfig().getBoolean(DETAILED_OUTPUT, true);
	}
	/**
	 * Checks config to see if 'protect-existing-chest' is set
	 *
	 * @return true
	 */
	public boolean isExistingChestProtected() {
		return plugin.getConfig().getBoolean(PROTECT_EXISTING_CHEST, true);
	}
	/**
	 * Checks config to see if extended names are enabled (only matters if auto-fill is on)
	 * @return true if service is enabled
	 */
	public boolean isExtendedNames() {
		return plugin.getConfig().getBoolean(AUTO_FILL_NAME, true)
			&& plugin.getConfig().getBoolean(EXTENDED_NAMES);
	}
	/**
	 * Checks config to get the 'protect-break' setting.
	 *
	 * @return the config option for protection chest breaking
	 */
	public boolean isProtectBreak() {
		return plugin.getConfig().getBoolean(PROTECT_BREAK, true);
	}
	/**
	 * Checks config to get the 'protect-chest-access' setting.
	 *
	 * @return the config option for protecting chest access
	 */
	public boolean isProtectChestAccess() {
		return plugin.getConfig().getBoolean(PROTECT_CHEST_ACCESS, true);
	}
	/**
	 * Checks config to get the 'protect-explode' setting.
	 *
	 * @return the config option for protecting chests from explosions
	 */
	public boolean isProtectExplode() {
		return plugin.getConfig().getBoolean(PROTECT_EXPLODE, true);
	}
	/**
	 * Checks config to get the 'showcase-mode' setting.
	 *
	 * @return the config option for showcase mode
	 */
	public boolean isShowcaseEnabled() {
		return plugin.getConfig().getBoolean(SHOWCASE_MODE, true) && isValidVersion();
	}
	/**
	 * Checks config (for override option) or references the compiled against server version
	 * @return if the ignore version option is active, or the server version matches the supported showcase version
	 */
	public boolean isValidVersion() {
		return plugin.getConfig().getBoolean(IGNORE_VERSION, false)
			|| plugin.getServer().getVersion().contains(PlayerHandler.MC_VERSION);
	}
}
