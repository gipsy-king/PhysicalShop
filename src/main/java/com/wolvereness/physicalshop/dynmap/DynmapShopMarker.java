package com.wolvereness.physicalshop.dynmap;

import java.io.InputStream;

import org.bukkit.Location;
import org.dynmap.markers.Marker;
import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerIcon;
import org.dynmap.markers.MarkerSet;

import com.wolvereness.physicalshop.ChestShop;
import com.wolvereness.physicalshop.Rate;
import com.wolvereness.physicalshop.Shop;
import com.wolvereness.physicalshop.config.Localized;
import com.wolvereness.physicalshop.config.MaterialConfig;

public class DynmapShopMarker {

	private static final String MARKER_SET_NAME = "Shops";
	private static final String MARKER_SET_ID = "PhysicalShop";
	
	
	private String id;
	private String html;
	private String world;
	private double x;
	private double y;
	private double z;
	private boolean isBuy;
	private boolean isSell;
	private String owner;
	
	public DynmapShopMarker(Shop shop, MaterialConfig materialConfig, Localized local) {
		
		this(shop);
		
		this.html = createHTML(shop, materialConfig, local);
		
	}
	
	public DynmapShopMarker(Shop shop) {
		Location location = shop.getSign().getLocation();
		world = location.getWorld().getName();
		x = location.getBlockX();
		y = location.getBlockY();
		z = location.getBlockZ();
		id = world + "," + x + "," + y + "," + z;
		
		owner = shop.getOwnerName();
		
		if (shop.getBuyRate() != null) {
			isBuy = true;
		}
		if (shop.getSellRate() != null) {
			isSell = true;
		}
		
		
	}

	/**
	 * Save shop as dynmap marker
	 * @param markerAPI
	 * @param iconLoader
	 * @return true if marker added or false if marker updated
	 */
	public boolean save(MarkerAPI markerAPI, IconLoader iconLoader) {
		MarkerSet set = markerAPI.getMarkerSet(MARKER_SET_ID);
		if (set == null) {
			set = markerAPI.createMarkerSet(MARKER_SET_ID, MARKER_SET_NAME, null, true);
		}
		
		Marker marker = set.findMarker(id);
		if (marker == null) {
			MarkerIcon icon;
			InputStream stream = iconLoader.load(isBuy, isSell, owner);
			if (stream == null) {
				icon = markerAPI.getMarkerIcon("coins");
			} else {
				icon = markerAPI.createMarkerIcon("physicalshop", "PhsyicalShop", stream);
			}
			
			set.createMarker(id, html, true, world, x, y, z, icon, true);
			return true;
			
		} else {
			marker.setLabel(html, true);
			return false;
		}
		
	}

	public void delete(MarkerAPI markerAPI) {
		MarkerSet set = markerAPI.getMarkerSet(MARKER_SET_ID);
		if (set != null) {
			Marker marker = set.findMarker(id);
			if (marker != null) {
				marker.deleteMarker();
			}
		}
	}

	private String createHTML(Shop shop, MaterialConfig materialConfig, Localized local) {
		Rate buy = shop.getBuyRate();
		Rate sell = shop.getSellRate();
		
		int itemCount = -1;
		int buyCapital = 0;
		int sellCapital = 0;
		if (shop instanceof ChestShop) {
			ChestShop chestShop = (ChestShop) shop;
			itemCount = chestShop.getShopItems();
			if (buy != null) {
				buyCapital = chestShop.getShopBuyCapital();
			}
			if (sell != null) {
				sellCapital = chestShop.getShopSellCapital();
			}
		}
		
		
		
		
		String html = wrapInTags(new String[] {"div", "strong"}, shop.getMaterial().toString(materialConfig));
				
		
		if (isBuy) {
			html += wrapInDiv(local.getMessage(Localized.Message.DYNMAP_HTML_BUY,
					buy.getAmount(), buy.getPrice(), shop.getBuyCurrency().toString(materialConfig)));
		}
		
		if (isSell) {
			html += wrapInDiv(local.getMessage(Localized.Message.DYNMAP_HTML_SELL,
					sell.getAmount(), sell.getPrice(), shop.getSellCurrency().toString(materialConfig)));
		}
		
		html += wrapInDiv(shop.getOwnerName());
		
		if (itemCount > -1) {
			
			if (isBuy) {
				html += wrapInDiv(local.getMessage(Localized.Message.DYNMAP_HTML_AVAILABLE,
						itemCount, shop.getMaterial().toString(materialConfig)));
				html += wrapInDiv(local.getMessage(Localized.Message.DYNMAP_HTML_PROFIT_BUY,
						buyCapital, shop.getBuyCurrency().toString(materialConfig)));
			}
			if (isSell) {
				html += wrapInDiv(local.getMessage(Localized.Message.DYNMAP_HTML_BOUGHT,
						itemCount, shop.getMaterial().toString(materialConfig)));
				html += wrapInDiv(local.getMessage(Localized.Message.DYNMAP_HTML_PROFIT_SELL,
						sellCapital, shop.getSellCurrency().toString(materialConfig)));
			}
		}
		return html;
	}

	private static String wrapInDiv(String string) {
		return wrapInTags(new String[] {"div"}, string);
	}
	
	private static String wrapInTags(String[] tags, String string) {
		if (tags.length == 1) {
			return "<" + tags[0] + ">" + string + "</" + tags[0] + ">";
			
		} else {
			String result = "";
			for(String tag: tags) {
				result += "<" + tag + ">";
			}
			result += string;
			for(int i = tags.length - 1; i >= 0; i--) {
				result += "</" + tags[i] + ">";
			}
			return result;
			
		}
	}
}
