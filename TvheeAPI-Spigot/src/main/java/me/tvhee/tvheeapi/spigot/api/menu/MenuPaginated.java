package me.tvhee.tvheeapi.spigot.api.menu;

import java.util.HashMap;
import java.util.Map;
import me.tvhee.tvheeapi.api.chat.Component;
import me.tvhee.tvheeapi.spigot.api.items.ItemBuilder;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public abstract class MenuPaginated extends Menu
{
	private final Map<Integer, ItemStack> permanentItems = new HashMap<>();
	private int nextClickSlot = -1;
	private int previousClickSlot = -1;
	private int page = 1;

	public MenuPaginated(Component name, InventoryType inventoryType)
	{
		super(name, inventoryType);
	}

	public MenuPaginated(Component name, int size)
	{
		super(name, size);
	}

	public final int getPage()
	{
		return page;
	}

	public final int getMaxPages()
	{
		int pages = 0;
		int maxContents = 0;
		int itemsPerPage = getNormalItemsPerPage();

		for(int key : getUsedSlots())
		{
			if(key > maxContents)
				maxContents = key;
		}

		for(int i = maxContents; i > 0; i = i - itemsPerPage)
			pages++;

		return pages;
	}

	public final int getNormalItemsPerPage()
	{
		return getSize() - permanentItems.size();
	}

	public final int getPermanentItemsPerPage()
	{
		return permanentItems.size();
	}

	public final void setPermanentItem(int slot, ItemBuilder item)
	{
		setPermanentItem(slot, item.toItem(), PermanentItemType.NORMAL);
	}

	public final void setPermanentItem(int slot, ItemBuilder item, PermanentItemType itemType)
	{
		setPermanentItem(slot, item.toItem(), itemType);
	}

	public final void setPermanentItem(int slot, ItemStack item)
	{
		setPermanentItem(slot, item, PermanentItemType.NORMAL);
	}

	public final void setPermanentItem(int slot, ItemStack item, PermanentItemType itemType)
	{
		if(this.permanentItems.containsKey(slot))
			throw new IllegalArgumentException("Slot is already set!");

		if(itemType == PermanentItemType.NEXT_BUTTON)
		{
			if(this.nextClickSlot != -1)
				this.permanentItems.remove(nextClickSlot);

			this.nextClickSlot = slot;
		}
		else if(itemType == PermanentItemType.PREVIOUS_BUTTON)
		{
			if(this.previousClickSlot != -1)
				this.permanentItems.remove(previousClickSlot);

			this.previousClickSlot = slot;
		}

		this.permanentItems.put(slot, item);
	}
	
	public final ItemStack getPermanentItem(int slot)
	{
		return this.permanentItems.get(slot);
	}

	public final boolean isPermanentItem(int slot)
	{
		return this.permanentItems.containsKey(slot);
	}

	@Override
	public final boolean isSlotFiller(int slot)
	{
		return !this.permanentItems.containsKey(slot) && !getContents().containsKey(slot);
	}

	public int getNextClickSlot()
	{
		return nextClickSlot;
	}

	public int getPreviousClickSlot()
	{
		return previousClickSlot;
	}

	public final boolean isNextClick(int slot)
	{
		return slot == nextClickSlot;
	}

	public final boolean isPreviousClick(int slot)
	{
		return slot == previousClickSlot;
	}

	//Get the slot of the 'items' list without the permanent items
	public final int getNormalItemSlot(int inventorySlot)
	{
		int slot = (page - 1) * getNormalItemsPerPage();

		for(int foundSlot = 0; foundSlot < inventorySlot; foundSlot++)
		{
			if(!isPermanentItem(foundSlot))
				slot++;
		}

		return slot;
	}

	@Override
	public final void open(Player player)
	{
		this.page = 1;
		super.open(player);
	}

	@Override
	public void update()
	{
		Inventory inventory = getInventory();
		int currentItem = (page - 1) * getNormalItemsPerPage();

		for(int slot = 0; slot < getSize(); slot++)
		{
			if(isPermanentItem(slot) || isSlotFiller(slot))
			{
				if((slot == nextClickSlot && page >= getMaxPages()) || (slot == previousClickSlot && page <= 1))
					continue;

				inventory.setItem(slot, replacePageModifier(isSlotFiller(slot) ? getSlotFiller() : permanentItems.get(slot)));
			}
			else
			{
				inventory.setItem(slot, replacePageModifier(getItem(currentItem)));
				currentItem++;
			}
		}
	}

	private ItemStack replacePageModifier(ItemStack item)
	{
		ItemStack newItem = item.clone();
		ItemMeta itemMeta = newItem.getItemMeta();

		if(itemMeta != null)
		{
			String name = itemMeta.getDisplayName().replaceAll("%page%", String.valueOf(page));
			itemMeta.setDisplayName(name);
		}

		newItem.setItemMeta(itemMeta);
		return newItem;
	}

	public enum PermanentItemType
	{
		NEXT_BUTTON,
		PREVIOUS_BUTTON,
		NORMAL
	}
}
