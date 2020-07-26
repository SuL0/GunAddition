package me.sul.crackshotaddition;

import com.shampaggon.crackshot.events.WeaponHitBlockEvent;
import me.sul.crackshotaddition.events.CrackShotProjectileBlockBreakEffectEvent;
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
import java.util.List;

public class WeaponBlockBreakEffect implements Listener {

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onWeaponHitBlock(WeaponHitBlockEvent e) {
		if (e.getBlock().getType().equals(Material.AIR)) return;

		final CrackShotProjectileBlockBreakEffectEvent customEvent = new CrackShotProjectileBlockBreakEffectEvent(e.getPlayer(), e.getWeaponTitle());
        CrackShotAddition.getInstance().getServer().getPluginManager().callEvent((Event)customEvent);
        if (customEvent.isCancelled()) return;

		Location blockLoc = e.getBlock().getLocation().add(0.5, 0.5, 0.5);
		blockLoc.add(e.getProjectile().getVelocity().multiply(-1).normalize());

		List<Player> nearbyPlayers = new ArrayList<>();
		nearbyPlayers.add(e.getPlayer());
		for (Player loopPlayer : Bukkit.getServer().getOnlinePlayers()) {
			if (loopPlayer.equals(e.getPlayer())) continue;
			if (loopPlayer.getLocation().distance(blockLoc) <= 100) {
				nearbyPlayers.add(loopPlayer);
			}
		}
		e.getBlock().getLocation().getWorld().spawnParticle(Particle.BLOCK_CRACK, nearbyPlayers, e.getPlayer(),
				blockLoc.getX(), blockLoc.getY(), blockLoc.getZ(),
				20, 0, 0, 0, 1,
				new MaterialData(e.getBlock().getType()), true);  // 1.15버전은 new MaterialData(...) -> e.getBlock.getType() 만 해도됨
	}
}