package me.tvhee.tvheeapi.spigot.api.menu;

import me.tvhee.tvheeapi.api.plugin.TvheeAPIPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;

public final class MenuListener implements Listener
{
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e)
	{
		Inventory inventory = e.getClickedInventory();
		Player player = (Player) e.getWhoClicked();

		if(inventory == null || !(inventory.getHolder() instanceof Menu))
			return;

		Menu menu = (Menu) inventory.getHolder();
		int slot = e.getRawSlot();
		int page = -1;

		if(!menu.isAllowedToReplace())
			e.setCancelled(true);

		if(e.getCurrentItem() == null || slot < 0 || slot > menu.getSize())
			return;

		Menu.SlotType slotType = Menu.SlotType.NORMAL_SLOT;

		if(menu.isSlotFiller(slot))
			slotType = Menu.SlotType.FILLED_SLOT;

		if(menu instanceof MenuPaginated)
		{
			MenuPaginated paginated = (MenuPaginated) menu;
			page = paginated.getPage();

			if(slotType == Menu.SlotType.NORMAL_SLOT)
				slot = paginated.getNormalItemSlot(slot);
			else if(paginated.isNextClick(slot) && page + 1 < paginated.getMaxPages())
				page++;
			else if(paginated.isPreviousClick(slot) && page - 1 > 1)
				page--;

			if(paginated.isPermanentItem(slot))
				slotType = Menu.SlotType.PERMANENT_SLOT;
		}

		menu.callEvent(new MenuEvent(menu, page, player, e.getCurrentItem(), slot, e.getAction(), e.getClick(), slotType));
	}

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent e)
	{
		Inventory inventory = e.getInventory();
		Player player = (Player) e.getPlayer();

		if(!(inventory.getHolder() instanceof Menu))
			return;

		Menu menu = (Menu) inventory.getHolder();
		int page = -1;

		if(!menu.isAllowedToClose())
		{
			Bukkit.getScheduler().runTaskLater((Plugin) TvheeAPIPlugin.getPlugin(), () -> menu.open(player), 1L);
			return;
		}

		if(menu instanceof MenuPaginated)
			page = ((MenuPaginated) menu).getPage();

		menu.callEvent(new MenuEvent(menu, page, player));
	}
}
