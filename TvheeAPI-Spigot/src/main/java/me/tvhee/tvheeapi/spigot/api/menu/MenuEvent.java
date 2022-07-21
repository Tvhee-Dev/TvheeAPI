package me.tvhee.tvheeapi.spigot.api.menu;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;

public final class MenuEvent
{
	private final Menu menu;
	private final int page;
	private final Player player;
	private final ItemStack icon;
	private final int slot;
	private final InventoryAction action;
	private final ClickType click;
	private final Menu.SlotType slotType;
	private Menu redirect;
	private boolean closed;

	public MenuEvent(Menu menu, int lastPage, Player player)
	{
		this.menu = menu;
		this.icon = null;
		this.page = lastPage;
		this.player = player;
		this.slot = -1;
		this.action = InventoryAction.NOTHING;
		this.click = ClickType.UNKNOWN;
		this.slotType = Menu.SlotType.NORMAL_SLOT;
		this.closed = true;
	}

	public MenuEvent(Menu menu, int page, Player player, ItemStack icon, int slot, InventoryAction action, ClickType click, Menu.SlotType slotType)
	{
		this.menu = menu;
		this.page = page;
		this.player = player;
		this.icon = icon;
		this.slot = slot;
		this.action = action;
		this.click = click;
		this.slotType = slotType;
	}

	public Menu getInventory()
	{
		return menu;
	}

	//Counts from 1 - Integer.MAX_VALUE. -1 if not paginated
	public int getPage()
	{
		return page;
	}

	public Player getPlayer()
	{
		return player;
	}

	public ItemStack getItem()
	{
		return icon;
	}

	public int getSlot()
	{
		return slot;
	}

	public InventoryAction getAction()
	{
		return action;
	}

	public ClickType getClick()
	{
		return click;
	}

	public boolean willClose()
	{
		return closed;
	}

	public Menu.SlotType getSlotType()
	{
		return slotType;
	}

	public void setClosed(boolean close)
	{
		if(close && redirect != null)
			throw new IllegalArgumentException("You can't close the inventory because you are redirecting");

		this.closed = close;
	}

	public void setRedirect(Menu redirect)
	{
		this.redirect = redirect;
		setClosed(false);
	}

	public Menu getRedirect()
	{
		return redirect;
	}
}
