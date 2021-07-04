package me.tvhee.tvheeapi.spigot.api.items;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

public final class DurabilityCalculator
{
	private final DataContainer dataContainer;
	private final ItemStack item;
	private boolean broken;

	public DurabilityCalculator(ItemStack item, int durability)
	{
		this.dataContainer = new DataContainer(item.getItemMeta().getPersistentDataContainer());
		this.item = item;

		setMaxDurability(durability);
	}

	public void setMaxDurability(int durabilityItem)
	{
		dataContainer.addDouble("durability", item.getType().getMaxDurability());
		dataContainer.addInteger("maxDurability", durabilityItem);
		item.setItemMeta((ItemMeta) dataContainer.getDataHolder());
	}

	private double getDurabilityMainItem()
	{
		return dataContainer.getDouble("durability");
	}

	private int getMaxCustomDurability()
	{
		return dataContainer.getInteger("maxDurability");
	}

	public void addDamage(double damage)
	{
		if(broken)
			return;

		int maxDurability = item.getType().getMaxDurability();
		double calculateNewDamage = (maxDurability - getDurabilityMainItem()) + (maxDurability / (getMaxCustomDurability() * damage));
		int newDamage = (int) Math.round(calculateNewDamage);
		double newDurability = maxDurability - calculateNewDamage;

		if(newDamage >= maxDurability)
		{
			item.setAmount(item.getAmount() - 1);
			broken = true;
			return;
		}

		Damageable im = (Damageable) item.getItemMeta();
		im.setDamage(0);
		im.setDamage(newDamage);
		item.setItemMeta((ItemMeta) im);

		dataContainer.addDouble("durability", newDurability);
		item.setItemMeta((ItemMeta) dataContainer.getDataHolder());
	}

	public boolean isBroken()
	{
		return broken;
	}

	public ItemStack getItem()
	{
		return item;
	}
}
