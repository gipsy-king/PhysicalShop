package com.wolvereness.physicalshop;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.CoalType;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.TreeSpecies;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Coal;
import org.bukkit.material.Dye;
import org.bukkit.material.Leaves;
import org.bukkit.material.MaterialData;
import org.bukkit.material.MonsterEggs;
import org.bukkit.material.Sandstone;
import org.bukkit.material.SmoothBrick;
import org.bukkit.material.Step;
import org.bukkit.material.Tree;
import org.bukkit.material.Wool;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;

import com.wolvereness.physicalshop.config.MaterialConfig;
import com.wolvereness.physicalshop.exception.InvalidMaterialException;
/**
 *
 */
public class ShopMaterial {
	private static String toHumanReadableString(final Object object) {
		final StringBuilder sb = new StringBuilder();

		for (final String word : object.toString().split("_")) {
			if (word.length() == 0) {
				continue;
			}
			sb.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1).toLowerCase()).append(' ');
		}

		sb.deleteCharAt(sb.length() - 1);

		return sb.toString();
	}
	private final short durability;
	private final Map<Enchantment, Integer> enchantment;
	private final Material material;
	/**
	 * @param itemStack items to derive this material from
	 */
	public ShopMaterial(final ItemStack itemStack) {
		this(itemStack.getType(), itemStack.getDurability(), itemStack.getEnchantments());
	}
	/**
	 * @param material bukkit material to reference
	 * @param durability durability to reference
	 * @param enchantment enchantment to reference
	 */
	public ShopMaterial(
	                    final Material material,
	                    final short durability,
	                    final Map<Enchantment,Integer> enchantment) {
		this.material = material;
		this.durability = durability;
		this.enchantment = enchantment == null ? null : enchantment.isEmpty() ? null : enchantment;
	}
	/**
	 * @param string input string
	 * @throws InvalidMaterialException if material invalid
	 */
	public ShopMaterial(final String string) throws InvalidMaterialException {
		if (!string.contains("#") || !string.contains("^") ) {
			enchantment = null;
			final String[] strings = string.split(":");
	
			if (strings.length == 2) {
				material = Material.matchMaterial(strings[0].trim());
				try {
					durability = Short.parseShort(strings[1].trim());
				} catch (final NumberFormatException ex) {
					throw new InvalidMaterialException();
				}
				return;
			}
	
			Material material = null;
	
			for (int i = 0; i < string.length(); ++i) {
				if ((i == 0) || (string.charAt(i) == ' ')) {
					material = Material.matchMaterial(string.substring(i).trim());
	
					if (material != null) {
						this.material = material;
						durability = parseDurability(string.substring(0, i).trim(), material);
						return;
					}
				}
			}
	
			throw new InvalidMaterialException();
		} else {
			final String[] strings = string.split("#");
			material = Material.getMaterial(Integer.parseInt(strings[0]));
			
			if (strings.length > 1) {
				enchantment = new HashMap<Enchantment, Integer>();
				boolean first = true;
				for(String eString: strings) {
					if (first) {
						first = false;
						continue;
					}
					String[] eStrings = eString.split("\\^");
					enchantment.put(new EnchantmentWrapper(Integer.parseInt(eStrings[0])), Integer.parseInt(eStrings[1]));
				}
			} else {
				enchantment = null;
			}
			/**
			 * Ignore durability, so used enchanted items cannot be sold
			 */
			durability = 0;
		}
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof ShopMaterial)) return false;
		final ShopMaterial other = (ShopMaterial) obj;
		if (durability != other.durability) return false;
		if (material != other.material) return false;
		if (enchantment == other.enchantment) return true;
		if (enchantment == null) return false;
		if (!enchantment.equals(other.enchantment)) return false;
		return true;
	}
	/**
	 * @return the durability for this material
	 */
	public short getDurability() {
		return durability;
	}
	/**
	 * @return enchantments of material
	 */
	public Map<Enchantment,Integer> getEnchantment() {
		return enchantment;
	}
	/**
	 * @return the bukkit material for this material
	 */
	public Material getMaterial() {
		return material;
	}
	/**
	 * @param amount size to set the stack to
	 * @return an item stack representing this material
	 */
	public ItemStack getStack(final int amount) {
		if(amount == 0) return null;
		final ItemStack stack = new ItemStack(getMaterial(), amount,getDurability());
		if(enchantment == null) return stack;
		stack.addUnsafeEnchantments(enchantment);
		return stack;
	}
	@Override
	public int hashCode() {
		return material.ordinal() + durability * 31;
	}

	@SuppressWarnings("javadoc")
	@Deprecated
	public short parseDurability(final String string,final Material material) {
		try {
			return Short.parseShort(string);
		} catch (final NumberFormatException e) {
		}

		final String s = string.replace(' ', '_').toUpperCase();
		MaterialData data = null;

		try {
			switch (material) {
			case COAL:
				data = new Coal(CoalType.valueOf(s));
				break;
			case WOOD:
			case LOG:
				data = new Tree(TreeSpecies.valueOf(s));
				break;
			case LEAVES:
				data = new Leaves(TreeSpecies.valueOf(s));
				break;
			case STEP:
			case DOUBLE_STEP:
				data = new Step(Material.valueOf(s));
				break;
			case INK_SACK:
				data = new Dye();
				((Dye) data).setColor(DyeColor.valueOf(s));
				break;
			case WOOL:
				data = new Wool(DyeColor.valueOf(s));
				break;
			case MONSTER_EGGS:
				data = new MonsterEggs(Material.valueOf(s));
				break;
			case SMOOTH_BRICK:
				data = new SmoothBrick(Material.valueOf(s));
				break;
			case SANDSTONE:
				data = new Sandstone(Material.valueOf(s));
			}
		} catch (final IllegalArgumentException e) {
		}

		return data == null ? 0 : (short) data.getData();
	}//*/

	@Override
	public String toString() {
		return toHumanReadableString(toStringDefault(new StringBuilder()).toString());
	}

	/**
	 * @param materialConfig the material config to consider
	 * @return an appropriate string representing this shop material
	 */
	public String toString(final MaterialConfig materialConfig) {
		if (materialConfig.isConfigured(this)) {
			return materialConfig.toString(this);
			
		} else {
			// if enchanted, returns human readable string of item and enchantments
			return toHumanReadableString(toStringDefault(new StringBuilder(), true).toString());
			
		}
	}
	
	/**
	 * Adds information to
	 * @param sb StringBuilder being used
	 * @return the StringBuilder used
	 */
	public StringBuilder toStringDefault(final StringBuilder sb) {
		return toStringDefault(sb, false);
	}
	
	/**
	 * Adds information to
	 * @param sb StringBuilder being used
	 * @param humanReadableEnchantment true to decode enchantment, false to encode it
	 * @return the StringBuilder used
	 */
	private StringBuilder toStringDefault(final StringBuilder sb, boolean humanReadableEnchantment) {
		switch (material) {
		case COAL:
			sb.append(new Coal(material, (byte) durability).getType().toString());
			return sb;
		case WOOD:
		case LOG:
			sb.append(new Tree(material, (byte) durability).getSpecies().toString()).append('_');
			break;
		case LEAVES:
			sb.append(new Leaves(material, (byte) durability).getSpecies().toString()).append('_');
			break;
		case STEP:
		case DOUBLE_STEP:
			sb.append(new Step(material, (byte) durability).getMaterial().toString()).append('_');
			break;
		case INK_SACK:
			sb.append(new Dye(material, (byte) durability).getColor().toString()).append('_');
			break;
		case WOOL:
			sb.append(new Wool(material, (byte) durability).getColor().toString()).append('_');
			break;
		case MONSTER_EGGS:
			sb.append(new MonsterEggs(material, (byte) durability).getMaterial().toString()).append('_');
			break;
		case SMOOTH_BRICK:
			sb.append(new SmoothBrick(material, (byte) durability).getMaterial().toString()).append('_');
			break;
		case SANDSTONE:
			sb.append(new Sandstone(material, (byte) durability).getType().toString()).append('_');
			break;
		case POTION:
			if (humanReadableEnchantment) {
				sb.append(material.toString());
				Potion potion = new Potion((byte) durability);
				sb.append('_').append(potion.getType().toString());
				if (potion.getLevel() == 2) {
					sb.append('_').append("II");
				} else if (potion.getLevel() > 2) {
					sb.append('_').append(potion.getLevel());
				}
				
				for(PotionEffect effect: potion.getEffects()) {
					sb.append('_').append(effect.getType().getName());
					int duration = (int) Math.floor(effect.getDuration() / 20);
					if (duration > 0) {
						sb.append('(');
						if (duration / 60 < 10) {
							sb.append('0');
						}
						sb.append((int) Math.floor(duration / 60));
						sb.append(':');
						if (duration % 60 < 10) {
							sb.append('0');
						}
						sb.append((int) Math.floor(duration % 60));
						sb.append(')');
					}
				}
				
				if (potion.isSplash()) {
					sb.append('_').append("+Splash");
				}
				
				return sb;
			} else {
				return sb.append(material.toString()).append(':').append((byte) durability);
			}
			
			
		}
		
		if (enchantment != null && enchantment.size() > 0) {
			if (humanReadableEnchantment) {
				sb.append(material.toString());
				for (Map.Entry<Enchantment, Integer> entry : enchantment.entrySet()) {
					sb.append('+');
				    sb.append(toHumanReadableString(entry.getKey().getName()).replace(" ", ""));
				    sb.append('(').append(entry.getValue()).append(')');
				}
				return sb;
			} else {
				sb.append(material.getId());
				for (Map.Entry<Enchantment, Integer> entry : enchantment.entrySet()) {
					sb.append('#');
				    sb.append(entry.getKey().getId());
				    sb.append('^').append(entry.getValue());
				}
				return sb;
			}
		
		} else {
			return sb.append(material.toString());
		}
	}
}
