package com.wolvereness.physicalshop.dynmap;

import java.io.File;
import java.io.InputStream;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.dynmap.DynmapCommonAPI;
import org.dynmap.markers.MarkerAPI;

import com.wolvereness.physicalshop.PhysicalShop;
import com.wolvereness.physicalshop.Shop;
import com.wolvereness.physicalshop.config.Localized;
import com.wolvereness.physicalshop.config.MaterialConfig;
import com.wolvereness.physicalshop.events.ShopCreationEvent;
import com.wolvereness.physicalshop.events.ShopDestructionEvent;
import com.wolvereness.physicalshop.events.ShopInteractEvent;

public class DynmapListener implements Listener {

	
	
	private PhysicalShop plugin;
	private DynmapCommonAPI dynmap;
	
	private File dynmapFile;
	private IconLoader iconLoader;

	public DynmapListener(PhysicalShop plugin, DynmapCommonAPI dynmap) {
		this.plugin = plugin;
		this.dynmap = dynmap;
		
		this.iconLoader = new IconLoader() {
			public InputStream load(/*boolean isBuy, boolean isSell, String owner*/) {
				/**
				 * Pop in plugin.getResource with custom icon if you feel creative
				 */
				return null;
			}
		};
		
		plugin.getLogger().info("Dynmap detected");
		
		reloadDynmapConfig();
		
		
	}
	
	public void reloadDynmapConfig() {
	    if (dynmapFile == null) {
	    	dynmapFile = new File(plugin.getDataFolder(), "dynmap.yml");
	    }
	}
	
	public void saveDynmapConfig() {
	    if (dynmapFile == null) {
	    	return;
	    }
	}

	/**
	 * Shop Interact event
	 * @param e Event
	 */
	@EventHandler(ignoreCancelled = false)
	public void onShopInteract(final ShopInteractEvent e) {
		if (!e.isCancelled()) {
			addOrUpdateMarker(e.getShop(), plugin.getMaterialConfig(), dynmap.getMarkerAPI(), iconLoader);
		}
	}

	@EventHandler(ignoreCancelled = false)
	public void onShopDeleted(final ShopDestructionEvent e) {
		for(Shop shop: e.getShops()) {
			new DynmapShopMarker(shop).delete(dynmap.getMarkerAPI());
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onNewShop(final ShopCreationEvent e) {
		if (!e.isCancelled()) {
			addOrUpdateMarker(e.getShop(), plugin.getMaterialConfig(), dynmap.getMarkerAPI(), iconLoader);
		}
	}

	private void addOrUpdateMarker(Shop shop, MaterialConfig materialConfig,
			MarkerAPI markerAPI, IconLoader iconLoader) {
		if (new DynmapShopMarker(shop, materialConfig, plugin.getLocale()).save(markerAPI,
				plugin.getLocale().getMessage(Localized.Message.DYNMAP_MARKER_SET_LABEL), iconLoader)) {
			plugin.getLogger().info("marker added");
		} else {
			plugin.getLogger().info("marker updated");
		}
		
	}
}
