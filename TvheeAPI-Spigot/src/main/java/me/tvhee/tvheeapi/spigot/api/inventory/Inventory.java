package me.tvhee.tvheeapi.spigot.api.inventory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import me.tvhee.tvheeapi.api.TvheeAPI;
import me.tvhee.tvheeapi.api.chat.MessageType;
import me.tvhee.tvheeapi.api.chat.Component;
import me.tvhee.tvheeapi.api.exception.TvheeAPIException;
import me.tvhee.tvheeapi.api.player.Player;
import me.tvhee.tvheeapi.api.plugin.TvheeAPIPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

public abstract class Inventory implements Listener
{
	private final List<InventoryClickHandler> handlers = new ArrayList<>();
	private final TvheeAPI api = TvheeAPI.getInstance();
	private final Component name;
	private final int size;
	private final Player player;
	private final Map<Integer, ItemStack> contents = new HashMap<>();
	private int maxStackSize = 64;
	private ItemStack slotFiller = new ItemStack(Material.AIR);
	private InventoryType inventoryType;
	private boolean allowedToClose = true;
	private boolean allowedToReplace = false;

	Inventory(Component name, InventoryType inventoryType, Player player)
	{
		this.name = name;
		this.inventoryType = inventoryType;
		this.player = player;
		this.size = inventoryType.getDefaultSize();

		api.getPluginManager().registerListener(this);
	}

	Inventory(Component name, int size, Player player)
	{
		this.name = name;
		this.size = size;
		this.player = player;

		api.getPluginManager().registerListener(this);
	}

