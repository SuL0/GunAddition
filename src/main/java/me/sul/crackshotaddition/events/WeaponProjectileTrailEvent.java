package me.sul.crackshotaddition.events;

import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;


public class WeaponProjectileTrailEvent extends Event implements Cancellable {
	private static final HandlerList handlers = new HandlerList();
	private Entity entity;
	private boolean cancelled = false;
	private Particle particle;
	private String weaponTitle;
	
	public WeaponProjectileTrailEvent(Entity entity, String weaponTitle, Particle particle) {
		this.particle = particle;
		this.weaponTitle = weaponTitle;
		this.entity = entity;
	}
	public WeaponProjectileTrailEvent(Entity entity, String weaponTitle, String particle) {
		this.particle = Particle.valueOf(particle.toUpperCase());
		this.weaponTitle = weaponTitle;
		this.entity = entity;
	}
	public void setParticle(Particle particle) {
		this.particle = particle;
	}
	public void setParticle(String particle) {
		this.particle = Particle.valueOf(particle.toUpperCase());
	}
	public Entity getEntity() {
		return entity;
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
