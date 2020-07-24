package me.sul.crackshotaddition;

import com.shampaggon.crackshot.events.WeaponShootEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;

import java.util.Collections;

public class WeaponMuzzleFlash implements Listener {
    private int RIGHT = 90;
    private int LEFT = -90;


    @EventHandler
    public void onShoot(WeaponShootEvent e) {
        if (e.getWeaponTitle().equals("AK-47_1")) {
            spawnMuzzleFlashParticle(e.getPlayer(), RIGHT, 0.18F, -0.1F, 1.25F);
        }
    }

    private void spawnMuzzleFlashParticle(Player p, int rightOrLeft, float multiplyToRightSideVec, float sumToY, float multiplyToForwardSideVec) {
        double playerYaw = (p.getLocation().getYaw() + 90.0F + rightOrLeft) * Math.PI / 180.0D;

        Vector toRightSideVec = new Vector(Math.cos(playerYaw)*multiplyToRightSideVec, sumToY, Math.sin(playerYaw)*multiplyToRightSideVec);
        Vector toForwardSideVec = p.getLocation().getDirection().normalize().multiply(multiplyToForwardSideVec);

        // 벡터 보정
        double value;
        if (toForwardSideVec.getY() > 0) {
            value = 0.185*Math.pow(toForwardSideVec.getY()/toForwardSideVec.length(), 3D);
            toForwardSideVec.add(toForwardSideVec.clone().multiply(new Vector(0, -1, 0)).normalize().multiply(value));
        } else {
            value = 0.14*Math.pow(Math.abs(toForwardSideVec.getY())/toForwardSideVec.length(), 2D);
            toForwardSideVec.add(toForwardSideVec.clone().multiply(new Vector(-1, 0, -1)).normalize().multiply(value));
        }

        // 최종 위치
        Location loc = p.getEyeLocation().toVector().add(toRightSideVec).add(toForwardSideVec).toLocation(p.getWorld());
        loc.getWorld().spawnParticle(Particle.FLAME, Collections.singletonList(p), p, loc.getX(), loc.getY(), loc.getZ(), 1, 0, 0, 0, 0, null);
    }
}