	public void open()
	{
		org.bukkit.inventory.Inventory filled = fillInventory(getBukkitInventory(), new ArrayList<>());
		Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin) TvheeAPIPlugin.getPlugin(), () -> ((org.bukkit.entity.Player) player.getOriginal()).openInventory(filled), 1);
	}

	public void addClickHandler(InventoryClickHandler handler)
	{
		this.handlers.add(handler);
	}

	public void removeClickHandler(InventoryClickHandler handler)
	{
		this.handlers.remove(handler);
	}

	public Component getName()
	{
		return name;
	}

	public Player getPlayer()
	{
		return player;
	}

	public InventoryType getInventoryType()
	{
		return inventoryType;
	}

	public void setAllowedToClose(boolean allowedToClose)
	{
		this.allowedToClose = allowedToClose;
	}

	public void setAllowedToReplace(boolean allowedToReplace)
	{
		this.allowedToReplace = allowedToReplace;
	}

	public boolean isAllowedToClose()
	{
		return allowedToClose;
	}

	public boolean isAllowedToReplace()
	{
		return allowedToReplace;
	}

	public int getSize()
	{
		return size;
	}

	public int getContentSize()
	{
		return this.contents.size();
	}

	public int getMaxStackSize()
	{
		return maxStackSize;
	}

	public void setMaxStackSize(int maxStackSize)
	{
		this.maxStackSize = maxStackSize;
	}

	public ItemStack getItem(int slot)
	{
		return this.contents.get(slot);
	}

	public ItemStack getSlotFiller()
	{
		return slotFiller;
	}

	public boolean isSlotFiller(int slot)
	{
		return !this.contents.containsKey(slot);
	}

	public void setItem(int slot, Component name, Material material, List<Component> lore)
	{
		setItem(slot, buildStack(name, new ItemStack(material), lore));
	}

	public void setItem(int slot, Component name, ItemStack item, List<Component> lore)
	{
		setItem(slot, buildStack(name, item, lore));
	}

	public void setItem(int slot, Component name, ItemStack item)
	{
		setItem(slot, buildStack(name, item));
	}

	public void setItem(int slot, Component name, Material material)
	{
		setItem(slot, buildStack(name, new ItemStack(material)));
	}

	public void setItem(int slot, ItemStack item)
	{
		if(item.getAmount() > maxStackSize)
			throw new TvheeAPIException(getClass(), "setSlot", "The item's amount is higher then the maxStackSize!");

		if(slot > this.size && !(this instanceof PaginatedInventory))
			throw new TvheeAPIException(getClass(), "setSlot", "The slots must be less then the size!");

		if(this.contents.containsKey(slot))
			throw new TvheeAPIException(getClass(), "setSlot", "You can only set one item and one permanent per slot!");

		if(this.slotFiller.isSimilar(item))
			throw new TvheeAPIException(getClass(), "setSlot", item + " is exactly the same as the slot filler!");

		if(this.contents.size() + 1 > size)
			throw new TvheeAPIException(getClass(), "setSlot", "There are too many items!");

		this.contents.put(slot, item);
	}

	public void setSlotFiller(Component name, Material material, List<Component> lore)
	{
		setSlotFiller(buildStack(name, new ItemStack(material), lore));
	}

	public void setSlotFiller(Component name, ItemStack item, List<Component> lore)
	{
		setSlotFiller(buildStack(name, item, lore));
	}

	public void setSlotFiller(Component name, ItemStack item)
	{
		setSlotFiller(buildStack(name, item));
	}

	public void setSlotFiller(Component name, Material material)
	{
		setSlotFiller(buildStack(name, new ItemStack(material)));
	}

	public void setSlotFiller(ItemStack slotFiller)
	{
		this.slotFiller = slotFiller;
	}

	public Set<Integer> getUsedSlots()
	{
		return this.contents.keySet();
	}

	public void addItems(ItemStack... items)
	{
		int max = this instanceof PaginatedInventory ? Integer.MAX_VALUE : size;
		int itemCount = 0;

		for(int i = 0; i < max; i++)
		{
			if(itemCount == items.length)
				break;

			if(getItem(i) == null)
			{
				setItem(i, items[itemCount]);
				itemCount++;
			}
		}
	}

	public void removeItems(ItemStack... items)
	{
		for(ItemStack item : items)
		{
			for(int slot : this.contents.keySet())
			{
				if(getItem(slot).equals(item))
				{
					this.contents.remove(slot);
					break;
				}
			}
		}
	}

	public Map<Integer, ItemStack> getContents()
	{
		return this.contents;
	}

	public void setContents(Map<Integer, ItemStack> contents)
	{
		for(Entry<Integer, ItemStack> entry : contents.entrySet())
			setItem(entry.getKey(), entry.getValue());
	}

	public boolean contains(Material material)
	{
		for(ItemStack item : this.contents.values())
		{
			if(item.getType() == material)
				return true;
		}

		return false;
	}

	public boolean containsExact(Material material, int amount)
	{
		if(amount < 1)
			return true;

		int found = 0;

		for(ItemStack item : this.contents.values())
		{
			if(item.getType() == material)
				found++;
		}

		return found == amount;
	}

	public boolean containsAtLeast(Material material, int amount)
	{
		if(amount < 1)
			return true;

		int found = 0;

		for(ItemStack item : this.contents.values())
		{
			if(item.getType() == material)
				found++;
		}

		return found >= amount;
	}

	public boolean contains(ItemStack itemStack)
	{
		for(ItemStack item : this.contents.values())
		{
			if(item.isSimilar(itemStack))
				return true;
		}

		return false;
	}

	public boolean containsExact(ItemStack itemStack, int amount)
	{
		if(amount < 1)
			return true;

		int found = 0;

		for(ItemStack item : this.contents.values())
		{
			if(item.equals(itemStack))
				found++;
		}

		return found == amount;
	}

	public boolean containsAtLeast(ItemStack itemStack, int amount)
	{
		if(amount < 1)
			return true;

		int found = 0;

		for(ItemStack item : this.contents.values())
		{
			if(item.equals(itemStack))
				found++;
		}

		return found >= amount;
	}

	public Map<Integer, ItemStack> all(ItemStack item)
	{
		Map<Integer, ItemStack> foundItems = new HashMap<>();

		for(Entry<Integer, ItemStack> entry : this.contents.entrySet())
		{
			if(entry.getValue().equals(item))
				foundItems.put(entry.getKey(), entry.getValue());
		}

		return foundItems;
	}

	public Map<Integer, ItemStack> all(Material material)
	{
		Map<Integer, ItemStack> foundItems = new HashMap<>();

		for(Entry<Integer, ItemStack> entry : this.contents.entrySet())
		{
			if(entry.getValue().getType() == material)
				foundItems.put(entry.getKey(), entry.getValue());
		}

		return foundItems;
	}

	public int first(ItemStack item)
	{
		for(Entry<Integer, ItemStack> entry : this.contents.entrySet())
		{
			if(entry.getValue().equals(item))
				return entry.getKey();
		}

		return -1;
	}

	public int first(Material material)
	{
		for(Entry<Integer, ItemStack> entry : this.contents.entrySet())
		{
			if(entry.getValue().getType() == material)
				return entry.getKey();
		}

		return -1;
	}

	public int firstEmpty()
	{
		for(Integer slot : this.contents.keySet())
		{
			if(!this.contents.containsKey(slot + 1))
				return slot + 1;
		}

		return -1;
	}

	public int lastFilled()
	{
		List<Integer> keys = new ArrayList<>(this.contents.keySet());
		Collections.reverse(keys);

		for(Integer slot : keys)
		{
			if(!this.contents.containsKey(slot - 1))
				return slot - 1;
		}

		return -1;
	}

	public boolean isEmpty()
	{
		return this.contents.isEmpty();
	}

	public void remove(Material material)
	{
		List<Integer> toRemoveKeys = new ArrayList<>();

		for(Entry<Integer, ItemStack> entry : this.contents.entrySet())
		{
			if(entry.getValue().getType() == material)
				toRemoveKeys.add(entry.getKey());
		}

		for(Integer key : toRemoveKeys)
			this.contents.remove(key);
	}

	public void remove(ItemStack item)
	{
		List<Integer> toRemoveKeys = new ArrayList<>();

		for(Entry<Integer, ItemStack> entry : this.contents.entrySet())
		{
			if(entry.getValue().equals(item))
				toRemoveKeys.add(entry.getKey());
		}

		for(Integer key : toRemoveKeys)
			this.contents.remove(key);
	}

	public void clear()
	{
		this.contents.clear();
	}

	protected void callClickEvent(me.tvhee.tvheeapi.spigot.api.inventory.InventoryClickEvent event)
	{
		for(InventoryClickHandler handler : this.handlers)
			handler.onInventoryClick(event);
	}

	protected void destroy()
	{
		HandlerList.unregisterAll(this);
		this.handlers.clear();
	}

	protected abstract void onInventoryClick(InventoryClickEvent e);

	protected abstract org.bukkit.inventory.Inventory getBukkitInventory();

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBukkitInventoryClick(InventoryClickEvent e)
	{
		if(inventoryEquals(e.getView()) && e.getWhoClicked().equals(this.player))
			onInventoryClick(e);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBukkitInventoryClose(InventoryCloseEvent e)
	{
		if(inventoryEquals(e.getView()) && e.getPlayer().equals(this.player))
		{
			if(!allowedToClose)
				open();
			else
				destroy();
		}
	}

	protected org.bukkit.inventory.Inventory fillInventory(org.bukkit.inventory.Inventory inventory, List<Integer> excluded)
	{
		if(getSlotFiller() != null)
		{
			for(int i = 0; i < inventory.getSize(); i++)
			{
				if(excluded.contains(i))
					continue;

				if(inventory.getContents()[i] == null)
					inventory.setItem(i, getSlotFiller());
			}
		}

		return inventory;
	}

	protected ItemStack buildStack(Component name, ItemStack material, List<Component> lore)
	{
		ItemStack itemStack = material.clone();
		ItemMeta meta = itemStack.getItemMeta();
		meta.setDisplayName(name.toLegacyText(null));

		List<String> formattedLore = new ArrayList<>();

		for(Component line : lore)
			formattedLore.add(line.toLegacyText(null));

		meta.setLore(formattedLore);
		itemStack.setItemMeta(meta);
		return itemStack;
	}

	protected ItemStack buildStack(Component name, ItemStack material)
	{
		ItemStack itemStack = material.clone();
		ItemMeta meta = itemStack.getItemMeta();
		meta.setDisplayName(name.toLegacyText(null));
		itemStack.setItemMeta(meta);
		return itemStack;
	}

	private boolean inventoryEquals(InventoryView view)
	{
		char[] chars = ChatColor.stripColor(view.getTitle()).toCharArray();
		char[] org = ChatColor.stripColor(name.toLegacyText(MessageType.INVENTORY_LINE)).toCharArray();

		for(int i = 0; i < chars.length; i++)
		{
			if(chars[i] != org[i])
				return false;
		}

		return true;
	}

	public enum SlotType
	{
		PERMANENT_SLOT, FILLED_SLOT, NORMAL_SLOT
	}
}
