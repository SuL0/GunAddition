package me.sul.crackshotaddition;

import com.shampaggon.crackshot.events.WeaponShootEvent;
import me.sul.crackshotaddition.events.CrackShotProjectileTrailEvent;
import net.minecraft.server.v1_12_R1.PacketPlayOutEntityDestroy;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class WeaponProjectileTrail implements Listener {
	public static Particle DEFAULT_PARTICLE = Particle.SWEEP_ATTACK; // SUSPENDED, WATER_BUBBLE 리팩입히면 괜찮을 듯
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onShoot(WeaponShootEvent e) {
		projectileTrail(e.getProjectile(), e.getWeaponTitle(), e.getPlayer());
	}

	private final float DISTORTION_DISTANCE = 60;
	public void projectileTrail(Entity projectile, String weaponTitle, Player shooter) {
		Particle particle;
		
		hideEntity(projectile);
		
		final CrackShotProjectileTrailEvent event = new CrackShotProjectileTrailEvent(shooter, weaponTitle, DEFAULT_PARTICLE);
        CrackShotAddition.getInstance().getServer().getPluginManager().callEvent((Event)event);
        if (event.isCancelled()) return;
        particle = event.getParticle();

		new BukkitRunnable() {
			final double playerYaw = (shooter.getLocation().getYaw() + 90.0F + 90) * Math.PI / 180.0D;
			final Vector toRightSideVec = new Vector(Math.cos(playerYaw)*0.5F, -0.2F, Math.sin(playerYaw)*0.5F);
			Location loc;
			Location previousLoc;
			int cnt = (int) DISTORTION_DISTANCE;
			boolean skipFirstLoc = true;

			@Override
			public void run() {
				if (!projectile.isValid()) cancel();
				loc = projectile.getLocation();
				if (!skipFirstLoc) {
					List<Player> nearbyPlayers = new ArrayList<>();
					nearbyPlayers.add(shooter);
					for (Player loopPlayer : Bukkit.getServer().getOnlinePlayers()) {
						if (loopPlayer.equals(shooter)) continue;
						if (loopPlayer.getLocation().distance(loc) <= 100) {
							nearbyPlayers.add(loopPlayer);
						}
					}

					loc = loc.clone().add(toRightSideVec.multiply((Math.max(cnt--, 0))/DISTORTION_DISTANCE)); // 총알 궤적 위치 왜곡   // loc에 바로 더하면 projectile에 더해짐
					loc.getWorld().spawnParticle(particle, nearbyPlayers, shooter, loc.getX(), loc.getY(), loc.getZ(), 1, 0, 0, 0, 0, null, true);  // extra가 속도

					Location backwardLoc1 = loc.clone().add(projectile.getVelocity().multiply(-1).multiply(0.33));
					loc.getWorld().spawnParticle(particle, nearbyPlayers, shooter, backwardLoc1.getX(), backwardLoc1.getY(), backwardLoc1.getZ(), 1, 0, 0, 0, 0, null, true);  // extra가 속도

					Location backwardLoc2 = loc.clone().add(projectile.getVelocity().multiply(-1).multiply(0.66));
					loc.getWorld().spawnParticle(particle, nearbyPlayers, shooter, backwardLoc2.getX(), backwardLoc2.getY(), backwardLoc2.getZ(), 1, 0, 0, 0, 0, null, true);  // extra가 속도


					// 청크에 projectile이 막혔을 시 projectile 삭제
					if (loc.distance(previousLoc) <= 0.1) {
						projectile.remove();
						cancel();
					}
				} else {
					skipFirstLoc = false;
				}
				previousLoc = loc;
			}
		}.runTaskTimer(CrackShotAddition.getInstance(), 0L, 1L);
	}
	
	public static void hideEntity(Entity entity) {
		PacketPlayOutEntityDestroy packetPlayOutEntityDestroy = new PacketPlayOutEntityDestroy(entity.getEntityId());
		for (Player player : entity.getWorld().getPlayers()) {
            ((CraftPlayer)player).getHandle().playerConnection.sendPacket(packetPlayOutEntityDestroy);
        }
	}
}