package me.sul.crackshotaddition;

import com.shampaggon.crackshot.events.WeaponHitBlockEvent;
import me.sul.crackshotaddition.events.ProjectileBlockBreakEffectEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
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
	public void onWeaponHitBlock(WeaponHitBlockEvent e) {
		final ProjectileBlockBreakEffectEvent event = new ProjectileBlockBreakEffectEvent(e.getPlayer(), e.getWeaponTitle());
        CrackShotAddition.getInstance().getServer().getPluginManager().callEvent((Event)event);
        if (event.isCancelled()) return;

		Location blockLoc = e.getBlock().getLocation().add(0.5, 0.5, 0.5);
		blockLoc.add(e.getProjectile().getVelocity().multiply(-1).normalize());
//		e.getBlock().getLocation().getWorld().spawnParticle(Particle.BLOCK_CRACK, blockLoc, 20, 0, 0, 0, 1, e.getBlock().getType());
		e.getBlock().getLocation().getWorld().spawnParticle(Particle.BLOCK_CRACK, (ArrayList<Player>)Bukkit.getServer().getOnlinePlayers(), e.getPlayer(),
				blockLoc.getX(), blockLoc.getY(), blockLoc.getZ(),
				20, 0, 0, 0, 1,
				new MaterialData(e.getBlock().getType()), true);
	}
}