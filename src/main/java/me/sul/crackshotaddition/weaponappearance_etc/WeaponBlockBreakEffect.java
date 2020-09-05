package me.sul.crackshotaddition.weaponappearance_etc;

import com.shampaggon.crackshot.CSDirector;
import com.shampaggon.crackshot.events.WeaponHitBlockEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class WeaponBlockBreakEffect implements Listener {
	@EventHandler
	public void onWeaponHitBlock(WeaponHitBlockEvent e) {
		if (e.getBlock().getType().equals(Material.AIR)) return;

		boolean b_blockBreakEffect = CSDirector.getInstance().getBoolean(e.getWeaponTitle() + ".Addition.Block_Break_Effect");
		if (b_blockBreakEffect) {
			Location projStruckLoc = calcProjectileStruckLocation(e.getProjectile());
			Player p = e.getPlayer();

			List<Player> nearbyPlayers = new ArrayList<>();
			nearbyPlayers.add(p);
			nearbyPlayers.addAll(Bukkit.getServer().getOnlinePlayers().stream().filter(loopP -> !loopP.equals(p) && loopP.getWorld().equals(p.getWorld()) && loopP.getLocation().distance(p.getLocation()) <= 100).collect(Collectors.toList()));
			e.getBlock().getLocation().getWorld().spawnParticle(Particle.BLOCK_CRACK, nearbyPlayers, e.getPlayer(),
					projStruckLoc.getX(), projStruckLoc.getY(), projStruckLoc.getZ(),
					20, 0, 0, 0, 1,
					new MaterialData(e.getBlock().getType()), true);  // 1.15버전은 new MaterialData(...) -> e.getBlock.getType() 만 해도됨

			// TODO: 블럭 부숴지는 소리 추가
		}
	}

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