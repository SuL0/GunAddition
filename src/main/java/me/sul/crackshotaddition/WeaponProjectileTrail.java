package me.sul.crackshotaddition;

import com.shampaggon.crackshot.events.WeaponShootEvent;
import me.sul.crackshotaddition.events.CrackShotProjectileTrailEvent;
import me.sul.customentities.events.CustomEntityShootEvent;
import net.minecraft.server.v1_12_R1.PacketPlayOutEntityDestroy;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class WeaponProjectileTrail implements Listener {
	public static Particle DEFAULT_PARTICLE = Particle.SWEEP_ATTACK; // SUSPENDED, WATER_BUBBLE 리팩입히면 괜찮을 듯
	private final int DISTORTION_DISTANCE = 60;
	private final float SHIFTVECTOR_LENGTH = 0.2F;


	public void onCustomEntityShoot(CustomEntityShootEvent e) {
		projectileTrail(e.getEntity(), e.getProjectile(), null);
	}
	public void onShoot(WeaponShootEvent e) {
		// TODO: 무기.yml에서 읽어오고, 매개변수에 파티클 타입 넣는 방식으로 변경
		projectileTrail(e.getPlayer(), e.getProjectile(), e.getWeaponTitle());
	}

	public void projectileTrail(Entity shooter, Entity projectile, String weaponTitle) {
		projectileTrail(shooter, projectile, weaponTitle, DEFAULT_PARTICLE);
	}
	public void projectileTrail(Entity shooter, Entity projectile, @Nullable String weaponTitle ,Particle particle) {
		hideEntity(projectile);
		if (weaponTitle != null) {
			final CrackShotProjectileTrailEvent event = new CrackShotProjectileTrailEvent(shooter, weaponTitle, DEFAULT_PARTICLE);
			CrackShotAddition.getInstance().getServer().getPluginManager().callEvent((Event) event);
			if (event.isCancelled()) return;
			particle = event.getParticle();
		}

		final Particle finalParticle = particle;
		new BukkitRunnable() {
			final double playerYaw = (shooter.getLocation().getYaw() + 90.0F + 90) * Math.PI / 180.0D;
			final Vector toRightSideVec = new Vector(Math.cos(playerYaw)*0.3F, -0.2F, Math.sin(playerYaw)*0.3F);
			Location loc;
			Location previousLoc;
			int cnt = DISTORTION_DISTANCE;
			boolean skipFirstLoc = true;

			@Override
			public void run() {
				if (!projectile.isValid()) cancel();
				loc = projectile.getLocation();
				if (!skipFirstLoc) {
					List<Player> nearbyPlayers = new ArrayList<>();
					if (shooter instanceof Player)
						nearbyPlayers.add((Player)shooter);
					for (Player loopPlayer : Bukkit.getServer().getOnlinePlayers()) {
						if (loopPlayer.equals(shooter)) continue;
						if (loopPlayer.getLocation().distance(loc) <= 100) {
							nearbyPlayers.add(loopPlayer);
						}
					}

					if (DebuggingCommand.distortion) {
						loc = loc.clone().add(toRightSideVec.multiply((Math.max(cnt--, 0)) / DISTORTION_DISTANCE)); // 총알 궤적 위치 왜곡   // loc에 바로 더하면 projectile에 더해짐
					}
					Location particleLoc = loc.clone();
					Vector shiftVector = projectile.getVelocity().clone().multiply(-1).multiply(SHIFTVECTOR_LENGTH);
					for (int i=0; i<1/SHIFTVECTOR_LENGTH; i++) {
						loc.getWorld().spawnParticle(finalParticle, nearbyPlayers, (shooter instanceof Player) ? (Player)shooter : null, particleLoc.getX(), particleLoc.getY(), particleLoc.getZ(), 1, 0, 0, 0, 0, null, true);  // extra가 속도
						particleLoc.add(shiftVector);
					}

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