package me.tvhee.tvheeapi.spigot.api.items;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import me.tvhee.tvheeapi.api.chat.Component;
import me.tvhee.tvheeapi.api.exception.TvheeAPIInternalException;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

public final class ItemBuilder
{
	private ItemStack itemStack;

	public ItemBuilder(Material material)
	{
		this(material, 1);
	}

	public ItemBuilder(ItemStack itemStack)
	{
		this.itemStack = itemStack;
	}

	public ItemBuilder(Material material, int amount)
	{
		itemStack = new ItemStack(material, amount);
	}

	@Override
	public ItemBuilder clone()
	{
		return new ItemBuilder(itemStack);
	}

	/**
	 * This will set the durability
	 *
	 * @param durability The durability you want to set
	 * @return Return to ItemBuilder
	 * @see DurabilityCalculator for more information about durability
	 */

	public ItemBuilder setDurability(int durability)
	{
		DurabilityCalculator durabilityCalculator = new DurabilityCalculator(itemStack, durability);
		this.itemStack = durabilityCalculator.getItem();
		return this;
	}

	/**
	 * This will set the name
	 *
	 * @param name The name you want to set (colors allowed)
	 * @return Return to ItemBuilder
	 */

	public ItemBuilder setName(Component name)
	{
		ItemMeta im = itemStack.getItemMeta();
		im.setDisplayName(name.toLegacyText(null));
		im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_DESTROYS);
		itemStack.setItemMeta(im);
		return this;
	}

	/**
	 * This will set the custom model data
	 *
	 * @param model The model you want to set
	 * @return Return to ItemBuilder
	 */

	public ItemBuilder setModel(int model)
	{
		ItemMeta im = itemStack.getItemMeta();
		im.setCustomModelData(model);
		itemStack.setItemMeta(im);
		return this;
	}

	/**
	 * This will add an unsafe enchantment
	 *
	 * @param enchantment The enchantment you want to set
	 * @param level       The level for your enchantment
	 * @return Return to ItemBuilder
	 */

	public ItemBuilder addUnsafeEnchantment(Enchantment enchantment, int level)
	{
		itemStack.addUnsafeEnchantment(enchantment, level);
		return this;
	}

	/**
	 * This will remove an enchantment
	 *
	 * @param enchantment The enchantment you want to remove
	 * @return Return to ItemBuilder
	 */

	public ItemBuilder removeEnchantment(Enchantment enchantment)
	{
		itemStack.removeEnchantment(enchantment);
		return this;
	}

	/**
	 * This will set the owner of a skull (only if you are creating an skull)
	 *
	 * @param owner The exact getUuid of the player
	 * @return Return to ItemBuilder
	 */

	public ItemBuilder setSkullOwner(UUID owner)
	{
		try
		{
			SkullMeta im = (SkullMeta) itemStack.getItemMeta();
			im.setOwningPlayer(Bukkit.getOfflinePlayer(owner));
			itemStack.setItemMeta(im);
		}
		catch(ClassCastException e)
		{
			throw new TvheeAPIInternalException(getClass(), "setSkullOwner", e);
		}

		return this;
	}

	/**
	 * This will add an enchantment
	 *
	 * @param enchantment The enchantment you want to set
	 * @param level       The level for your enchantment
	 * @return Return to ItemBuilder
	 */

	public ItemBuilder addEnchant(Enchantment enchantment, int level)
	{
		ItemMeta im = itemStack.getItemMeta();
		im.addEnchant(enchantment, level, true);
		itemStack.setItemMeta(im);
		return this;
	}

	/**
	 * This will add enchantments
	 *
	 * @param enchantments The enchantments you want to set in an Map (Enchantment enchantment, Integer level)
	 * @return Return to ItemBuilder
	 */

	public ItemBuilder addEnchantments(Map<Enchantment, Integer> enchantments)
	{
		itemStack.addEnchantments(enchantments);
		return this;
	}

	/**
	 * Makes the item unbreakable, do NOT set if you are using my custom durability system
	 *
	 * @return Return to ItemBuilder
	 */

	public ItemBuilder setInfinityDurability()
	{
		ItemMeta meta = itemStack.getItemMeta();
		meta.setUnbreakable(true);
		itemStack.setItemMeta(meta);
		return this;
	}

	/**
	 * Set the lore
	 *
	 * @param lore The lore you want to set
	 * @return Return to ItemBuilder
	 */

	public ItemBuilder setLore(String... lore)
	{
		ItemMeta im = itemStack.getItemMeta();
		im.setLore(Arrays.asList(lore));
		itemStack.setItemMeta(im);
		return this;
	}

	/**
	 * Set the lore
	 *
	 * @param lore The lore you want to set as Stringlist
	 * @return Return to ItemBuilder
	 */

	public ItemBuilder setLore(List<String> lore)
	{
		ItemMeta im = itemStack.getItemMeta();
		im.setLore(lore);
		itemStack.setItemMeta(im);
		return this;
	}

	/**
	 * Remove a line from the lore
	 *
	 * @param line The line you want to remove
	 * @return Return to ItemBuilder
	 */

	public ItemBuilder removeLoreLine(String line)
	{
		ItemMeta im = itemStack.getItemMeta();
		List<String> lore = new ArrayList<>(im.getLore());
		if(!lore.contains(line))
			return this;
		lore.remove(line);
		im.setLore(lore);
		itemStack.setItemMeta(im);
		return this;
	}

	/**
	 * Remove a line from the lore
	 *
	 * @param index The line number you want to remove
	 * @return Return to ItemBuilder
	 */

	public ItemBuilder removeLoreLine(int index)
	{
		ItemMeta im = itemStack.getItemMeta();
		List<String> lore = new ArrayList<>(im.getLore());
		if(index < 0 || index > lore.size())
			return this;
		lore.remove(index);
		im.setLore(lore);
		itemStack.setItemMeta(im);
		return this;
	}

	/**
	 * Add a line to the lore
	 *
	 * @param line The line you want to add
	 * @return Return to ItemBuilder
	 */

	public ItemBuilder addLoreLine(String line)
	{
		ItemMeta im = itemStack.getItemMeta();
		List<String> lore = new ArrayList<>();
		if(im.hasLore())
			lore = new ArrayList<>(im.getLore());
		lore.add(line);
		im.setLore(lore);
		itemStack.setItemMeta(im);
		return this;
	}

	/**
	 * Add a line to the lore
	 *
	 * @param line     The line you want to add
	 * @param position The position where you want to add the lore
	 * @return Return to ItemBuilder
	 */

	public ItemBuilder addLoreLine(String line, int position)
	{
		ItemMeta im = itemStack.getItemMeta();
		List<String> lore = new ArrayList<>(im.getLore());
		lore.set(position, line);
		im.setLore(lore);
		itemStack.setItemMeta(im);
		return this;
	}

	/**
	 * Set the color of dye
	 *
	 * @param color The color you want to set
	 * @return Return to ItemBuilder
	 */

	public ItemBuilder setDyeColor(String color)
	{
		color = color.toUpperCase();
		this.itemStack.setDurability(DyeColor.valueOf(color).getDyeData());
		return this;
	}

	/**
	 * Set the wool color
	 *
	 * @param color The color you want to set
	 * @return Return to ItemBuilder
	 */

	@Deprecated
	public ItemBuilder setWoolColor(String color)
	{
		color = color.toUpperCase();
		if(!itemStack.getType().equals(Material.valueOf(color + "_WOOL")))
			return this;
		this.itemStack.setDurability(DyeColor.valueOf(color).getDyeData());
		return this;
	}

	/**
	 * Set the armour color (only leather)
	 *
	 * @param color The color you want to set
	 * @return Return to ItemBuilder
	 */

	public ItemBuilder setLeatherArmorColor(Color color)
	{
		try
		{
			LeatherArmorMeta im = (LeatherArmorMeta) itemStack.getItemMeta();
			im.setColor(color);
			itemStack.setItemMeta(im);
		}
		catch(ClassCastException e)
		{
			throw new TvheeAPIInternalException(getClass(), "setLeatherArmorColor", e);
		}
		return this;
	}

	/**
	 * Get your ItemStack
	 *
	 * @return Return the ItemStack
	 */

	public ItemStack toItemStack()
	{
		return itemStack;
	}
}

