package me.sul.crackshotaddition;

import com.shampaggon.crackshot.events.WeaponHitBlockEvent;
import me.sul.crackshotaddition.events.ProjectileBlockBreakEffectEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.material.MaterialData;

import java.util.ArrayList;

public class WeaponBlockBreakEffect implements Listener {

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onWeaponHitBlock(WeaponHitBlockEvent event) {
		if (event.getBlock().getType().equals(Material.AIR)) return;

		final ProjectileBlockBreakEffectEvent customEvent = new ProjectileBlockBreakEffectEvent(event.getPlayer(), event.getWeaponTitle());
        CrackShotAddition.getInstance().getServer().getPluginManager().callEvent((Event)customEvent);
        if (customEvent.isCancelled()) return;

		Location blockLoc = event.getBlock().getLocation().add(0.5, 0.5, 0.5);
		blockLoc.add(event.getProjectile().getVelocity().multiply(-1).normalize());
//		e.getBlock().getLocation().getWorld().spawnParticle(Particle.BLOCK_CRACK, blockLoc, 20, 0, 0, 0, 1, e.getBlock().getType(), true);		1.15
		event.getBlock().getLocation().getWorld().spawnParticle(Particle.BLOCK_CRACK, null, event.getPlayer(),
				blockLoc.getX(), blockLoc.getY(), blockLoc.getZ(),
				20, 0, 0, 0, 1,
				new MaterialData(event.getBlock().getType()), true);
	}
}