package me.tvhee.tvheeapi.spigot.api.inventory;

import me.tvhee.tvheeapi.api.chat.MessageType;
import me.tvhee.tvheeapi.api.chat.Component;
import me.tvhee.tvheeapi.api.plugin.TvheeAPIPlugin;
import me.tvhee.tvheeapi.spigot.core.BukkitPlayer;
import me.tvhee.tvheeapi.spigot.core.BukkitPluginLoader;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.plugin.Plugin;

public final class InventoryMenu extends Inventory implements Listener
{
	public InventoryMenu(Component name, InventoryType inventoryType, me.tvhee.tvheeapi.api.player.Player player)
	{
		super(name, inventoryType, player);
	}

	public InventoryMenu(Component name, int size, me.tvhee.tvheeapi.api.player.Player player)
	{
		super(name, size, player);
	}

	@Override
	public org.bukkit.inventory.Inventory getBukkitInventory()
	{
		org.bukkit.inventory.Inventory inventory = getInventoryType() == null ? Bukkit.createInventory(((org.bukkit.entity.Player) getPlayer().getOriginal()), getSize(), getName().toLegacyText(MessageType.INVENTORY_LINE)) : Bukkit.createInventory(((org.bukkit.entity.Player) getPlayer().getOriginal()), getInventoryType(), getName().toLegacyText(MessageType.INVENTORY_LINE));

		for(int i = 0; i < getContentSize(); i++)
		{
			if(getItem(i) != null)
				inventory.setItem(i, getItem(i));
		}

		return inventory;
	}

	@Override
	public void onInventoryClick(InventoryClickEvent e)
	{
		if(!isAllowedToReplace())
			e.setCancelled(true);

		int slot = e.getRawSlot();

		if(e.getCurrentItem() == null || (e.getCurrentItem().getType() != Material.AIR && !e.getCurrentItem().getType().isItem()))
			return;

		if(slot < 0 || slot > getSize())
			return;

		Plugin plugin = (Plugin) TvheeAPIPlugin.getPlugin();

		SlotType slotType = SlotType.NORMAL_SLOT;

		if(isSlotFiller(slot))
			slotType = SlotType.FILLED_SLOT;

		me.tvhee.tvheeapi.spigot.api.inventory.InventoryClickEvent optionClick = new me.tvhee.tvheeapi.spigot.api.inventory.InventoryClickEvent(this, 0, new BukkitPlayer((BukkitPluginLoader) plugin, (org.bukkit.entity.Player) e.getWhoClicked()), e.getCurrentItem(), slot, e.getAction(), e.getClick(), slotType);
		callClickEvent(optionClick);

		if(optionClick.willClose())
		{
			setAllowedToClose(true);
			final Player player = (Player) e.getWhoClicked();
			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, player::closeInventory, 1);
			destroy();
		}
	}
}
