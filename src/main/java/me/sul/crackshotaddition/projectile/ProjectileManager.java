package me.sul.crackshotaddition.projectile;

import me.sul.crackshotaddition.CrackShotAddition;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


// 현재 ProjectileManager은 객체 생성부분도 지워놨음
public class ProjectileManager {

	
	// 총알 스쳤을 때
	public void projectileGraze(Entity projectile, Player shooter) {
		new BukkitRunnable() {
			Location loc;
			List<Player> players;
			List<Player> excludePlayers = new ArrayList<Player>(Arrays.asList(shooter));
			@Override
			public void run() {
				if (projectile.isValid() == false) cancel();
				loc = projectile.getLocation();
				players = loc.getWorld().getPlayers();
				for (Player p: players) {
					if (p.getLocation().distance(loc) <= 3 && !excludePlayers.contains(p)) {
						excludePlayers.add(p);
						Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "rsp p " + p.getName() + " MASTER block.enchantment_table.use 1 0 false");
					}
				}
				
			}
		}.runTaskTimer((Plugin) CrackShotAddition.getInstance(), 0, 1);
	}
}
