package me.sul.crackshotaddition.events;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
public class WeaponProjectileTrailEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	private final Entity entity;
	private @Setter Particle particle;
	private final String weaponTitle;
	
	public WeaponProjectileTrailEvent(Entity entity, String weaponTitle, Particle particle) {
		this.particle = particle;
		this.weaponTitle = weaponTitle;
		this.entity = entity;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	public static HandlerList getHandlerList()
	{
		return handlers;
	}

}
