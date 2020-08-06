package me.sul.crackshotaddition.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class WeaponsProjectilesBlockBreakEffectEvent extends Event implements Cancellable {
	private static final HandlerList handlers = new HandlerList();
	private Player player;
	private String weaponTitle;
	private boolean cancelled = false;
	
	public WeaponsProjectilesBlockBreakEffectEvent(Player player, String weaponTitle) {
		this.player = player;
		this.weaponTitle = weaponTitle;
	}
	public Player getPlayer() {
		return player;
	}
	public String getWeaponTitle() {
		return weaponTitle;
	}
	public boolean isCancelled() {
		return cancelled;
	}

	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

	public HandlerList getHandlers() {
		return handlers;
	}
	public static HandlerList getHandlerList()
	{
		return handlers;
	}
}
