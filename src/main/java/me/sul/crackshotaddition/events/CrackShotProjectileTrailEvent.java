package me.sul.crackshotaddition.events;

import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;


public class CrackShotProjectileTrailEvent extends Event implements Cancellable {
	private static final HandlerList handlers = new HandlerList();
	private Player player;
	private boolean cancelled = false;
	private Particle particle;
	private String weaponTitle;
	
	public CrackShotProjectileTrailEvent(Player player, String weaponTitle, Particle particle) {
		this.particle = particle;
		this.weaponTitle = weaponTitle;
		this.player = player;
	}
	public CrackShotProjectileTrailEvent(Player player, String weaponTitle, String particle) {
		this.particle = Particle.valueOf(particle.toUpperCase());
		this.weaponTitle = weaponTitle;
		this.player = player;
	}
	public void setParticle(Particle particle) {
		this.particle = particle;
	}
	public void setParticle(String particle) {
		this.particle = Particle.valueOf(particle.toUpperCase());
	}
	public Player getPlayer() {
		return player;
	}
	public String getWeaponTitle() {
		return weaponTitle;
	}
	public Particle getParticle() {
		return particle;
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
