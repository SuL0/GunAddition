package me.sul.crackshotaddition;

import com.shampaggon.crackshot.events.WeaponShootEvent;
import me.sul.crackshotaddition.events.ProjectileTrailEvent;
import net.minecraft.server.v1_12_R1.Packet;
import net.minecraft.server.v1_12_R1.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_12_R1.PacketPlayOutWorldParticles;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class WeaponProjectileTrail implements Listener {
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onShoot(WeaponShootEvent e) {
		projectileTrail(e.getProjectile(), e.getWeaponTitle(), e.getPlayer());
	}
	
	public void projectileTrail(Entity projectile, String weaponTitle, Player shooter) {
		Particle particle;
		
		hideEntity(projectile);
		
		final ProjectileTrailEvent event = new ProjectileTrailEvent(shooter, weaponTitle, Particle.CRIT);
        CrackShotAddition.getInstance().getServer().getPluginManager().callEvent((Event)event);
        if (event.isCancelled()) return;
        particle = event.getParticle();

		new BukkitRunnable() {
			PacketPlayOutWorldParticles packet;
			Location loc;
			Location previousLoc;
			boolean skipFirstLoc = true;
			@Override
			public void run() {
				if (projectile.isValid() == false) cancel();
				loc = projectile.getLocation();
				if (!skipFirstLoc) {
					loc.getWorld().spawnParticle(particle, loc, 1);
					loc.getWorld().spawnParticle(particle, loc.add(previousLoc).multiply(0.5), 1);
				} else {
					skipFirstLoc = false;
				}

				if (loc.equals(previousLoc)) {
					projectile.remove();
					cancel();
				}
				previousLoc = loc;
			}
		}.runTaskTimer((Plugin) CrackShotAddition.getInstance(), 0, 1);
	}
	
	public static void hideEntity(Entity entity) {
		PacketPlayOutEntityDestroy packetPlayOutEntityDestroy = new PacketPlayOutEntityDestroy(new int[]{entity.getEntityId()});
		for (Player player : entity.getWorld().getPlayers()) {
            ((CraftPlayer)player).getHandle().playerConnection.sendPacket((Packet)packetPlayOutEntityDestroy);
        }
	}
}
