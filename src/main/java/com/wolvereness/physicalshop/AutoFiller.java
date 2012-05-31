package com.wolvereness.physicalshop;

import static org.bukkit.Material.CHEST;
import static org.bukkit.block.BlockFace.DOWN;

import java.util.ListIterator;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;


public class AutoFiller {
	public static boolean autoFillSign(SignChangeEvent e, String autoFillBuyItem, String autoFillBuy, String currencyString, Material currency) {
		if (e.getBlock().getRelative(DOWN).getType() == CHEST) {
			String[] lines = e.getLines();
			
			if (lines[0].equals(autoFillBuyItem)
					&& lines[1].equals("") && lines[2].equals("") && lines[3].equals("")) {
				
				Chest chest = (Chest) e.getBlock().getRelative(DOWN).getState();
				
				int price = 1;
				int amount = 1;
				String materialName = null;
				
				Inventory inventory = chest.getBlockInventory();
				
				if (inventory.contains(currency)) {
					int slot = inventory.first(currency);
					ItemStack stack = inventory.getItem(slot);
					price = stack.getAmount();
				}
				
				ListIterator<ItemStack> iterator = inventory.iterator();
				ShopMaterial material = null;
				while(iterator.hasNext()) {
					ItemStack stack = iterator.next();
					if (stack == null || stack.getData().getItemType() == org.bukkit.Material.AIR
							|| stack.getData().getItemType() == currency) {
						continue;
					}
					
					if (material == null) {
						material = new ShopMaterial(stack);
						materialName = material.toString();
						amount = stack.getAmount();
						if (amount > 1) {
							// is stackable, this shall be the amount
							break;
						}
						
					} else if (material.getMaterial().getId() == stack.getTypeId()) {
						// is not stackable, increse the amount for each item of that type
						amount += 1;
					}
				}
				
				
				if (materialName != null) {
					String line = "[" + materialName + "]";
					e.setLine(0, line);
					line = autoFillBuy.replace("%1$d", Integer.toString(amount)).replace("%2$s", price + currencyString);
					e.setLine(1, line);
				}
			}
		}
		return false;
	}
}
