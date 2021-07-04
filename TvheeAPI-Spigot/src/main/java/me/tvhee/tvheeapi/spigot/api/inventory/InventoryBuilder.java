package me.tvhee.tvheeapi.spigot.api.inventory;

import me.tvhee.tvheeapi.api.chat.Component;
import me.tvhee.tvheeapi.api.exception.TvheeAPIException;
import me.tvhee.tvheeapi.api.player.Player;
import me.tvhee.tvheeapi.spigot.api.inventory.PaginatedInventory.PermanentItemType;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class InventoryBuilder
{
	private final Component name;
	private final Player player;
	private int size = -1;
	private InventoryType inventoryType;

	public InventoryBuilder(Component name, InventoryType inventoryType, Player player)
	{
		this.name = name;
		this.inventoryType = inventoryType;
		this.player = player;
	}

	public InventoryBuilder(Component name, int size, Player player)
	{
		this.name = name;
		this.size = size;
		this.player = player;
	}

	public Inventory buildInventory()
	{
		if(inventoryType != null && size == -1)
			return new InventoryMenu(name, inventoryType, player);
		else if(inventoryType == null && size != -1)
			return new InventoryMenu(name, size, player);
		else
			throw new TvheeAPIException(getClass(), "buildInventory", "Illegal inventory size!");
	}

	public PaginatedInventory buildPaginatedInventory(int nextButtonSlot, ItemStack nextButton, int previousButtonSlot, ItemStack previousButton)
	{
		PaginatedInventory inventory;

		if(inventoryType != null && size == -1)
			inventory = new PaginatedInventory(name, inventoryType, player);
		else if(inventoryType == null && size != -1)
			inventory =  new PaginatedInventory(name, size, player);
		else
			throw new TvheeAPIException(getClass(), "buildPaginatedInventory", "Illegal inventory size!");

		inventory.setPermanentItem(nextButtonSlot, nextButton, PermanentItemType.NEXT_BUTTON);
		inventory.setPermanentItem(previousButtonSlot, previousButton, PermanentItemType.PREVIOUS_BUTTON);
		return inventory;
	}

	public PaginatedInventory buildPaginatedInventory(int nextButtonSlot, Component nextButtonName, Material nextButton, int previousButtonSlot, Component previousButtonName, Material previousButton)
	{
		ItemStack nextButtonItem = new ItemStack(nextButton);
		ItemMeta nextButtonMeta = nextButtonItem.getItemMeta();
		nextButtonMeta.setDisplayName(nextButtonName.toLegacyText(null));
		nextButtonItem.setItemMeta(nextButtonMeta);

		ItemStack previousButtonItem = new ItemStack(previousButton);
		ItemMeta previousButtonMeta = previousButtonItem.getItemMeta();
		previousButtonMeta.setDisplayName(previousButtonName.toLegacyText(null));
		previousButtonItem.setItemMeta(previousButtonMeta);

		return buildPaginatedInventory(nextButtonSlot, nextButtonItem, previousButtonSlot, previousButtonItem);
	}
}
