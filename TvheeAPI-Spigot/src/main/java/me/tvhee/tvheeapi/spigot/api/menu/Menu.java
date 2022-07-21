package me.tvhee.tvheeapi.spigot.api.menu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import me.tvhee.tvheeapi.api.chat.Component;
import me.tvhee.tvheeapi.api.plugin.TvheeAPIPlugin;
import me.tvhee.tvheeapi.spigot.api.items.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public abstract class Menu implements InventoryHolder
{
	private final Component name;
	private final Inventory inventory;
	private final Map<Integer, ItemStack> contents = new HashMap<>();
	private int maxStackSize = 64;
	private ItemStack slotFiller = new ItemStack(Material.AIR);
	private boolean allowedToClose = true;
	private boolean allowedToReplace = false;

	public Menu(Component name, InventoryType inventoryType)
	{
		this.name = name;
		this.inventory = Bukkit.createInventory(this, inventoryType, name.toLegacyText());
	}

	public Menu(Component name, int size)
	{
		this.name = name;
		this.inventory = Bukkit.createInventory(this, getSize(size), name.toLegacyText());
	}

	public static boolean hasInventory(Player player)
	{
		return getInventory(player) != null;
	}

	public static Menu getInventory(Player player)
	{
		return getInventory(player, Menu.class);
	}

	public static <T extends Menu> T getInventory(Player player, Class<T> holderClass)
	{
		InventoryView openInventory = player.getOpenInventory();

		if(openInventory != null)
		{
			Inventory topInventory = openInventory.getTopInventory();

			if(topInventory != null)
			{
				Menu menu = (Menu) topInventory.getHolder();

				if(holderClass.isInstance(menu))
					return (T) menu;
			}
		}

		return null;
	}

	protected abstract void onMenuEvent(MenuEvent event);

	public void open(Player player)
	{
		if(isViewing(player))
			throw new IllegalArgumentException("Player is already viewing the inventory!");

		update();

		Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin) TvheeAPIPlugin.getPlugin(), () -> player.openInventory(getInventory()), 1L);
	}

	public boolean isViewing(Player player)
	{
		InventoryView playerInventory = player.getOpenInventory();
		return playerInventory != null && playerInventory.getTopInventory() != null && playerInventory.getTopInventory().getHolder().equals(this);
	}

	public void update()
	{
		Inventory inventory = getInventory();

		for(int slot : getUsedSlots())
		{
			if(hasItem(slot))
				inventory.setItem(slot, getItem(slot));
		}
	}

	public Component getName()
	{
		return name;
	}

	public InventoryType getInventoryType()
	{
		return inventory.getType();
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
		return inventory.getSize();
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

	public void setItem(int slot, ItemBuilder item)
	{
		setItem(slot, item.toItem());
	}

	public void setItem(int slot, ItemStack item)
	{
		if(item == null)
			return;

		if(item.getAmount() > maxStackSize)
			throw new IllegalArgumentException("The item's amount is higher then the maxStackSize!");

		if(slot >= inventory.getSize() && !(this instanceof MenuPaginated))
			throw new IllegalArgumentException("The slots must be less then the getSize!");

		if(this.slotFiller.isSimilar(item))
			throw new IllegalArgumentException(item + " is the same as the slot filler!");

		if(this.contents.size() + 1 > inventory.getSize() && !(this instanceof MenuPaginated))
			throw new IllegalArgumentException("There are too many items!");

		this.contents.put(slot, item);
	}

	public void setSlotFiller(ItemBuilder itemBuilder)
	{
		setSlotFiller(itemBuilder.toItem());
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
		int max = this instanceof MenuPaginated ? Integer.MAX_VALUE : inventory.getSize();
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

	public boolean hasItem(int slot)
	{
		return this.contents.containsKey(slot);
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

	@Override
	public final Inventory getInventory()
	{
		return inventory;
	}

	final void callEvent(MenuEvent event)
	{
		onMenuEvent(event);

		if(event.willClose() || event.getRedirect() != null)
		{
			Player player = event.getPlayer();
			setAllowedToClose(true);
			Bukkit.getScheduler().runTaskLater((Plugin) TvheeAPIPlugin.getPlugin(), player::closeInventory, 1L);

			if(event.getRedirect() != null)
				event.getRedirect().open(player);
		}
	}

	private int getSize(int size)
	{
		while(size % 9 != 0)
			size++;

		if(size > 54)
			size = 54;

		return size;
	}

	public enum SlotType
	{
		PERMANENT_SLOT, FILLED_SLOT, NORMAL_SLOT
	}
}
