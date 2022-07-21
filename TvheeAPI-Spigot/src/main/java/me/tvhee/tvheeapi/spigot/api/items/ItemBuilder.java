package me.tvhee.tvheeapi.spigot.api.items;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import me.tvhee.tvheeapi.api.chat.Component;
import me.tvhee.tvheeapi.api.collection.CollectionUtil;
import me.tvhee.tvheeapi.api.exception.TvheeAPIInternalException;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

public final class ItemBuilder
{
	private final ItemStack itemStack;

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
		new DurabilityCalculator(itemStack).setMaxDurability(durability);
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
		ItemMeta itemMeta = itemStack.getItemMeta();
		itemMeta.setDisplayName(name.toLegacyText(null));
		itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_DESTROYS);
		itemStack.setItemMeta(itemMeta);
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

	public ItemBuilder addEnchantment(Enchantment enchantment, int level)
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

	public ItemBuilder setLore(Component... lore)
	{
		ItemMeta itemMeta = itemStack.getItemMeta();
		itemMeta.setLore(CollectionUtil.convertList(Arrays.asList(lore), Component::toLegacyText));
		itemStack.setItemMeta(itemMeta);
		return this;
	}

	/**
	 * Set the lore
	 *
	 * @param lore The lore you want to set as Stringlist
	 * @return Return to ItemBuilder
	 */

	public ItemBuilder setLore(List<Component> lore)
	{
		ItemMeta itemMeta = itemStack.getItemMeta();
		itemMeta.setLore(CollectionUtil.convertList(lore, Component::toLegacyText));
		itemStack.setItemMeta(itemMeta);
		return this;
	}

	/**
	 * Remove a line from the lore
	 *
	 * @param line The line you want to remove
	 * @return Return to ItemBuilder
	 */

	public ItemBuilder removeLoreLine(Component line)
	{
		ItemMeta itemMeta = itemStack.getItemMeta();
		List<String> lore = new ArrayList<>(itemMeta.getLore());

		if(!lore.contains(line.toLegacyText()))
			return this;

		lore.remove(line.toLegacyText());
		itemMeta.setLore(lore);
		itemStack.setItemMeta(itemMeta);
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
		ItemMeta itemMeta = itemStack.getItemMeta();
		List<String> lore = new ArrayList<>(itemMeta.getLore());

		if(index < 0 || index > lore.size())
			return this;

		lore.remove(index);
		itemMeta.setLore(lore);
		itemStack.setItemMeta(itemMeta);
		return this;
	}

	/**
	 * Add a line to the lore
	 *
	 * @param line The line you want to add
	 * @return Return to ItemBuilder
	 */

	public ItemBuilder addLoreLine(Component line)
	{
		ItemMeta itemMeta = itemStack.getItemMeta();
		List<String> lore = new ArrayList<>();

		if(itemMeta.hasLore())
			lore = new ArrayList<>(itemMeta.getLore());

		lore.add(line.toLegacyText());
		itemMeta.setLore(lore);
		itemStack.setItemMeta(itemMeta);
		return this;
	}

	/**
	 * Add a line to the lore
	 *
	 * @param line     The line you want to add
	 * @param position The position where you want to add the lore
	 * @return Return to ItemBuilder
	 */

	public ItemBuilder addLoreLine(Component line, int position)
	{
		ItemMeta itemMeta = itemStack.getItemMeta();
		List<String> lore = new ArrayList<>(itemMeta.getLore());
		lore.set(position, line.toLegacyText());
		itemMeta.setLore(lore);
		itemStack.setItemMeta(itemMeta);
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
		LeatherArmorMeta itemMeta = (LeatherArmorMeta) itemStack.getItemMeta();
		itemMeta.setColor(color);
		itemStack.setItemMeta(itemMeta);
		return this;
	}

	/**
	 * Get your ItemStack
	 *
	 * @return Return the ItemStack
	 */

	public ItemStack toItem()
	{
		return itemStack;
	}
}

