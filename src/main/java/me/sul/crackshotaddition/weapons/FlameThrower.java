package me.sul.crackshotaddition.weapons;

import com.shampaggon.crackshot.events.WeaponHitBlockEvent;
import com.shampaggon.crackshot.events.WeaponShootEvent;
import me.sul.crackshotaddition.CrackShotAddition;
import me.sul.crackshotaddition.events.CrackShotProjectileBlockBreakEffectEvent;
import me.sul.crackshotaddition.events.CrackShotProjectileTrailEvent;
import net.minecraft.server.v1_12_R1.PacketPlayOutWorldParticles;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class FlameThrower implements Listener {
    public static final String FLAMETHROWER = "N화염방사기";

    @EventHandler
    public void onBlockBreakEffectEvent(CrackShotProjectileBlockBreakEffectEvent e) {
        if (e.getWeaponTitle().equals(FLAMETHROWER)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onProjectileTrailEvent(CrackShotProjectileTrailEvent e) {
        if (e.getWeaponTitle().equals(FLAMETHROWER)) {
            e.setParticle(Particle.FLAME);
        }
    }

    @EventHandler
    public void onShoot(WeaponShootEvent e) {
        if (e.getWeaponTitle().equals(FLAMETHROWER)) {
            Entity proj = e.getProjectile();

            new BukkitRunnable() {
                PacketPlayOutWorldParticles packet;
                Location loc;
                Location previousLoc;
                boolean skipFirstLoc = true;
                int passedTick = 0;

                @Override
                public void run() {
                    if (proj.isValid() == false) cancel();
                    loc = proj.getLocation();
                    if (!skipFirstLoc) {
                        loc.getWorld().spawnParticle(Particle.LAVA, loc,1);
                    } else {
                        skipFirstLoc = false;
                    }

                    if (loc.equals(previousLoc)) {
                        cancel();
                    }
                    if (passedTick >= 10) {
                        e.getProjectile().remove();
                        cancel();
                    }

                    passedTick += 2;
                    previousLoc = loc;
                }
            }.runTaskTimer((Plugin) CrackShotAddition.getInstance(), 0, 2);
        }
    }

    @EventHandler
    public void onWeaponHitBlock(WeaponHitBlockEvent e) {
        if (e.getWeaponTitle().equals(FLAMETHROWER)) {
            Block block = e.getBlock();
            if (block.getRelative(BlockFace.UP).getType().equals(Material.AIR)) {
                block.getRelative(BlockFace.UP).setType(Material.FIRE);

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (block.getRelative(BlockFace.UP).getType().equals(Material.FIRE)) {
                            block.getRelative(BlockFace.UP).setType(Material.AIR);
                        }
                    }
                }.runTaskLater(CrackShotAddition.getInstance(), 20);
            }
        }
    }
}