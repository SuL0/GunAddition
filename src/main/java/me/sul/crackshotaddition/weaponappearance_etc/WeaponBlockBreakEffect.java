package me.sul.crackshotaddition.weaponappearance_etc;

import com.shampaggon.crackshot.events.WeaponHitBlockEvent;
import me.sul.crackshotaddition.CrackShotAddition;
import me.sul.crackshotaddition.events.WeaponsProjectilesBlockBreakEffectEvent;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class WeaponBlockBreakEffect implements Listener {
	@EventHandler
	public void onWeaponHitBlock(WeaponHitBlockEvent e) {
		if (e.getBlock().getType().equals(Material.AIR)) return;

		final WeaponsProjectilesBlockBreakEffectEvent customEvent = new WeaponsProjectilesBlockBreakEffectEvent(e.getPlayer(), e.getWeaponTitle()); // TODO: 파티클 여부는 무기.yml에 넣고 이벤트는 삭제하기
        CrackShotAddition.getInstance().getServer().getPluginManager().callEvent(customEvent);
        if (customEvent.isCancelled()) return;

		Location projStruckLoc = calcProjectileStruckLocation(e.getProjectile());

		List<Player> nearbyPlayers = new ArrayList<>();
		nearbyPlayers.add(e.getPlayer());
		for (Player loopPlayer : Bukkit.getServer().getOnlinePlayers()) {
			if (loopPlayer.equals(e.getPlayer())) continue;
			if (loopPlayer.getLocation().distance(projStruckLoc) <= 100) {
				nearbyPlayers.add(loopPlayer);
			}
		}
		e.getBlock().getLocation().getWorld().spawnParticle(Particle.BLOCK_CRACK, nearbyPlayers, e.getPlayer(),
				projStruckLoc.getX(), projStruckLoc.getY(), projStruckLoc.getZ(),
				20, 0, 0, 0, 1,
				new MaterialData(e.getBlock().getType()), true);  // 1.15버전은 new MaterialData(...) -> e.getBlock.getType() 만 해도됨

		// TODO: 블럭 부숴지는 소리 추가
	}


	// TODO: normalized된 Vector 더해서 블럭 찾고나면, 정확한 모서리 위치를 찾아야 함.
	private Location calcProjectileStruckLocation(Entity proj) {
		Location lastestLoc = proj.getLocation();
		Vector lastestVec = proj.getVelocity();

		Location tempLoc = lastestLoc.clone();
		Vector lastestNormalizedVec = lastestVec.clone().normalize();
		for (int i=1; i<=Math.ceil(lastestVec.length()); i++) {
			tempLoc.add(lastestNormalizedVec);
			if (isStruckableMaterial(tempLoc.getWorld().getBlockAt(tempLoc).getType())) {  // ProjectileHitEvent의 기준을 알아내야 함 -> 그냥 Proj가 죽었으면 호출됨
				return getBlockCornerLocation(tempLoc, lastestNormalizedVec.clone().multiply(-1));
			}
		}
		return new Location(proj.getWorld(), 0, 0, 0);
	}
	private Location getBlockCornerLocation(Location loc, Vector vec) {
		Material origMaterial = loc.getBlock().getType();
		final int maxI = 10; // 블럭을 10등분
		vec = vec.multiply(vec.length()/maxI);
		Location previousLoc = loc.clone();
		for (int i=1; i<=maxI+1; i++) {
			loc.add(vec);
			if (loc.getBlock().getType() != origMaterial) {
				return previousLoc;
			}
			previousLoc = loc.clone();
		}
		return new Location(loc.getWorld(), 0, 0, 0);
	}

	private boolean isStruckableMaterial(Material material) {
		switch(material) {
			case AIR:
			case WATER:
			case LAVA:
				return false;
			default:
				return true;
		}
	}

//	private Sound getBlockBreakSound(Block block) {
//		WorldServer nmsWorld = ((CraftWorld) block.getWorld()).getHandle();
//		BlockPosition blockPosition = new BlockPosition(block.getX(), block.getY(), block.getZ());
//		nmsWorld.
//	}
}