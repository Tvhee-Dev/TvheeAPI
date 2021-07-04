package me.tvhee.tvheeapi.spigot.api.inventory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.tvhee.tvheeapi.api.chat.MessageType;
import me.tvhee.tvheeapi.api.chat.Component;
import me.tvhee.tvheeapi.api.exception.TvheeAPIException;
import me.tvhee.tvheeapi.api.plugin.TvheeAPIPlugin;
import me.tvhee.tvheeapi.spigot.core.BukkitPlayer;
import me.tvhee.tvheeapi.spigot.core.BukkitPluginLoader;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

public final class PaginatedInventory extends Inventory
{
	private final Map<Integer, ItemStack> permanentSlotIcons = new HashMap<>();
	private int nextClickSlot = -1;
	private int previousClickSlot = -1;
	private int page = 1;

	public PaginatedInventory(Component name, InventoryType inventoryType, me.tvhee.tvheeapi.api.player.Player player)
	{
		super(name, inventoryType, player);
	}

	public PaginatedInventory(Component name, int size, me.tvhee.tvheeapi.api.player.Player player)
	{
		super(name, size, player);
	}

	public final int getPage()
	{
		return page;
	}

	public void setPermanentItem(int slot, Component name, Material material, List<Component> lore, PermanentItemType itemType)
	{
		setPermanentItem(slot, buildStack(name, new ItemStack(material), lore), itemType);
	}

	public void setPermanentItem(int slot, Component name, ItemStack item, List<Component> lore, PermanentItemType itemType)
	{
		setPermanentItem(slot, buildStack(name, item, lore), itemType);
	}

	public void setPermanentItem(int slot, Component name, ItemStack item, PermanentItemType itemType)
	{
		setPermanentItem(slot, buildStack(name, item), itemType);
	}

	public void setPermanentItem(int slot, Component name, Material material, PermanentItemType itemType)
	{
		setPermanentItem(slot, buildStack(name, new ItemStack(material)), itemType);
	}

	public void setPermanentItem(int slot, ItemStack item, PermanentItemType itemType)
	{
		if(this.permanentSlotIcons.containsKey(slot))
			throw new TvheeAPIException(getClass(), "setPermanentItem", "Slot is already set!");

		if(itemType == PermanentItemType.NEXT_BUTTON)
		{
			if(this.nextClickSlot != -1)
				this.permanentSlotIcons.remove(nextClickSlot);

			this.nextClickSlot = slot;
		}
		else if(itemType == PermanentItemType.PREVIOUS_BUTTON)
		{
			if(this.previousClickSlot != -1)
				this.permanentSlotIcons.remove(previousClickSlot);

			this.previousClickSlot = slot;
		}

		this.permanentSlotIcons.put(slot, item);
	}
	
	public ItemStack getPermanentItem(int slot)
	{
		return this.permanentSlotIcons.get(slot);
	}

	public boolean isPermanentItem(int slot)
	{
		return this.permanentSlotIcons.containsKey(slot);
	}

	@Override
	public boolean isSlotFiller(int slot)
	{
		return !this.permanentSlotIcons.containsKey(slot) && !getContents().containsKey(slot);
	}

	@Override
	public org.bukkit.inventory.Inventory getBukkitInventory()
	{
		org.bukkit.inventory.Inventory inventory = getInventoryType() == null ? Bukkit.createInventory(((org.bukkit.entity.Player) getPlayer().getOriginal()), getSize(), getName().toLegacyText(MessageType.INVENTORY_LINE)) : Bukkit.createInventory(((org.bukkit.entity.Player) getPlayer().getOriginal()), getInventoryType(), getName().toLegacyText(MessageType.INVENTORY_LINE));

		for(int i = 0; i < getSize(); i++)
		{
			if(this.permanentSlotIcons.containsKey(i))
			{
				if((previousClickSlot == i && page - 1 < 1) || (nextClickSlot == i && page + 1 > getMaxPage()))
					continue;

				inventory.setItem(i, replacePageModifier(permanentSlotIcons.get(i)));
				continue;
			}

			int iconSlot = i + ((getSize() - permanentSlotIcons.size()) * (page - 1));

			if(getItem(iconSlot) != null)
				inventory.setItem(i, replacePageModifier(getItem(iconSlot)));
		}

		return inventory;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInventoryClick(org.bukkit.event.inventory.InventoryClickEvent e)
	{
		if(!isAllowedToReplace())
			e.setCancelled(true);

		int slot = e.getRawSlot();

		if(e.getCurrentItem() == null || (e.getCurrentItem().getType() != Material.AIR && !e.getCurrentItem().getType().isItem()))
			return;

		if(slot == nextClickSlot && e.getCurrentItem().equals(getPermanentItem(nextClickSlot)))
		{
			page++;
			open();
			return;
		}
		else if(slot == previousClickSlot && e.getCurrentItem().equals(getPermanentItem(previousClickSlot)))
		{
			page--;
			open();
			return;
		}

		int iconSlot = slot;

		if(!permanentSlotIcons.containsKey(slot))
			iconSlot = slot + ((getSize() - permanentSlotIcons.size()) * (page - 1));

		if(slot < 0 || slot > getSize())
			return;

		Plugin plugin = (Plugin) TvheeAPIPlugin.getPlugin();

		SlotType slotType = SlotType.NORMAL_SLOT;

		if(isSlotFiller(slot))
			slotType = SlotType.FILLED_SLOT;
		else if(isPermanentItem(slot))
			slotType = SlotType.PERMANENT_SLOT;

		InventoryClickEvent optionClick = new InventoryClickEvent(this, page, new BukkitPlayer((BukkitPluginLoader) plugin, (org.bukkit.entity.Player) e.getWhoClicked()), e.getCurrentItem(), iconSlot, e.getAction(), e.getClick(), slotType);
		callClickEvent(optionClick);

		if(optionClick.willClose())
		{
			setAllowedToClose(true);
			final Player player = (Player) e.getWhoClicked();
			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, player::closeInventory, 1);
			destroy();
		}
	}

	private int getMaxPage()
	{
		int pages = 0;
		int maxContents = 0;

		for(Integer key : getUsedSlots())
		{
			if(key > maxContents)
				maxContents = key;
		}

		int contentsPerPage = getSize() - this.permanentSlotIcons.size();

		for(int i = maxContents; i > 0; i = i - contentsPerPage)
			pages++;

		return pages;
	}

	private ItemStack replacePageModifier(ItemStack item)
	{
		ItemStack newItem = item.clone();
		ItemMeta itemMeta = newItem.getItemMeta();
		String name = itemMeta.getDisplayName().replaceAll("%page%", String.valueOf(page));
		itemMeta.setDisplayName(name);
		newItem.setItemMeta(itemMeta);
		return newItem;
	}

	public enum PermanentItemType
	{
		NEXT_BUTTON,
		PREVIOUS_BUTTON,
		NORMAL;
	}
}
